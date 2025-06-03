package com.dmitry.hibernate_1.controller;

import com.dmitry.hibernate_1.model.Service;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Pattern;

public class ServiceDialogController implements DialogController<Service> {

    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField costField;
    @FXML private TextField typeField;

    private Stage dialogStage;
    private Service service;
    private boolean okClicked = false;

    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L}0-9 .'-]+$");
    private static final Pattern COST_PATTERN = Pattern.compile("^\\d+(\\.\\d{1,2})?$");
    private static final Pattern TYPE_PATTERN = Pattern.compile("^[\\p{L}0-9 -]+$");

    @FXML
    private void initialize() {
        idField.setDisable(true);
    }

    @Override
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @Override
    public void setEntity(Service service) {
        this.service = service;
        if (service.getId() != 0) {
            idField.setText(String.valueOf(service.getId()));
        } else {
            idField.setText("Авто");
        }
        nameField.setText(service.getName());
        costField.setText(service.getCost() != null ?
                service.getCost().setScale(2, RoundingMode.HALF_UP).toPlainString() : "");
        typeField.setText(service.getType());
    }

    @Override
    public boolean isOkClicked() {
        return okClicked;
    }

    @Override
    public Service getEntity() {
        return service;
    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            updateServiceData();
            okClicked = true;
            dialogStage.close();
        }
    }

    private void updateServiceData() {
        service.setName(nameField.getText().trim());

        try {
            String costText = costField.getText().replace(",", ".");
            service.setCost(new BigDecimal(costText).setScale(2, RoundingMode.HALF_UP));
        } catch (NumberFormatException e) {
            service.setCost(BigDecimal.ZERO);
        }

        service.setType(typeField.getText().trim());
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        StringBuilder errorMessage = new StringBuilder();
        String name = nameField.getText();
        if (name == null || name.trim().isEmpty()) {
            errorMessage.append("Не указано название услуги!\n");
        } else if (name.length() > 100) {
            errorMessage.append("Название услуги слишком длинное (максимум 100 символов)!\n");
        } else if (!NAME_PATTERN.matcher(name).matches()) {
            errorMessage.append("Название услуги содержит недопустимые символы!\n");
        }
        String cost = costField.getText();
        if (cost == null || cost.trim().isEmpty()) {
            errorMessage.append("Не указана стоимость услуги!\n");
        } else {
            try {
                BigDecimal costValue = new BigDecimal(cost.replace(",", "."));
                if (costValue.compareTo(BigDecimal.ZERO) <= 0) {
                    errorMessage.append("Стоимость должна быть положительной!\n");
                } else if (costValue.compareTo(new BigDecimal("1000000")) > 0) {
                    errorMessage.append("Стоимость слишком высокая (максимум 1 000 000)!\n");
                } else if (!COST_PATTERN.matcher(cost).matches()) {
                    errorMessage.append("Стоимость должна быть числом с максимум 2 знаками после запятой!\n");
                }
            } catch (NumberFormatException e) {
                errorMessage.append("Некорректный формат стоимости!\n");
            }
        }
        String type = typeField.getText();
        if (type != null && !type.trim().isEmpty()) {
            if (type.length() > 50) {
                errorMessage.append("Тип услуги слишком длинный (максимум 50 символов)!\n");
            } else if (!TYPE_PATTERN.matcher(type).matches()) {
                errorMessage.append("Тип услуги содержит недопустимые символы!\n");
            }
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

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(dialogStage);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}