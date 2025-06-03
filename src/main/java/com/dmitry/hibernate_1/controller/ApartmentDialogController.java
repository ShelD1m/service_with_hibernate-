package com.dmitry.hibernate_1.controller;

import com.dmitry.hibernate_1.dao.LandlordDao;
import com.dmitry.hibernate_1.dao.LandlordDaoImpl;
import com.dmitry.hibernate_1.model.Address;
import com.dmitry.hibernate_1.model.Apartment;
import com.dmitry.hibernate_1.model.Landlord;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ApartmentDialogController implements DialogController<Apartment> {

    @FXML private TextField apartmentIdField;
    @FXML private ComboBox<Landlord> ownerComboBox;
    @FXML private TextField numberOfRoomsField;
    @FXML private TextField squareMetersField;

    @FXML private TextField addressCityField;
    @FXML private TextField addressRegionField;
    @FXML private TextField addressStreetField;
    @FXML private TextField addressHouseNumberField;
    @FXML private TextField addressFloorField;
    @FXML private TextField addressApartmentNumberField;

    private Stage dialogStage;
    private Apartment apartment;
    private Address address;
    private boolean okClicked = false;
    private final LandlordDao landlordDao = new LandlordDaoImpl();

    @FXML
    private void initialize() {
        apartmentIdField.setDisable(true);
        loadLandlords();

        ownerComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Landlord landlord) {
                return landlord == null ? null : landlord.getFullName() + " (ID: " + landlord.getLandlordId() + ")";
            }

            @Override
            public Landlord fromString(String string) {
                return null;
            }
        });
    }

    private void loadLandlords() {
        ObservableList<Landlord> landlords = FXCollections.observableArrayList(landlordDao.findAll());
        ownerComboBox.setItems(landlords);
    }

    @Override
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @Override
    public void setEntity(Apartment apartment) {
        this.apartment = apartment;
        this.address = apartment.getAddress() != null ? apartment.getAddress() : new Address();

        apartmentIdField.setText(apartment.getApartmentId() != 0 ?
                String.valueOf(apartment.getApartmentId()) : "Авто");

        ownerComboBox.setValue(apartment.getLandlordId());
        numberOfRoomsField.setText(apartment.getRoomCount() != null ?
                String.valueOf(apartment.getRoomCount()) : "");
        squareMetersField.setText(apartment.getSquareMeters() != null ?
                String.valueOf(apartment.getSquareMeters()) : "");

        addressCityField.setText(address.getCityName());
        addressRegionField.setText(address.getRegion());
        addressStreetField.setText(address.getStreetName());
        addressHouseNumberField.setText(address.getHouseNumber());
        addressFloorField.setText(address.getFloor() != null ?
                String.valueOf(address.getFloor()) : "");
        addressApartmentNumberField.setText(address.getApartmentNumber());
    }

    @Override
    public boolean isOkClicked() {
        return okClicked;
    }

    @Override
    public Apartment getEntity() {
        return apartment;
    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            updateApartmentData();
            updateAddressData();

            apartment.setAddress(address);
            address.setApartment(apartment);

            okClicked = true;
            dialogStage.close();
        }
    }

    private void updateApartmentData() {
        apartment.setLandlordId(ownerComboBox.getValue());

        try {
            apartment.setRoomCount(numberOfRoomsField.getText().isEmpty() ?
                    null : Integer.parseInt(numberOfRoomsField.getText()));
        } catch (NumberFormatException e) {
            apartment.setRoomCount(null);
        }

        try {
            String squareText = squareMetersField.getText().replace(",", ".");
            apartment.setSquareMeters(squareText.isEmpty() ?
                    null : new BigDecimal(squareText).setScale(2, RoundingMode.HALF_UP).doubleValue());
        } catch (NumberFormatException e) {
            apartment.setSquareMeters(null);
        }
    }

    private void updateAddressData() {
        address.setCityName(addressCityField.getText().trim());
        address.setRegion(addressRegionField.getText().trim());
        address.setStreetName(addressStreetField.getText().trim());
        address.setHouseNumber(addressHouseNumberField.getText().trim());

        try {
            address.setFloor(addressFloorField.getText().isEmpty() ?
                    null : Integer.parseInt(addressFloorField.getText()));
        } catch (NumberFormatException e) {
            address.setFloor(null);
        }

        address.setApartmentNumber(addressApartmentNumberField.getText().trim());
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        StringBuilder errorMessage = new StringBuilder();
        if (ownerComboBox.getValue() == null) {
            errorMessage.append("Не выбран владелец квартиры!\n");
        }
        if (!numberOfRoomsField.getText().isEmpty()) {
            try {
                int rooms = Integer.parseInt(numberOfRoomsField.getText());
                if (rooms <= 0 || rooms > 20) {
                    errorMessage.append("Количество комнат должно быть от 1 до 20!\n");
                }
            } catch (NumberFormatException e) {
                errorMessage.append("Количество комнат должно быть целым числом!\n");
            }
        }
        if (!squareMetersField.getText().isEmpty()) {
            try {
                double square = new BigDecimal(squareMetersField.getText().replace(",", "."))
                        .setScale(2, RoundingMode.HALF_UP).doubleValue();
                if (square <= 0 || square > 1000) {
                    errorMessage.append("Площадь должна быть от 0.01 до 1000 кв.м!\n");
                }
            } catch (NumberFormatException e) {
                errorMessage.append("Площадь должна быть числом (разделитель . или ,)!\n");
            }
        }
        if (addressCityField.getText() == null || addressCityField.getText().trim().isEmpty()) {
            errorMessage.append("Не указан город!\n");
        } else if (addressCityField.getText().length() > 100) {
            errorMessage.append("Название города слишком длинное (макс. 100 символов)!\n");
        }
        if (addressStreetField.getText() == null || addressStreetField.getText().trim().isEmpty()) {
            errorMessage.append("Не указана улица!\n");
        } else if (addressStreetField.getText().length() > 100) {
            errorMessage.append("Название улицы слишком длинное (макс. 100 символов)!\n");
        }
        if (addressHouseNumberField.getText() == null || addressHouseNumberField.getText().trim().isEmpty()) {
            errorMessage.append("Не указан номер дома!\n");
        } else if (addressHouseNumberField.getText().length() > 20) {
            errorMessage.append("Номер дома слишком длинный (макс. 20 символов)!\n");
        }
        if (!addressFloorField.getText().isEmpty()) {
            try {
                int floor = Integer.parseInt(addressFloorField.getText());
                if (floor < -10 || floor > 200) {
                    errorMessage.append("Этаж должен быть между -10 и 200!\n");
                }
            } catch (NumberFormatException e) {
                errorMessage.append("Этаж должен быть целым числом!\n");
            }
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