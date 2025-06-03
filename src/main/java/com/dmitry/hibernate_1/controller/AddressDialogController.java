package com.dmitry.hibernate_1.controller;

import com.dmitry.hibernate_1.model.Address;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddressDialogController implements DialogController<Address> {

    @FXML private TextField idField;
    @FXML private TextField cityField;
    @FXML private TextField regionField;
    @FXML private TextField streetField;
    @FXML private TextField houseNumberField;
    @FXML private TextField floorField;
    @FXML private TextField apartmentNumberField;

    private Stage dialogStage;
    private Address address;
    private boolean okClicked = false;

    @FXML
    private void initialize() {
        idField.setDisable(true);
    }

    @Override
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @Override
    public void setEntity(Address address) {
        this.address = address;

        if (address.getAddressId() != 0) {
            idField.setText(String.valueOf(address.getAddressId()));
        } else {
            idField.setText("Авто (от Квартиры)");
        }

        cityField.setText(address.getCityName());
        regionField.setText(address.getRegion());
        streetField.setText(address.getStreetName());
        houseNumberField.setText(address.getHouseNumber());
        floorField.setText(address.getFloor() != null ? String.valueOf(address.getFloor()) : "");
        apartmentNumberField.setText(address.getApartmentNumber());
    }

    @Override
    public boolean isOkClicked() {
        return okClicked;
    }

    @Override
    public Address getEntity() {
        return address;
    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            address.setCityName(cityField.getText().trim());
            address.setRegion(regionField.getText().trim());
            address.setStreetName(streetField.getText().trim());
            address.setHouseNumber(houseNumberField.getText().trim());

            try {
                address.setFloor(floorField.getText().isEmpty() ? null : Integer.parseInt(floorField.getText()));
            } catch (NumberFormatException e) {
                address.setFloor(null);
            }

            address.setApartmentNumber(apartmentNumberField.getText().trim());
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
        if (cityField.getText() == null || cityField.getText().trim().isEmpty()) {
            errorMessage.append("Не указан город!\n");
        } else if (cityField.getText().length() > 100) {
            errorMessage.append("Название города слишком длинное (макс. 100 символов)!\n");
        }
        if (regionField.getText() != null && regionField.getText().length() > 100) {
            errorMessage.append("Название региона слишком длинное (макс. 100 символов)!\n");
        }
        if (streetField.getText() == null || streetField.getText().trim().isEmpty()) {
            errorMessage.append("Не указана улица!\n");
        } else if (streetField.getText().length() > 100) {
            errorMessage.append("Название улицы слишком длинное (макс. 100 символов)!\n");
        }
        if (houseNumberField.getText() == null || houseNumberField.getText().trim().isEmpty()) {
            errorMessage.append("Не указан номер дома!\n");
        } else if (houseNumberField.getText().length() > 20) {
            errorMessage.append("Номер дома слишком длинный (макс. 20 символов)!\n");
        }
        if (!floorField.getText().isEmpty()) {
            try {
                int floor = Integer.parseInt(floorField.getText());
                if (floor < -10 || floor > 200) {
                    errorMessage.append("Этаж должен быть между -10 и 200!\n");
                }
            } catch (NumberFormatException e) {
                errorMessage.append("Этаж должен быть целым числом!\n");
            }
        }
        if (apartmentNumberField.getText() != null && apartmentNumberField.getText().length() > 20) {
            errorMessage.append("Номер квартиры слишком длинный (макс. 20 символов)!\n");
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showAlert("Ошибка ввода",
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