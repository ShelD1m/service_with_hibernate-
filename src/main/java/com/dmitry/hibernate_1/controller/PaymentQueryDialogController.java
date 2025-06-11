package com.dmitry.hibernate_1.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

import java.time.LocalDate;

public class PaymentQueryDialogController {

    @FXML
    private DatePicker fromDateicker;

    private Stage dialogStage;
    private boolean okClicked = false;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    public LocalDate getFromDate() {
        return fromDateicker.getValue();
    }

    @FXML
    private void handleOk() {
        if (fromDateicker.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(dialogStage);
            alert.setTitle("Нет данных");
            alert.setHeaderText("Не выбрана дата");
            alert.setContentText("Пожалуйста, выберите начальную дату.");
            alert.showAndWait();
        } else {
            okClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
}