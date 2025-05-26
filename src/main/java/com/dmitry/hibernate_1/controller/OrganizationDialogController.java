package com.dmitry.hibernate_1.controller;


import com.dmitry.hibernate_1.model.Organization; // Замените на ваш пакет
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class OrganizationDialogController implements DialogController<Organization> {

    @FXML private TextField nameField;
    @FXML private TextField innField;
    @FXML private TextField websiteField;

    private Stage dialogStage;
    private Organization organization;
    private boolean okClicked = false;

    @FXML
    private void initialize() {}

    @Override
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @Override
    public void setEntity(Organization organization) {
        this.organization = organization;
        if (organization != null) {
            // Если редактирование, заполняем поля
            nameField.setText(organization.getOrganizationName()); // Или getName()
            innField.setText(organization.getTaxId()); // Или getInn()
            websiteField.setText(organization.getWebsiteUrl()); // Или getWebsite()
        } else {
            // Если добавление новой, this.organization будет null, мы создадим новую в handleOk
            this.organization = new Organization(); // создаем пустой для заполнения
        }
    }

    @Override
    public boolean isOkClicked() {
        return okClicked;
    }

    @Override
    public Organization getEntity() {
        return organization; // Возвращаем обновленную или новую сущность
    }


    @FXML
    private void handleOk() {
        if (isInputValid()) {
            organization.setOrganizationName(nameField.getText()); // Используйте актуальные сеттеры
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
        String errorMessage = "";
        if (nameField.getText() == null || nameField.getText().isEmpty()) {
            errorMessage += "Не указано название!\n";
        }
        if (innField.getText() == null || innField.getText().isEmpty()) {
            errorMessage += "Не указан ИНН!\n";
        }
        // Другие проверки, например, на длину ИНН, формат сайта и т.д.

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            // Показать сообщение об ошибке.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Некорректные данные");
            alert.setHeaderText("Пожалуйста, исправьте некорректные поля");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
    }
}
