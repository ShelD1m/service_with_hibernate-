package com.dmitry.hibernate_1.controller;


import com.dmitry.hibernate_1.model.Organization;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class OrganizationDialogController implements DialogController<Organization> {

    @FXML
    private TextField nameField;
    @FXML
    private TextField innField;
    @FXML
    private TextField websiteField;

    private Stage dialogStage;
    private Organization organization;
    private boolean okClicked = false;

    @FXML
    private void initialize() {
    }

    @Override
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @Override
    public void setEntity(Organization organization) {
        this.organization = organization;

        if (organization != null) {
            nameField.setText(organization.getOrganizationName());
            innField.setText(organization.getTaxId());
            websiteField.setText(organization.getWebsiteUrl());
        } else {
            this.organization = new Organization();
        }
    }

    @Override
    public boolean isOkClicked() {
        return okClicked;
    }

    @Override
    public Organization getEntity() {
        return organization;
    }


    @FXML
    private void handleOk() {
        if (isInputValid()) {
            organization.setOrganizationName(nameField.getText());
            organization.setTaxId(innField.getText());
            organization.setWebsiteUrl(websiteField.getText());

            okClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }


    private boolean isInputValid() {
        StringBuilder errorMessage = new StringBuilder();
        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            errorMessage.append("Не указано название организации!\n");
        } else if (nameField.getText().length() > 100) {
            errorMessage.append("Название организации слишком длинное (макс. 100 символов)!\n");
        }
        if (innField.getText() == null || innField.getText().trim().isEmpty()) {
            errorMessage.append("Не указан ИНН!\n");
        } else {
            String inn = innField.getText().trim();
            if (!inn.matches("\\d+")) {
                errorMessage.append("ИНН должен содержать только цифры!\n");
            } else if (inn.length() != 10 && inn.length() != 12) {
                errorMessage.append("ИНН должен содержать 10 или 12 цифр!\n");
            }
        }
        if (websiteField.getText() != null && !websiteField.getText().trim().isEmpty()) {
            String website = websiteField.getText().trim();
            // Простая проверка URL (можно заменить на более сложную regex)
            if (!website.matches("^(https?://)?([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$")) {
                errorMessage.append("Веб-сайт имеет некорректный формат!\n");
            } else if (website.length() > 255) {
                errorMessage.append("Ссылка на веб-сайт слишком длинная (макс. 255 символов)!\n");
            }
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showAlert("Некорректные данные",
                    "Пожалуйста, исправьте некорректные поля",
                    errorMessage.toString());
            return false;
        }
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
