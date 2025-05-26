package com.dmitry.hibernate_1.controller;

import com.dmitry.hibernate_1.model.Service;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ServiceDialogController implements DialogController<Service> {

    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField costField;
    @FXML private TextField typeField;

    private Stage dialogStage;
    private Service service;
    private boolean okClicked = false;

    @FXML
    private void initialize() { idField.setDisable(true); }

    @Override
    public void setDialogStage(Stage dialogStage) { this.dialogStage = dialogStage; }

    @Override
    public void setEntity(Service service) {
        this.service = service;
        if (service.getId() != 0) {
            idField.setText(String.valueOf(service.getId()));
        } else {
            idField.setText("Авто");
        }
        nameField.setText(service.getName());
        costField.setText(service.getCost() != null ? service.getCost().setScale(2, RoundingMode.HALF_UP).toPlainString() : "");
        typeField.setText(service.getType());
    }

    @Override
    public boolean isOkClicked() { return okClicked; }

    @Override
    public Service getEntity() { return service; }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            service.setName(nameField.getText());
            try {
                service.setCost(new BigDecimal(costField.getText().replace(",", ".")));
            } catch (NumberFormatException e) {
                // Уже проверено в isInputValid
            }
            service.setType(typeField.getText());
            okClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() { dialogStage.close(); }

    private boolean isInputValid() {
        String errorMessage = "";
        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            errorMessage += "Не указано название услуги!\n";
        }
        if (costField.getText() == null || costField.getText().trim().isEmpty()) {
            errorMessage += "Не указана стоимость!\n";
        } else {
            try {
                new BigDecimal(costField.getText().replace(",", "."));
            } catch (NumberFormatException e) {
                errorMessage += "Стоимость должна быть числом!\n";
            }
        }
        // TODO: Другие проверки

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showAlert("Некорректные данные", errorMessage);
            return false;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(dialogStage);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}