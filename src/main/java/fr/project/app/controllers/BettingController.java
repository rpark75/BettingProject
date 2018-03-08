package fr.project.app.controllers;

import java.net.URL;
import java.util.ArrayList;

import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import fr.project.app.dao.FootballMatchDao;
import fr.project.app.entities.FootballMatchEntity;
import fr.project.app.entities.FootballMatchSimplifiedEntity;
import fr.project.app.services.FileService;
import fr.project.app.services.HtmlService;
import fr.project.app.services.ParisService;

@Controller
public class BettingController {

	private static final Logger betLogger = LoggerFactory.getLogger(BettingController.class);

	@Autowired
	private HtmlService htmlService;

	@Autowired
	private FileService fileService;

	@Autowired
	private FootballMatchDao matchDao;
	
	@Autowired
	private ParisService parisService;

	/**
	 * M�thode pour mettre les r�sultats d'un jour dans un CSV.
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public Response test() throws Exception {
		betLogger.info("Requ�te de test re�ue.");

		/* R�cup�re le contenu de la page */
		URL url = new URL("http://www.prosoccer.gr/en/2018/03/soccer-predictions-2018-03-05.html");
		Elements rows = htmlService.getContentOfUrlPage(url.toString());
 
		/* D�duit la date, et �crit le CSV */
		String date = StringUtils.substringBetween(url.getPath(), "predictions-", ".html");
		ArrayList<FootballMatchEntity> matchsList = htmlService.getListFromPageContent(rows, date);
		fileService.writeToCSV(matchsList);

		betLogger.info("Fin de la requ�te de test.");
		return Response.status(200).build();
	}

	/**
	 * M�thode pour alimenter la base en donn�es sur un intervalle de dates.
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/bddTest", method = RequestMethod.GET)
	public Response bddTest() throws Exception {
		betLogger.info("Requ�te de test de bdd re�ue.");

		/* G�n�rer les dates � parser entre un intervalle donn� */
		ArrayList<URL> urlList = htmlService.getListOfURLs("2017-01-01", "2018-02-24");

		for (URL url : urlList) {
			betLogger.info("Traitement de la date : \n {}", url);
			
			/* R�cup�re le contenu de la page */
			Elements rows = htmlService.getContentOfUrlPage(url.toString());
			String date = StringUtils.substringBetween(url.getPath(), "predictions-", ".html");
			ArrayList<FootballMatchEntity> matchsList = htmlService.getListFromPageContent(rows, date);

			/* Insert en bdd. */
			betLogger.debug("Lancement de l'insert.");
			matchDao.insertAll(matchsList);
			betLogger.debug("Fin de l'insert.");
		}

		return Response.status(200).build();
	}
	
	/**
	 * M�thode pour exporter le contenu de la BDD en CSV
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/importFromBdd", method = RequestMethod.GET)
	public Response bddToCSV() throws Exception{
		betLogger.info("Requ�te de r�cup�ration des matchs en base re�ue.");
		ArrayList<FootballMatchEntity> matchsList = matchDao.selectAll();
		
		betLogger.info("Taille de la liste � transformer en CSV : {}",matchsList.size());
		fileService.writeToCSV(matchsList);
		
		
		betLogger.info("Fin de la requ�te de r�cup�ration.");
		return Response.status(200).build();
	}
	
	/**
	 * Permet de d�duire les matchs � parier pour le lendemain.
	 * @return
	 * @throws Exception
	 */
//	@RequestMapping(value = "/predictionLendemain", method = RequestMethod.POST)
//	public Response prevoirPariLendemain(@RequestBody String url) throws Exception{
//		betLogger.info("Requete sur la prediction pour le lendemain re�ue.");
//		betLogger.info("Contenu de la requ�te : {}", url);
//		
//		return Response.status(200).build();
//	}
	@RequestMapping(value = "/predictionLendemainTest", method = RequestMethod.GET)
	public Response prevoirPariLendemain() throws Exception {
		betLogger.info("Requ�te sur la pr�diction pour le lendemain re�ue.");
		
		URL url = new URL("http://www.prosoccer.gr/en/2018/03/soccer-predictions-2018-03-06.html");
		Elements rows = htmlService.getContentOfUrlPage(url.toString());
		String date = StringUtils.substringBetween(url.getPath(), "predictions-", ".html");
		
		ArrayList<FootballMatchEntity> matchsList = htmlService.getListFromPageContent(rows, date);
		ArrayList<String> leaguesList = parisService.getAllLeaguesFromMatchList(matchsList);
		ArrayList<String> leaguesAParier = parisService.checkIfLeaguesInList(leaguesList);
		
		/* D�terminer les matchs � jouer en fonction des leagues de confiance */
		ArrayList<FootballMatchSimplifiedEntity> sMatchsList = parisService.getSimplifiedMatches(matchsList);
		ArrayList<FootballMatchSimplifiedEntity> targetMatchs = parisService.getMatchsAParier(sMatchsList, leaguesAParier);
		
		/* Ecriture dans un CSV */
		fileService.writeSimplifiedToCSV(targetMatchs);
		
		betLogger.info("Fin de la requ�te.");
		return Response.status(200).build();
	}
	
	/**
	 * M�thode pour mettre les r�sultats "de confiance" d'un jour dans un CSV.
	 * La liste des leagues de confiance est en dur.
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/resultatsConfiance", method = RequestMethod.GET)
	public Response resultatsConfiance() throws Exception {
		betLogger.info("Requ�te de r�sultat sur league de confiance re�ue.");

		/* R�cup�re le contenu de la page */
		URL url = new URL("http://www.prosoccer.gr/en/2018/03/soccer-predictions-2018-03-01.html");
		Elements rows = htmlService.getContentOfUrlPage(url.toString());

		/* D�duit la date, et �crit le CSV */
		String date = StringUtils.substringBetween(url.getPath(), "predictions-", ".html");
		ArrayList<FootballMatchEntity> matchsList = htmlService.getListFromPageContent(rows, date);
		ArrayList<FootballMatchEntity> matchsConfianceList = parisService.getConfianceMatchs(matchsList);
		
		fileService.writeToCSV(matchsConfianceList);

		betLogger.info("Fin de la requ�te de test.");
		return Response.status(200).build();
	}
	
	/**
	 * M�thode pour pr�voir les gains sur les paris de la veille, dans un CSV.
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/gainsVeille", method = RequestMethod.GET)
	public Response gainsVeille() throws Exception {
		betLogger.info("Requ�te de r�sultat du calcul de gains de la veille.");

		/* R�cup�re le contenu de la page */
		URL url = new URL("http://www.prosoccer.gr/en/2018/03/soccer-predictions-2018-03-03.html");
		Elements rows = htmlService.getContentOfUrlPage(url.toString());

		/* D�duit la date, et �crit le CSV */
		String date = StringUtils.substringBetween(url.getPath(), "predictions-", ".html");
		ArrayList<FootballMatchEntity> matchsList = htmlService.getListFromPageContent(rows, date);
		ArrayList<FootballMatchEntity> confianceMatchList = parisService.getConfianceMatchs(matchsList);
		ArrayList<FootballMatchSimplifiedEntity> simplifiedMatchList = parisService.getSimplifiedMatches(confianceMatchList);
		
		betLogger.info("Taille de la liste de matchs simplifi�s de confiance de la veille : {}",simplifiedMatchList.size());
		fileService.writeSimplifiedToCSV(simplifiedMatchList);
		
		betLogger.info("Fin de la requ�te de test.");
		return Response.status(200).build();
	}
}
