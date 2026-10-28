package com.company.ems;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.InputStream;
import java.util.Properties;

/** Creates ONE SessionFactory (reads hibernate.cfg.xml + db.properties). */
public class HibernateUtil {

    private static final SessionFactory sessionFactory = build();

    private static SessionFactory build() {
        try (InputStream in = HibernateUtil.class.getResourceAsStream("/db.properties")) {
            Properties p = new Properties();
            p.load(in);
            return new Configuration().configure()
                    .setProperty("hibernate.connection.url", p.getProperty("db.url"))
                    .setProperty("hibernate.connection.username", p.getProperty("db.username"))
                    .setProperty("hibernate.connection.password", p.getProperty("db.password"))
                    .buildSessionFactory();
        } catch (Exception e) {
            throw new RuntimeException("Cannot read db.properties", e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}