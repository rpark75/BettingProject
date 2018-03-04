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
		betLogger.info("Requête de test reçue.");

		/* Récupère le contenu de la page */
		URL url = new URL("http://www.prosoccer.gr/en/2018/02/soccer-predictions-2018-02-24.html");
		Elements rows = htmlService.getContentOfUrlPage(url.toString());

		/* Déduit la date, et écrit le CSV */
		String date = StringUtils.substringBetween(url.getPath(), "predictions-", ".html");
		ArrayList<FootballMatchEntity> matchsList = htmlService.getListFromPageContent(rows, date);
		fileService.writeToCSV(matchsList);

		betLogger.info("Fin de la requête de test.");
		return Response.status(200).build();
	}

	/**
	 * Méthode pour alimenter la base en données sur un intervalle de dates.
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/bddTest", method = RequestMethod.GET)
	public Response bddTest() throws Exception {
		betLogger.info("Requête de test de bdd reçue.");

		/* Générer les dates à parser entre un intervalle donné */
		ArrayList<URL> urlList = htmlService.getListOfURLs("2017-01-01", "2018-02-24");

		for (URL url : urlList) {
			betLogger.info("Traitement de la date : \n {}", url);
			
			/* Récupère le contenu de la page */
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
	 * Méthode pour exporter le contenu de la BDD en CSV
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/importFromBdd", method = RequestMethod.GET)
	public Response bddToCSV() throws Exception{
		betLogger.info("Requête de récupération des matchs en base reçue.");
		ArrayList<FootballMatchEntity> matchsList = matchDao.selectAll();
		
		betLogger.info("Taille de la liste à transformer en CSV : {}",matchsList.size());
		fileService.writeToCSV(matchsList);
		
		
		betLogger.info("Fin de la requête de récupération.");
		return Response.status(200).build();
	}
	
}
