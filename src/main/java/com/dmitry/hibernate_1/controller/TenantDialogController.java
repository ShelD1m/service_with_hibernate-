package com.dmitry.hibernate_1.controller;

import com.dmitry.hibernate_1.model.Tenant;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class TenantDialogController implements DialogController<Tenant> {

    @FXML private TextField idField;
    @FXML private TextField fullNameField;
    @FXML private TextField passportField;
    @FXML private TextField phoneNumberField;

    private Stage dialogStage;
    private Tenant tenant;
    private boolean okClicked = false;

    @FXML
    private void initialize() { idField.setDisable(true); }

    @Override
    public void setDialogStage(Stage dialogStage) { this.dialogStage = dialogStage; }

    @Override
    public void setEntity(Tenant tenant) {
        this.tenant = tenant;
        if (tenant.getId() != 0) {
            idField.setText(String.valueOf(tenant.getId()));
        } else {
            idField.setText("Авто");
        }
        fullNameField.setText(tenant.getFullName());
        passportField.setText(tenant.getPassportNumber()); // Предполагаю геттер getPassportNumber()
        phoneNumberField.setText(tenant.getPhoneNumber());
    }

    @Override
    public boolean isOkClicked() { return okClicked; }

    @Override
    public Tenant getEntity() { return tenant; }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            tenant.setFullName(fullNameField.getText());
            tenant.setPassportNumber(passportField.getText()); // Предполагаю сеттер setPassportNumber()
            tenant.setPhoneNumber(phoneNumberField.getText());
            okClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() { dialogStage.close(); }

    private boolean isInputValid() {
        String errorMessage = "";
        if (fullNameField.getText() == null || fullNameField.getText().trim().isEmpty()) {
            errorMessage += "Не указано ФИО!\n";
        }
        if (passportField.getText() == null || passportField.getText().trim().isEmpty()) {
            errorMessage += "Не указан паспорт!\n";
        }
        // TODO: Добавить другие проверки

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