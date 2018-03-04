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
import fr.project.app.services.FileService;
import fr.project.app.services.HtmlService;

@Controller
public class BettingController {

	private static final Logger betLogger = LoggerFactory.getLogger(BettingController.class);

	@Autowired
	private HtmlService htmlService;

	@Autowired
	private FileService fileService;

	@Autowired
	private FootballMatchDao matchDao;

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public Response test() throws Exception {
		betLogger.info("Requ�te de test re�ue.");

		/* R�cup�re le contenu de la page */
		URL url = new URL("http://www.prosoccer.gr/en/2018/02/soccer-predictions-2018-02-24.html");
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
	
}
