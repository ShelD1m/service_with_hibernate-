package com.dmitry.hibernate_1.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class OrganizationQueryDialogController {

    @FXML
    private TextField orgNameField;

    private Stage dialogStage;
    private boolean okClicked = false;

    @FXML
    private void initialize() {
        System.out.println("OrganizationQueryDialogController: метод initialize() вызван.");
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    public String getOrganizationName() {
        if (orgNameField != null) {
            return orgNameField.getText();
        }
        return null;
    }

    @FXML
    private void handleOk() {
        System.out.println("Нажата кнопка 'Выполнить'.");
        okClicked = true;
        dialogStage.close();
    }

    @FXML
    private void handleCancel() {
        System.out.println("Нажата кнопка 'Отмена'.");
        dialogStage.close();
    }
}