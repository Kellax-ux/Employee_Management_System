package com.company.ems;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/** Creates ONE SessionFactory (reads hibernate.cfg.xml) and shares it. */
public class HibernateUtil {

    private static final SessionFactory sessionFactory =
            new Configuration().configure().buildSessionFactory();

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
