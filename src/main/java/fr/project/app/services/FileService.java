package fr.project.app.services;

import java.beans.PropertyDescriptor;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;

import org.springframework.stereotype.Service;

import fr.project.app.entities.FootballMatchEntity;

/**
 * Service contenant les méthodes de traitement et création de fichiers.
 * 
 * @author RPARK-ADVANCED
 *
 */
@Service
public class FileService {

	private static final String CSV_SEPARATOR = ";";
	private static final Class<FootballMatchEntity> FOOTBALL_MATCH_CLASS = FootballMatchEntity.class;

	/**
	 * Permet d'écrire la liste de matchs dans un fichier csv.
	 * 
	 * @param matchList
	 *            : liste des matchs.
	 * @throws Exception
	 */
	public void writeToCSV(ArrayList<FootballMatchEntity> matchList) throws Exception {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream("C:/Users/RPARK-ADVANCED/Documents/Projets/BettingProject/test.csv"), "UTF-8"));

		/* Rajout des colonnes */
		bw.write(genererHeadersCSV().toString());
		bw.newLine();

		/* Remplissage du CSV */
		for (FootballMatchEntity match : matchList) {
			StringBuffer activeLine = new StringBuffer();

			for (Field field : FOOTBALL_MATCH_CLASS.getDeclaredFields()) {
				Object attribut = new PropertyDescriptor(field.getName(), FootballMatchEntity.class).getReadMethod()
						.invoke(match);

				activeLine.append(attribut);
				activeLine.append(CSV_SEPARATOR);
			}

			bw.write(activeLine.toString());
			bw.newLine();

		}

		bw.flush();
		bw.close();
	}

	/**
	 * Permet de générer le nom des colonnes dynamiquement.
	 * 
	 * @return : StringBuffer à rajouter lors de l'écriture du CSV
	 */
	public static StringBuffer genererHeadersCSV() {
		StringBuffer headersCSV = new StringBuffer();
		FootballMatchEntity match = new FootballMatchEntity();
		Class<?> matchClazz = match.getClass();

		for (Field field : matchClazz.getDeclaredFields()) {

			/* Permet de générer le nom de colonne, avec majuscule première lettre */
			headersCSV.append(field.getName().replaceFirst(".", (field.getName().charAt(0) + "").toUpperCase()));
			headersCSV.append(CSV_SEPARATOR);
		}

		return headersCSV;
	}
}
