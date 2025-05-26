package com.dmitry.hibernate_1;


import com.dmitry.hibernate_1.util.HibernateUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.dmitry.hibernate_1.util.HibernateUtil; // Импортируем

import java.io.IOException;
import java.net.URL;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            HibernateUtil.getSessionFactory();

            FXMLLoader loader = new FXMLLoader();
            URL xmlUrl = getClass().getResource("/fxml/MainWindow.fxml");
            if (xmlUrl == null) {
                System.err.println("Cannot find FXML file!");
                return;
            }
            loader.setLocation(xmlUrl);
            Parent root = loader.load();

            primaryStage.setTitle("Управление недвижимостью");
            primaryStage.setScene(new Scene(root, 800, 600));
            primaryStage.show();

        } catch (IOException e) {
            System.err.println("Failed to load FXML or initialize application.");
            e.printStackTrace();
        } catch (ExceptionInInitializerError e) {
            System.err.println("Failed to initialize Hibernate SessionFactory.");
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        HibernateUtil.shutdown();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
