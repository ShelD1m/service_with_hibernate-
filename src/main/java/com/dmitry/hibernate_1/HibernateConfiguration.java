package com.dmitry.hibernate_1;

import org.hibernate.SessionFactory;

public class HibernateConfiguration {
    public SessionFactory sessionFactory(){
        org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();
        configuration
                .addPackage("com.dmitry.hibernate_1")
                .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
                .setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:5438/Service")
                .setProperty("hibernate.connection.username", "postgres")
                .setProperty("hibernate.connection.password", "postgres")
                .setProperty("hibernate.show_sql", "true")
                .setProperty("hibernate.hdm2ddl.auto", "create-drop");
        return configuration.buildSessionFactory();
    }
}
