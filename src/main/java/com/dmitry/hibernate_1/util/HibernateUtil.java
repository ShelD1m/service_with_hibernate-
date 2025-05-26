package com.dmitry.hibernate_1.util;


import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import com.dmitry.hibernate_1.model.*; // Импорт всех моделей

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");
            System.out.println("Hibernate Configuration loaded");

            configuration.addAnnotatedClass(Address.class);
            configuration.addAnnotatedClass(Apartment.class);
            configuration.addAnnotatedClass(ContractSigning.class);
            configuration.addAnnotatedClass(ContractTermination.class);
            configuration.addAnnotatedClass(Landlord.class);
            configuration.addAnnotatedClass(Organization.class);
            configuration.addAnnotatedClass(Payment.class);
            configuration.addAnnotatedClass(Service.class);
            configuration.addAnnotatedClass(Tenant.class);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();
            System.out.println("Hibernate serviceRegistry created");

            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            System.out.println("SessionFactory created");
            return sessionFactory;
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = buildSessionFactory();
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            getSessionFactory().close();
            System.out.println("SessionFactory shutdown");
        }
    }
}