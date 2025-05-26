package com.dmitry.hibernate_1.controller;

import com.dmitry.hibernate_1.model.Landlord;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LandlordDialogController implements DialogController<Landlord> {

    @FXML private TextField idField;
    @FXML private TextField fullNameField;
    @FXML private TextField passportField;    // Соответствует passportSerialAndNumber
    @FXML private TextField phoneNumberField;

    private Stage dialogStage;
    private Landlord landlord;
    private boolean okClicked = false;

    @FXML
    private void initialize() { idField.setDisable(true); }

    @Override
    public void setDialogStage(Stage dialogStage) { this.dialogStage = dialogStage; }

    @Override
    public void setEntity(Landlord landlord) {
        this.landlord = landlord;
        if (landlord.getLandlordId() != 0) { // Предполагаю, геттер landlordId()
            idField.setText(String.valueOf(landlord.getLandlordId()));
        } else {
            idField.setText("Авто");
        }
        fullNameField.setText(landlord.getFullName());
        passportField.setText(landlord.getPassportNumber()); // Предполагаю, геттер passportNumber()
        phoneNumberField.setText(landlord.getPhoneNumber());
    }

    @Override
    public boolean isOkClicked() { return okClicked; }

    @Override
    public Landlord getEntity() { return landlord; }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            landlord.setFullName(fullNameField.getText());
            landlord.setPassportNumber(passportField.getText()); // Предполагаю, сеттер passportNumber()
            landlord.setPhoneNumber(phoneNumberField.getText());
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