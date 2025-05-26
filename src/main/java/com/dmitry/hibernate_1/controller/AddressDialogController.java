package com.dmitry.hibernate_1.controller;


import com.dmitry.hibernate_1.model.Address;
import com.dmitry.hibernate_1.model.Apartment; // Для установки Apartment, если нужно
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddressDialogController implements DialogController<Address> {

    @FXML private TextField idField; // Будет ID квартиры
    @FXML private TextField cityField;
    @FXML private TextField regionField;
    @FXML private TextField streetField;
    @FXML private TextField houseNumberField;
    @FXML private TextField floorField;
    @FXML private TextField apartmentNumberField;

    private Stage dialogStage;
    private Address address;
    private boolean okClicked = false;
    // private Apartment apartmentContext; // Если адрес создается в контексте квартиры

    @FXML
    private void initialize() { idField.setDisable(true); }

    @Override
    public void setDialogStage(Stage dialogStage) { this.dialogStage = dialogStage; }

    @Override
    public void setEntity(Address address) {
        this.address = address;
        // if (address.getApartment() != null && address.getApartment().getApartmentId() != 0) {
        //    idField.setText(String.valueOf(address.getApartment().getApartmentId()));
        // }
        // Если Address уже существует и у него есть ID (который равен ID квартиры)
        if (address.getAddressId() != 0) {
            idField.setText(String.valueOf(address.getAddressId()));
        } else {
            idField.setText("Авто (от Квартиры)");
        }

        cityField.setText(address.getCityName()); // Используйте геттер из Address
        regionField.setText(address.getRegion());
        streetField.setText(address.getStreetName());
        houseNumberField.setText(address.getHouseNumber());
        floorField.setText(address.getFloor() != null ? String.valueOf(address.getFloor()) : "");
        apartmentNumberField.setText(address.getApartmentNumber());
    }
    /* Если адрес всегда связан с квартирой, то нужно передавать квартиру:
    public void setEntity(Address address, Apartment apartmentContext) {
        this.address = address;
        this.apartmentContext = apartmentContext;
        if (apartmentContext != null) {
             idField.setText(String.valueOf(apartmentContext.getApartmentId()));
        }
        // ... заполнение полей из address ...
    }
    */

    @Override
    public boolean isOkClicked() { return okClicked; }

    @Override
    public Address getEntity() { return address; }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            address.setCityName(cityField.getText()); // Используйте сеттеры из Address
            address.setRegion(regionField.getText());
            address.setStreetName(streetField.getText());
            address.setHouseNumber(houseNumberField.getText());
            try {
                address.setFloor(floorField.getText().isEmpty() ? null : Integer.parseInt(floorField.getText()));
            } catch (NumberFormatException e) {
                // Обработка ошибки, если этаж не число
            }
            address.setApartmentNumber(apartmentNumberField.getText());

            // Если Address создается/редактируется в контексте Apartment, связь уже установлена или будет установлена там.
            // @MapsId сам свяжет ID при сохранении Apartment, содержащего этот Address.
            // Если это самостоятельный Address (редко для такой схемы), нужно обработать apartment.setId() здесь,
            // но это противоречит @MapsId, если нет связанного apartment.
            // В данном случае, мы предполагаем, что Address - часть Квартиры,
            // и связь через поле Apartment apartment в Address устанавливается там, где создается/редактируется Квартира.

            okClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() { dialogStage.close(); }

    private boolean isInputValid() {
        String errorMessage = "";
        if (cityField.getText() == null || cityField.getText().trim().isEmpty()) {
            errorMessage += "Не указан город!\n";
        }
        if (streetField.getText() == null || streetField.getText().trim().isEmpty()) {
            errorMessage += "Не указана улица!\n";
        }
        // Доп. проверки

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            // ... (код показа сообщения об ошибке) ...
            alert.showAndWait();
            return false;
        }
    }
}
