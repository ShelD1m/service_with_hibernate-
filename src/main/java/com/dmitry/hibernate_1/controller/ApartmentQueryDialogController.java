package com.dmitry.hibernate_1.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ApartmentQueryDialogController {

    @FXML
    private TextField roomCountField;

    private Stage dialogStage;
    private boolean okClicked = false;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    public String getRoomCount() {
        return roomCountField.getText();
    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            okClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";
        if (roomCountField.getText() == null || roomCountField.getText().isEmpty()) {
            errorMessage += "Не введено количество комнат!\n";
        } else {
            try {
                Integer.parseInt(roomCountField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "Неверный формат! Введите целое число.\n";
            }
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Неверный ввод");
            alert.setHeaderText("Пожалуйста, исправьте ошибку");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
    }
}