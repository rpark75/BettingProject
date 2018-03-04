package fr.project.app.dao;

import java.util.ArrayList;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import fr.project.app.controllers.BettingController;
import fr.project.app.entities.FootballMatchEntity;
import fr.project.app.services.HibernateService;

@Repository
public class FootballMatchDao {
	
	private static final Logger betLogger = LoggerFactory.getLogger(BettingController.class);

	@Autowired
	HibernateService hibernateService;

	/**
	 * Methode pour ins�rer un match en bdd.
	 * @param match
	 */
	public void insertOne(FootballMatchEntity match) {
		betLogger.debug("Insertion en base du match {}",match.getMatch());
		
		SessionFactory factory = hibernateService.getSessionFactory();
		Session session = factory.openSession();
		Transaction tx = session.beginTransaction();

		session.save(match);
		tx.commit();

		hibernateService.closeAll(factory, session);
		
		betLogger.info("Fin de la requ�te d'insertion.");
	}

	/**
	 * M�thode pour ins�rer une liste de matchs en bdd.
	 * @param matchsList
	 */
	public void insertAll(ArrayList<FootballMatchEntity> matchsList) {
		betLogger.debug("Insertion en base des matchs. Total � ins�rer : {}",matchsList.size());
		
		SessionFactory factory = hibernateService.getSessionFactory();
		Session session = factory.openSession();
		Transaction tx = session.beginTransaction();

		for(int i = 0; i<matchsList.size(); i++) {
			session.save(matchsList.get(i));	
		}
		
		
		tx.commit();

		hibernateService.closeAll(factory, session);
		
		betLogger.debug("Fin de la requ�te d'insertion.");
	}
	
	/**
	 * M�thode pour r�cup�rer la liste des matchs en base.
	 * @return
	 * @throws Exception
	 */
	public ArrayList<FootballMatchEntity> selectAll() throws Exception {
		ArrayList<FootballMatchEntity> matchsList = new ArrayList<>();
		
		betLogger.debug("R�cup�ration des matchs en base...");
		SessionFactory factory = hibernateService.getSessionFactory();
		Session session = factory.openSession();
		Transaction tx = session.beginTransaction();
		
		matchsList = (ArrayList<FootballMatchEntity>) session.createQuery("FROM FootballMatchEntity").list();
		
		tx.commit();
		
		hibernateService.closeAll(factory, session);
		betLogger.debug("Fin de la requ�te SELECT.");
		
		return matchsList;
		
	}
}
