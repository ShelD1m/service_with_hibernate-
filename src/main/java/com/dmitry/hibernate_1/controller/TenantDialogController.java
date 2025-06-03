package com.dmitry.hibernate_1.controller;

import com.dmitry.hibernate_1.model.Tenant;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.regex.Pattern;

public class TenantDialogController implements DialogController<Tenant> {

    @FXML private TextField idField;
    @FXML private TextField fullNameField;
    @FXML private TextField passportField;
    @FXML private TextField phoneNumberField;

    private Stage dialogStage;
    private Tenant tenant;
    private boolean okClicked = false;

    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L} .'-]+$");
    private static final Pattern PASSPORT_PATTERN = Pattern.compile("^\\d{4} \\d{6}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[78][- ]?\\(?\\d{3}\\)?[- ]?\\d{3}[- ]?\\d{2}[- ]?\\d{2}$");

    @FXML
    private void initialize() {
        idField.setDisable(true);
    }

    @Override
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @Override
    public void setEntity(Tenant tenant) {
        this.tenant = tenant;
        if (tenant.getId() != 0) {
            idField.setText(String.valueOf(tenant.getId()));
        } else {
            idField.setText("Авто");
        }
        fullNameField.setText(tenant.getFullName());
        passportField.setText(tenant.getPassportNumber());
        phoneNumberField.setText(formatPhoneNumber(tenant.getPhoneNumber()));
    }

    @Override
    public boolean isOkClicked() {
        return okClicked;
    }

    @Override
    public Tenant getEntity() {
        return tenant;
    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            updateTenantData();
            okClicked = true;
            dialogStage.close();
        }
    }

    private void updateTenantData() {
        tenant.setFullName(fullNameField.getText().trim());
        tenant.setPassportNumber(passportField.getText().trim());
        tenant.setPhoneNumber(formatPhoneNumber(phoneNumberField.getText().trim()));
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        StringBuilder errorMessage = new StringBuilder();
        String fullName = fullNameField.getText();
        if (fullName == null || fullName.trim().isEmpty()) {
            errorMessage.append("Не указано ФИО!\n");
        } else if (fullName.length() > 100) {
            errorMessage.append("ФИО слишком длинное (максимум 100 символов)!\n");
        } else if (!NAME_PATTERN.matcher(fullName).matches()) {
            errorMessage.append("ФИО содержит недопустимые символы!\n");
        }
        String passport = passportField.getText();
        if (passport == null || passport.trim().isEmpty()) {
            errorMessage.append("Не указан номер паспорта!\n");
        } else if (!PASSPORT_PATTERN.matcher(passport).matches()) {
            errorMessage.append("Паспорт должен быть в формате: 1234 567890\n");
        }
        String phone = phoneNumberField.getText();
        if (phone == null || phone.trim().isEmpty()) {
            errorMessage.append("Не указан номер телефона!\n");
        } else if (!PHONE_PATTERN.matcher(phone).matches()) {
            errorMessage.append("Телефон должен быть в формате: +7(123)456-78-90 или 81234567890\n");
        }
        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showAlert("Ошибка ввода данных",
                    "Пожалуйста, исправьте следующие ошибки:",
                    errorMessage.toString());
            return false;
        }
    }

    private String formatPhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return "";
        }
        String digits = phone.replaceAll("[^0-9]", "");
        if (digits.startsWith("7") || digits.startsWith("8")) {
            return "+7" + digits.substring(1);
        }
        return phone;
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(dialogStage);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}