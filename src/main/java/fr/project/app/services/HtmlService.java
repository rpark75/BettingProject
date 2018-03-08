package fr.project.app.services;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.project.app.entities.FootballMatchEntity;

/**
 * Service contenant les m�thodes de traitement et r�cup�ration des donn�es du web.
 * @author RPARK-ADVANCED
 *
 */
@Service
public class HtmlService {
	
	private static final Logger betLogger = LoggerFactory.getLogger(HtmlService.class);

	@Autowired
	private ParisService parisService;
	
	/**
	 * Obtenir le contenu de la page html cible.
	 * 
	 * @param url
	 *            : page cible
	 * @return : objet Elements rempli.
	 * @throws Exception
	 */
	public Elements getContentOfUrlPage(String url) throws Exception {
		betLogger.debug("R�cup�ration des donn�es sources distantes...");
		Document doc = Jsoup.connect(url).get();
		return doc.select("tr");
	}

	/**
	 * Transformer les contenus de la page r�cup�r�s en Elements vers une liste de
	 * matchs.
	 * 
	 * @param rows
	 *            : objet Elements d'entr�e aliment� depuis une page.
	 * @return : ArrayList contenait les objets de match.
	 */
	public ArrayList<FootballMatchEntity> getListFromPageContent(Elements rows, String date) {
		ArrayList<FootballMatchEntity> matchList = new ArrayList<>();

		for (int i = 1; i < rows.size(); i++) {
			FootballMatchEntity match = new FootballMatchEntity();
			betLogger.debug("Incr�mentation.");
			Element row = rows.get(i);
			Elements elements = row.getAllElements();

			match.setId(i); 
			match.setDate(date);
			
			match.setLeague(elements.get(1).text());
			match.setMatch(elements.get(6).text());
			match.setProba1(Integer.parseInt(elements.get(7).text()));
			match.setProbaX(Integer.parseInt(elements.get(8).text()));
			match.setProba2(Integer.parseInt(elements.get(9).text()));
			
			
			if(elements.get(12).text().length()>1)	{
			match.setOdd1(Double.parseDouble(elements.get(12).text()));
			match.setOddX(Double.parseDouble(elements.get(13).text()));
			match.setOdd2(Double.parseDouble(elements.get(14).text()));
			} 
			
			match.setUnder50odd(Integer.parseInt(elements.get((elements.size() - 3)).text()));
			match.setOver50odd(Integer.parseInt(elements.get((elements.size() - 2)).text()));

			if (elements.hasClass("uop")) {
				match.setGoalsSuccess(true);
			} else {
				match.setGoalsSuccess(false);
			}

			
			
			/* Obtenir la valeur de la proba gagnante */
			match.setProbaGagnante(parisService.getProbaGagnante(elements));
			
			/* Savoir si la proba la plus haute �tait gagnante */
			match.setHighestProbaGagnante(parisService.getHighestProbaSuccess(match));
			
			/* Rajouter les infos sur le r�sultat du tip via le <span> */
			Elements spanList = elements.tagName("span");
			String tipResult = spanList.get(11).text();
			match.setTip(tipResult);
			match.setTipSuccess(parisService.getTipSuccess(tipResult));
			
			if(elements.get(12).text().length()>1)	{
			/* Obtenir la valeur de la c�te gagnante */
			match.setOddGagnant(parisService.getOddGagnant(elements));
			
			/* Savoir si la c�te la plus basse est gagnante */
			match.setLowestOddSuccess(parisService.getLowestOddSuccess(match));
			betLogger.trace("R�sultat success de la c�te la plus basse :" +match.isLowestOddSuccess());
			}
			
			betLogger.debug(match.toString());
			matchList.add(match);

		}
		
		betLogger.debug("Taille de la liste : " + matchList.size());
		return matchList;
	}
	
	
	

	/**
	 * M�thode permettant de g�n�rer la liste d'URL � parser en fonction de deux dates.
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 */
	public ArrayList<URL> getListOfURLs(String startDate, String endDate) throws Exception{
		ArrayList<URL> urlList = new ArrayList<>();
		
		/* G�n�rer la liste de dates � parser */
		LocalDate start = LocalDate.parse(startDate);
		LocalDate end = LocalDate.parse(endDate);
		List<LocalDate> totalDates = new ArrayList<>();
		while (!start.isAfter(end)) {
		    totalDates.add(start);
		    start = start.plusDays(1);
		}
		
		/* G�n�re les URLs qui en d�coulent */
		for(LocalDate date : totalDates) {
			String year = String.valueOf(date.getYear());
			String month = String.valueOf(date.getMonthValue());
			String day = String.valueOf(date.getDayOfMonth());
			
			if(Objects.equals(month.length(),1)){
				month = "0" + month;
			}
			
			if(Objects.equals(day.length(),1)){
				day = "0" + day;
			}
			
			String targetUrl = "http://www.prosoccer.gr/en/" + year + "/" + month + "/soccer-predictions-" 
					+ year + "-" + month + "-" + day + ".html";
			
			urlList.add(new URL(targetUrl));
		}
		
		betLogger.info("Nombre de dates � parser : {}", urlList.size());
		
		return urlList;
	}
}
