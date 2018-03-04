package fr.project.app.services;

import java.util.Objects;

import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import fr.project.app.entities.FootballMatchEntity;

@Service
public class ParisService {

	/**
	 * Permet de déterminer si le tips (conseil de pari du site) s'est révélé exact.
	 * 
	 * @param rawTipResult
	 * @return : true, exact ; false, incorrect.
	 */
	public boolean getTipSuccess(String rawTipResult) {
		if (rawTipResult.contains("c")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Permet de déterminer la probabilité gagnante.
	 * 
	 * @param elements
	 *            : contenu de la page.
	 * @return : la proba gagnante. Si 0 est retourné, erreur de parsing.
	 */
	public int getProbaGagnante(Elements elements) {

		/* Vérification de succès sur proba1 */
		if (elements.get(7).hasClass("w") || elements.get(7).hasClass("q") || elements.get(7).hasClass("f")) {
			return Integer.parseInt(elements.get(7).text());
		}

		/* Vérification de succès sur probaX */
		if (elements.get(8).hasClass("w") || elements.get(8).hasClass("q") || elements.get(8).hasClass("f")) {
			return Integer.parseInt(elements.get(8).text());
		}

		/* Vérification de succès sur proba2 */
		if (elements.get(9).hasClass("w") || elements.get(9).hasClass("q") || elements.get(9).hasClass("f")) {
			return Integer.parseInt(elements.get(9).text());
		}

		return 0;
	}

	/**
	 * Permet de déterminer si la probabilité la plus haute était gagnante.
	 * 
	 * @param match
	 * @return : true : succès.
	 */
	public boolean getHighestProbaSuccess(FootballMatchEntity match) {
		int proba1 = match.getProba1();
		int probaX = match.getProbaX();
		int proba2 = match.getProba2();
		
		if(Objects.equals(match.getProbaGagnante(),proba1) && proba1>probaX && proba1>proba2) {
			return true;
		}
		
		if(Objects.equals(match.getProbaGagnante(),probaX) && probaX>proba1 && probaX>proba2) {
			return true;
		}
		
		if(Objects.equals(match.getProbaGagnante(),proba2) && proba2>proba1 && proba2>probaX) {
			return true;
		}
		return false;
	}


	/**
	 * Permet de déterminer la côte gagnante.
	 * 
	 * @param elements
	 *            : contenu de la page.
	 * @return : la côte gagnante. Si 0 est retourné, erreur de parsing.
	 */
	public double getOddGagnant(Elements elements) {

		/* Vérification de succès sur proba1 */
		if (elements.get(12).hasClass("f")) {
			return Double.parseDouble(elements.get(12).text());
		}

		/* Vérification de succès sur probaX */
		if (elements.get(13).hasClass("f")) {
			return Double.parseDouble(elements.get(13).text());
		}

		/* Vérification de succès sur proba2 */
		if (elements.get(14).hasClass("f")) {
			return Double.parseDouble(elements.get(14).text());
		}

		return 0;
	}
	
	
	/**
	 * Permet de déterminer si la plus basse côte était gagnate.
	 * 
	 * @param match
	 * @return true : plus basse côte gagnante
	 */
	public boolean getLowestOddSuccess(FootballMatchEntity match) {
		double odd1 = match.getOdd1();
		double oddX = match.getOddX();
		double odd2 = match.getOdd2();
		
		if(Objects.equals(match.getOddGagnant(),odd1) && odd1<oddX && odd1<odd2) {
			return true;
		}
		
		if(Objects.equals(match.getOddGagnant(),oddX) && oddX<odd1 && oddX<odd2) {
			return true;
		}
		
		if(Objects.equals(match.getOddGagnant(),odd2) && odd2<odd1 && odd2<oddX) {
			return true;
		}
		
		return false;

	}
}
