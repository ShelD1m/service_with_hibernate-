package com.dmitry.hibernate_1.controller;

import com.dmitry.hibernate_1.dao.*;
import com.dmitry.hibernate_1.model.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;

public class ContractSigningDialogController implements DialogController<ContractSigning> {

    @FXML private TextField contractNumberField;
    @FXML private ComboBox<Landlord> landlordComboBox;
    @FXML private ComboBox<Tenant> tenantComboBox;
    @FXML private ComboBox<Apartment> apartmentComboBox;
    @FXML private DatePicker signingDatePicker;
    @FXML private TextField termField;

    private Stage dialogStage;
    private ContractSigning contractSigning;
    private boolean okClicked = false;

    private final LandlordDao landlordDao = new LandlordDaoImpl();
    private final TenantDao tenantDao = new TenantDaoImpl();
    private final ApartmentDao apartmentDao = new ApartmentDaoImpl();

    private boolean isEditMode = false;

    @FXML
    private void initialize() {
        loadComboBoxData();
        setupComboBoxConverters();
        signingDatePicker.setValue(LocalDate.now());
    }

    private void loadComboBoxData() {
        landlordComboBox.setItems(FXCollections.observableArrayList(landlordDao.findAll()));
        tenantComboBox.setItems(FXCollections.observableArrayList(tenantDao.findAll()));
        apartmentComboBox.setItems(FXCollections.observableArrayList(apartmentDao.findAll()));
    }

    private void setupComboBoxConverters() {
        landlordComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(Landlord l) { return l == null ? null : l.getFullName() + " (ID: " + l.getLandlordId() + ")";}
            @Override public Landlord fromString(String s) { return null; }
        });
        tenantComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(Tenant t) { return t == null ? null : t.getFullName() + " (ID: " + t.getId() + ")"; }
            @Override public Tenant fromString(String s) { return null; }
        });
        apartmentComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(Apartment a) {
                if (a == null) return null;
                String addressShort = a.getAddress() != null ? (a.getAddress().getCityName() + ", " + a.getAddress().getStreetName()) : "Адрес не указан";
                return "ID: " + a.getApartmentId() + " (" + addressShort + ")";
            }
            @Override public Apartment fromString(String s) { return null; }
        });
    }

    @Override
    public void setDialogStage(Stage dialogStage) { this.dialogStage = dialogStage; }

    @Override
    public void setEntity(ContractSigning contractSigning) {
        this.contractSigning = contractSigning;
        if ((contractSigning.getContractNumber()+"") != null && !(contractSigning.getContractNumber()+"").isEmpty()) {
            isEditMode = true;
            contractNumberField.setText(contractSigning.getContractNumber()+"");
            contractNumberField.setDisable(true);
        } else {
            isEditMode = false;
            contractNumberField.setDisable(false);
            contractNumberField.setText("");
        }

        landlordComboBox.setValue(contractSigning.getLandlord());
        tenantComboBox.setValue(contractSigning.getTenant());
        apartmentComboBox.setValue(contractSigning.getApartment());
        signingDatePicker.setValue(contractSigning.getSigningDate() != null ? contractSigning.getSigningDate() : LocalDate.now());
        termField.setText(contractSigning.getTerm());
    }

    @Override
    public boolean isOkClicked() { return okClicked; }

    @Override
    public ContractSigning getEntity() { return contractSigning; }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            if (!isEditMode) {
                contractSigning.setContractNumber(Integer.parseInt(contractNumberField.getText()));
            }

            contractSigning.setLandlord(landlordComboBox.getValue());
            contractSigning.setTenant(tenantComboBox.getValue());
            contractSigning.setApartment(apartmentComboBox.getValue());
            contractSigning.setSigningDate(signingDatePicker.getValue());
            contractSigning.setTerm(termField.getText());

            okClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() { dialogStage.close(); }

    private boolean isInputValid() {
        String errorMessage = "";
        if (!isEditMode && (contractNumberField.getText() == null || contractNumberField.getText().trim().isEmpty())) {
            errorMessage += "Не указан номер договора!\n";
        }
        if (landlordComboBox.getValue() == null) errorMessage += "Не выбран арендодатель!\n";
        if (tenantComboBox.getValue() == null) errorMessage += "Не выбран квартиросъемщик!\n";
        if (apartmentComboBox.getValue() == null) errorMessage += "Не выбрана квартира!\n";
        if (signingDatePicker.getValue() == null) errorMessage += "Не указана дата подписания!\n";
        if (termField.getText() == null || termField.getText().trim().isEmpty()) {
            errorMessage += "Не указан срок договора!\n";
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showAlert("Некорректные данные", errorMessage);
            return false;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.showAndWait();
    }
}