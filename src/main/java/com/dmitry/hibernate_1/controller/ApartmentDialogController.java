package com.dmitry.hibernate_1.controller;

import com.dmitry.hibernate_1.dao.LandlordDao;
import com.dmitry.hibernate_1.dao.LandlordDaoImpl; // Ваша реализация
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

import java.math.BigDecimal; // Если используете BigDecimal для площади

public class ApartmentDialogController implements DialogController<Apartment> {

    @FXML private TextField apartmentIdField;
    @FXML private ComboBox<Landlord> ownerComboBox;
    @FXML private TextField numberOfRoomsField;
    @FXML private TextField squareMetersField;

    // Поля для адреса
    @FXML private TextField addressCityField;
    @FXML private TextField addressRegionField;
    @FXML private TextField addressStreetField;
    @FXML private TextField addressHouseNumberField;
    @FXML private TextField addressFloorField;
    @FXML private TextField addressApartmentNumberField;


    private Stage dialogStage;
    private Apartment apartment;
    private Address address; // Адрес, связанный с квартирой
    private boolean okClicked = false;

    private final LandlordDao landlordDao = new LandlordDaoImpl(); // DAO для загрузки владельцев

    @FXML
    private void initialize() {
        apartmentIdField.setDisable(true);
        loadLandlords();

        // Для отображения ФИО владельца в ComboBox
        ownerComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Landlord landlord) {
                return landlord == null ? null : landlord.getFullName() + " (ID: " + landlord.getLandlordId() + ")";
            }
            @Override
            public Landlord fromString(String string) { return null;

 }
        });
    }

    private void loadLandlords() {
        ObservableList<Landlord> landlords = FXCollections.observableArrayList(landlordDao.findAll());
        ownerComboBox.setItems(landlords);
    }

    @Override
    public void setDialogStage(Stage dialogStage) { this.dialogStage = dialogStage; }

    @Override
    public void setEntity(Apartment apartment) {
        this.apartment = apartment;
        if (apartment.getAddress() == null) { // Если это новая квартира, создаем новый адрес
            this.address = new Address();
            this.apartment.setAddress(this.address); // Сразу связываем
        } else {
            this.address = apartment.getAddress();
        }

        if (apartment.getApartmentId() != 0) {
            apartmentIdField.setText(String.valueOf(apartment.getApartmentId()));
        } else {
            apartmentIdField.setText("Авто");
        }

        ownerComboBox.setValue(apartment.getLandlordId()); // Предполагается поле landlordId (или owner) в Apartment
        numberOfRoomsField.setText(apartment.getRoomCount() != null ? String.valueOf(apartment.getRoomCount()) : "");
        squareMetersField.setText(apartment.getSquareMeters() != null ? apartment.getSquareMeters().toString() : "");

        // Заполнение полей адреса
        addressCityField.setText(address.getCityName());
        addressRegionField.setText(address.getRegion());
        addressStreetField.setText(address.getStreetName());
        addressHouseNumberField.setText(address.getHouseNumber());
        addressFloorField.setText(address.getFloor() != null ? String.valueOf(address.getFloor()) : "");
        addressApartmentNumberField.setText(address.getApartmentNumber());
    }

    @Override
    public boolean isOkClicked() { return okClicked; }

    @Override
    public Apartment getEntity() { return apartment; }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            apartment.setLandlordId(ownerComboBox.getValue()); // Предполагается поле landlordId (или owner) в Apartment
            try {
                apartment.setRoomCount(numberOfRoomsField.getText().isEmpty() ? null : Integer.parseInt(numberOfRoomsField.getText()));
                apartment.setSquareMeters(squareMetersField.getText().isEmpty() ? null : new BigDecimal(squareMetersField.getText().replace(",", ".")).doubleValue()); // Замените Double если надо
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Кол-во комнат и площадь должны быть числами.");
                return;
            }

            // Обновление данных адреса
            // Address addressToSave = apartment.getAddress(); // уже установлен в setEntity
            // if (addressToSave == null) { // На всякий случай, хотя не должно быть null
            //    addressToSave = new Address();
            //    apartment.setAddress(addressToSave);
            // }
            address.setCityName(addressCityField.getText());
            address.setRegion(addressRegionField.getText());
            address.setStreetName(addressStreetField.getText());
            address.setHouseNumber(addressHouseNumberField.getText());
            try {
                address.setFloor(addressFloorField.getText().isEmpty() ? null : Integer.parseInt(addressFloorField.getText()));
            } catch (NumberFormatException ex){
                address.setFloor(null);
            }
            address.setApartmentNumber(addressApartmentNumberField.getText());
            // Связь Apartment -> Address уже установлена.
            // Hibernate сохранит Address каскадно благодаря CascadeType.ALL и @MapsId в Address.

            okClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() { dialogStage.close(); }

    private boolean isInputValid() {
        String errorMessage = "";
        if (ownerComboBox.getValue() == null) {
            errorMessage += "Не выбран владелец!\n";
        }
        if (addressCityField.getText() == null || addressCityField.getText().trim().isEmpty()) {
            errorMessage += "Не указан город для адреса!\n";
        }
        // TODO: Другие проверки для полей квартиры и адреса
        // ...

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showAlert(Alert.AlertType.ERROR, "Некорректные данные", errorMessage);
            return false;
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.initOwner(dialogStage);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
