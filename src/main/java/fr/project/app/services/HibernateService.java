package fr.project.app.services;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import fr.project.app.entities.FootballMatchEntity;

@Service
public class HibernateService {

	private static final Logger betLogger = LoggerFactory.getLogger(HibernateService.class);
	
	public SessionFactory getSessionFactory() {
		betLogger.trace("Chargement de la config Hibernate..");
		SessionFactory factory = new Configuration().configure().addPackage("fr.project.app.entities").addAnnotatedClass(FootballMatchEntity.class).buildSessionFactory();
		
		return factory;
	}
	
	public void closeAll(SessionFactory factory, Session session) {
		betLogger.trace("Cloture du stream bdd...");
		session.close();
		factory.close();
		betLogger.trace("Stream bdd clos.");
	}
	
}
