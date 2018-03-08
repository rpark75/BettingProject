package fr.project.app.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.project.app.entities.FootballMatchEntity;
import fr.project.app.entities.FootballMatchSimplifiedEntity;

@Service
public class ParisService {

	private static final Logger betLogger = LoggerFactory.getLogger(ParisService.class);
	
	@Autowired
	private MatchUtils matchUtils;
	
	/**
	 * Permet de d�terminer si le tips (conseil de pari du site) s'est r�v�l� exact.
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
	 * Permet de d�terminer la probabilit� gagnante.
	 * 
	 * @param elements
	 *            : contenu de la page.
	 * @return : la proba gagnante. Si 0 est retourn�, erreur de parsing.
	 */
	public int getProbaGagnante(Elements elements) {

		/* V�rification de succ�s sur proba1 */
		if (elements.get(7).hasClass("w") || elements.get(7).hasClass("q") || elements.get(7).hasClass("f")) {
			return Integer.parseInt(elements.get(7).text());
		}

		/* V�rification de succ�s sur probaX */
		if (elements.get(8).hasClass("w") || elements.get(8).hasClass("q") || elements.get(8).hasClass("f")) {
			return Integer.parseInt(elements.get(8).text());
		}

		/* V�rification de succ�s sur proba2 */
		if (elements.get(9).hasClass("w") || elements.get(9).hasClass("q") || elements.get(9).hasClass("f")) {
			return Integer.parseInt(elements.get(9).text());
		}

		return 0;
	}

	/**
	 * Permet de d�terminer si la probabilit� la plus haute �tait gagnante.
	 * 
	 * @param match
	 * @return : true : succ�s.
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
	 * Permet de d�terminer la c�te gagnante.
	 * 
	 * @param elements
	 *            : contenu de la page.
	 * @return : la c�te gagnante. Si 0 est retourn�, erreur de parsing.
	 */
	public double getOddGagnant(Elements elements) {

		/* V�rification de succ�s sur proba1 */
		if (elements.get(12).hasClass("f")) {
			return Double.parseDouble(elements.get(12).text());
		}

		/* V�rification de succ�s sur probaX */
		if (elements.get(13).hasClass("f")) {
			return Double.parseDouble(elements.get(13).text());
		}

		/* V�rification de succ�s sur proba2 */
		if (elements.get(14).hasClass("f")) {
			return Double.parseDouble(elements.get(14).text());
		}

		return 0;
	}
	
	
	/**
	 * Permet de d�terminer si la plus basse c�te �tait gagnate.
	 * 
	 * @param match
	 * @return true : plus basse c�te gagnante
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
	
	/**
	 * Retourne les leagues pr�sentes dans la liste des matchs r�cup�r�s.
	 * @param matchList
	 * @return : liste des leagues pr�sentes.
	 */
	public ArrayList<String> getAllLeaguesFromMatchList(ArrayList<FootballMatchEntity> matchList){
		ArrayList<String> leaguesList = new ArrayList<>();
		
		for(FootballMatchEntity match : matchList) {
			String league = match.getLeague();
			leaguesList.add(league);
		}
		
		return leaguesList;
	}
	
	/**
	 * M�thode permettant de v�rifier si une league est pr�sente dans la liste de leagues de confiance.
	 * @param leaguesList
	 * @return : Liste des leagues de confiance pr�sentes.
	 */
	public ArrayList<String> checkIfLeaguesInList(ArrayList<String> leaguesList){
		String[] targetLeagues = {"ASC","BES","BGa","BYS","CBC","CLS","DES","ISS","JPS","PTS","UAS","MEC","NG1","CHC","AZ1","THL","GRC","ALC","G3F","ILT","SKC","FIC","ILC","EE1","NLC","AMC","AZC","LVC","TH1","GR2","UZ1","SAL","BEC","ASN","BCa","ITC","RS1","FY1","SG1","CNC","FRC","FGC","MD1","G3A","AM1","AU1","CA1","G3D","SC2","CZ1","BY1","CAC","MDC","HR1","BA1","DEC","CH2","KE1","BE2","AGC","NL1","COC","BG2","RSC","CY1","GR3","G3E","GR1","JPL","NO1","ME1","IS1","IT1","SC3","HRC","SCP","HU1","ISL","US1","PT1","SCC","DK1","EN2","TR1","ENP","FR1","ENL","PT2","NL2","MXC"};
		
		ArrayList<String> containedLeagues = new ArrayList<>();
		
		betLogger.debug("leaguesList : {}",leaguesList.toString());
		betLogger.debug("Taille de la liste de leagues pr�sentes : {}", leaguesList.size());
		
		for(String target : targetLeagues) {
			betLogger.debug(String.valueOf(leaguesList.contains(target)));
			if(leaguesList.contains(target)) {
				containedLeagues.add(target);
			}
		}
		
		return containedLeagues;
	}
	
	/**
	 * M�thode permettant de v�rifier si une league est pr�sente dans la liste de leagues de confiance.
	 * @param leaguesList
	 * @return : Liste des leagues de confiance pr�sentes.
	 */
	public ArrayList<FootballMatchEntity> getConfianceMatchs(ArrayList<FootballMatchEntity> confianceMatchsList){
		String[] targetLeagues = {"ASC","BES","BGa","BYS","CBC","CLS","DES","ISS","JPS","PTS","UAS","MEC","NG1","CHC","AZ1","THL","GRC","ALC","G3F","ILT","SKC","FIC","ILC","EE1","NLC","AMC","AZC","LVC","TH1","GR2","UZ1","SAL","BEC","ASN","BCa","ITC","RS1","FY1","SG1","CNC","FRC","FGC","MD1","G3A","AM1","AU1","CA1","G3D","SC2","CZ1","BY1","CAC","MDC","HR1","BA1","DEC","CH2","KE1","BE2","AGC","NL1","COC","BG2","RSC","CY1","GR3","G3E","GR1","JPL","NO1","ME1","IS1","IT1","SC3","HRC","SCP","HU1","ISL","US1","PT1","SCC","DK1","EN2","TR1","ENP","FR1","ENL","PT2","NL2","MXC"};
		
		ArrayList<FootballMatchEntity> containedLeagues = new ArrayList<>();
		
		
		for(FootballMatchEntity match : confianceMatchsList) {
			if(Arrays.asList(targetLeagues).contains(match.getLeague())) {
				containedLeagues.add(match);
			}
		}
		betLogger.info("Taille de la liste de matchs de confiance : {}",containedLeagues.size());
		return containedLeagues;
	}
	
	/**
	 * M�thode permettant d'estimer les valeurs simplifi�es d'un match pour calcul de gain ult�rieur.
	 * @param matchsList
	 * @return
	 */
	public ArrayList<FootballMatchSimplifiedEntity> getSimplifiedMatches(ArrayList<FootballMatchEntity> matchsList){
		ArrayList<FootballMatchSimplifiedEntity> simplifiedList = new ArrayList<>();
		
		for(FootballMatchEntity match : matchsList) {
			FootballMatchSimplifiedEntity sMatch = new FootballMatchSimplifiedEntity();
			
			sMatch.setDate(match.getDate());
			sMatch.setLeague(match.getLeague());
			sMatch.setMatch(match.getMatch());
			sMatch.setTip(match.getTip());
			
			/* Ajout de la proba pr�vue d'apr�s le tip */
			sMatch = matchUtils.getProbaFromTip(sMatch, match);
			
			/* Ajout de la c�te pr�vue d'apr�s le tip */
			sMatch = matchUtils.getOddFromTip(sMatch, match);
			
			sMatch.setTipSuccess(match.isTipSuccess());
			
			/* D�termination de la proba du meilleur choix sur le nombre de buts */
			sMatch = matchUtils.getGoalsOddFromGoalsTip(sMatch, match);
			
			sMatch.setGoalsSuccess(match.isGoalsSuccess());
			
			simplifiedList.add(sMatch);
		}
		
		betLogger.info("Taille de la liste de matchs simplifi�s : {}",simplifiedList.size());
		
		return simplifiedList;
	}
	
	/**
	 * M�thode permettant de pr�voir les simplifiedMatchs � parier selon la liste de targetLeagues. 
	 * @param sMatchsList
	 * @param targetLeagues
	 * @return
	 */
	public ArrayList<FootballMatchSimplifiedEntity> getMatchsAParier(ArrayList<FootballMatchSimplifiedEntity> sMatchsList, 
			ArrayList<String> targetLeagues){
		ArrayList<FootballMatchSimplifiedEntity> targetMatchs = new ArrayList<>();
		
		for(FootballMatchSimplifiedEntity sMatch : sMatchsList) {
			if(targetLeagues.contains(sMatch.getLeague())) {
				targetMatchs.add(sMatch);
			}
		}
		betLogger.info("Taille de la liste de matchs � parier : {}",targetMatchs.size());
		
		return targetMatchs;
	}
}
