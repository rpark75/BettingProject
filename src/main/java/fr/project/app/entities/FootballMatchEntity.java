package fr.project.app.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

/**
 * Objet comportant toutes les informations sur un match.
 * 
 * @author RPARK-ADVANCED
 *
 */
@Component
@Entity
@Table(name = "footballMatch")
public class FootballMatchEntity {

	@Id
	@GeneratedValue
	private int id;

	@Column
	private String league;

	@Column
	private String match;

	@Column
	private int proba1;

	@Column
	private int probaX;

	@Column
	private int proba2;

	@Column
	private int probaGagnante;

	@Column
	private boolean highestProbaGagnante;

	@Column
	private String tip;

	@Column
	private boolean tipSuccess;

	@Column
	private double odd1;

	@Column
	private double oddX;

	@Column
	private double odd2;

	@Column
	private double oddGagnant;

	@Column
	private boolean lowestOddSuccess;

	@Column
	private int over50odd;

	@Column
	private int under50odd;

	/**
	 * Permet d'indiquer que la prédiction sur le nombre de but (inférieur ou sup à
	 * 2.5) est correcte.
	 */
	@Column
	private boolean goalsSuccess;

	@Column
	private String site;

	@Column
	private String date;

	public FootballMatchEntity() {
		super();
	}

	public FootballMatchEntity(int id, String league, String match, int proba1, int probaX, int proba2,
			int probaGagnante, boolean highestProbaGagnante, String tip, boolean tipSuccess, double odd1, double oddX,
			double odd2, double oddGagnant, boolean lowestOddSuccess, int over50odd, int under50odd, boolean goalsSuccess,
			String site, String date) {
		super();
		this.id = id;
		this.league = league;
		this.match = match;
		this.proba1 = proba1;
		this.probaX = probaX;
		this.proba2 = proba2;
		this.probaGagnante = probaGagnante;
		this.highestProbaGagnante = highestProbaGagnante;
		this.tip = tip;
		this.tipSuccess = tipSuccess;
		this.odd1 = odd1;
		this.oddX = oddX;
		this.odd2 = odd2;
		this.oddGagnant = oddGagnant;
		this.lowestOddSuccess = lowestOddSuccess;
		this.over50odd = over50odd;
		this.under50odd = under50odd;
		this.goalsSuccess = goalsSuccess;
		this.site = site;
		this.date = date;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLeague() {
		return league;
	}

	public void setLeague(String league) {
		this.league = league;
	}

	public String getMatch() {
		return match;
	}

	public void setMatch(String match) {
		this.match = match;
	}

	public int getProba1() {
		return proba1;
	}

	public void setProba1(int proba1) {
		this.proba1 = proba1;
	}

	public int getProbaX() {
		return probaX;
	}

	public void setProbaX(int probaX) {
		this.probaX = probaX;
	}

	public int getProba2() {
		return proba2;
	}

	public void setProba2(int proba2) {
		this.proba2 = proba2;
	}

	public int getProbaGagnante() {
		return probaGagnante;
	}

	public void setProbaGagnante(int probaGagnante) {
		this.probaGagnante = probaGagnante;
	}

	public boolean isHighestProbaGagnante() {
		return highestProbaGagnante;
	}

	public void setHighestProbaGagnante(boolean highestProbaGagnante) {
		this.highestProbaGagnante = highestProbaGagnante;
	}

	public String getTip() {
		return tip;
	}

	public void setTip(String tip) {
		this.tip = tip;
	}

	public boolean isTipSuccess() {
		return tipSuccess;
	}

	public void setTipSuccess(boolean tipSuccess) {
		this.tipSuccess = tipSuccess;
	}

	public double getOdd1() {
		return odd1;
	}

	public void setOdd1(double odd1) {
		this.odd1 = odd1;
	}

	public double getOddX() {
		return oddX;
	}

	public void setOddX(double oddX) {
		this.oddX = oddX;
	}

	public double getOdd2() {
		return odd2;
	}

	public void setOdd2(double odd2) {
		this.odd2 = odd2;
	}

	public double getOddGagnant() {
		return oddGagnant;
	}

	public void setOddGagnant(double oddGagnant) {
		this.oddGagnant = oddGagnant;
	}

	public boolean isLowestOddSuccess() {
		return lowestOddSuccess;
	}

	public void setLowestOddSuccess(boolean lowestOddSuccess) {
		this.lowestOddSuccess = lowestOddSuccess;
	}

	public int getOver50odd() {
		return over50odd;
	}

	public void setOver50odd(int over50odd) {
		this.over50odd = over50odd;
	}

	public int getUnder50odd() {
		return under50odd;
	}

	public void setUnder50odd(int under50odd) {
		this.under50odd = under50odd;
	}

	public boolean isGoalsSuccess() {
		return goalsSuccess;
	}

	public void setGoalsSuccess(boolean goalsSuccess) {
		this.goalsSuccess = goalsSuccess;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "FootballMatchEntity [id=" + id + ", league=" + league + ", match=" + match + ", proba1=" + proba1
				+ ", probaX=" + probaX + ", proba2=" + proba2 + ", probaGagnante=" + probaGagnante
				+ ", highestProbaGagnante=" + highestProbaGagnante + ", tip=" + tip + ", tipSuccess=" + tipSuccess
				+ ", odd1=" + odd1 + ", oddX=" + oddX + ", odd2=" + odd2 + ", oddGagnant=" + oddGagnant
				+ ", lowestOddSuccess=" + lowestOddSuccess + ", over50odd=" + over50odd + ", under50odd=" + under50odd
				+ ", goalsSuccess=" + goalsSuccess + ", site=" + site + ", date=" + date + "]";
	}

}