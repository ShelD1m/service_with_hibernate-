package com.dmitry.hibernate_1.controller;

import com.dmitry.hibernate_1.dao.*;
import com.dmitry.hibernate_1.model.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

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
    private boolean isEditMode = false;

    private final LandlordDao landlordDao = new LandlordDaoImpl();
    private final TenantDao tenantDao = new TenantDaoImpl();
    private final ApartmentDao apartmentDao = new ApartmentDaoImpl();

    private static final Pattern CONTRACT_NUMBER_PATTERN = Pattern.compile("^\\d{1,10}$");
    private static final Pattern TERM_PATTERN = Pattern.compile("^[\\p{L}\\d -]+$");

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
            @Override public String toString(Landlord l) {
                return l == null ? null : l.getFullName() + " (ID: " + l.getLandlordId() + ")";
            }
            @Override public Landlord fromString(String s) { return null; }
        });

        tenantComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(Tenant t) {
                return t == null ? null : t.getFullName() + " (ID: " + t.getId() + ")";
            }
            @Override public Tenant fromString(String s) { return null; }
        });

        apartmentComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(Apartment a) {
                if (a == null) return null;
                String addressShort = a.getAddress() != null ?
                        (a.getAddress().getCityName() + ", " + a.getAddress().getStreetName() + " " + a.getAddress().getHouseNumber()) :
                        "Адрес не указан";
                return "ID: " + a.getApartmentId() + " (" + addressShort + ")";
            }
            @Override public Apartment fromString(String s) { return null; }
        });
    }

    @Override
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @Override
    public void setEntity(ContractSigning contractSigning) {
        this.contractSigning = contractSigning;

        if (contractSigning.getContractNumber()+"" != null) {
            isEditMode = true;
            contractNumberField.setText(String.valueOf(contractSigning.getContractNumber()));
            contractNumberField.setDisable(true);
        } else {
            isEditMode = false;
            contractNumberField.setDisable(false);
            contractNumberField.setText("");
        }

        landlordComboBox.setValue(contractSigning.getLandlord());
        tenantComboBox.setValue(contractSigning.getTenant());
        apartmentComboBox.setValue(contractSigning.getApartment());
        signingDatePicker.setValue(contractSigning.getSigningDate() != null ?
                contractSigning.getSigningDate() : LocalDate.now());
        termField.setText(contractSigning.getTerm());
    }

    @Override
    public boolean isOkClicked() {
        return okClicked;
    }

    @Override
    public ContractSigning getEntity() {
        return contractSigning;
    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            updateContractData();
            okClicked = true;
            dialogStage.close();
        }
    }

    private void updateContractData() {
        if (!isEditMode) {
            contractSigning.setContractNumber(Integer.parseInt(contractNumberField.getText()));
        }

        contractSigning.setLandlord(landlordComboBox.getValue());
        contractSigning.setTenant(tenantComboBox.getValue());
        contractSigning.setApartment(apartmentComboBox.getValue());
        contractSigning.setSigningDate(signingDatePicker.getValue());
        contractSigning.setTerm(termField.getText().trim());
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        StringBuilder errorMessage = new StringBuilder();
        if (!isEditMode) {
            String contractNumber = contractNumberField.getText();
            if (contractNumber == null || contractNumber.trim().isEmpty()) {
                errorMessage.append("Не указан номер договора!\n");
            } else if (!CONTRACT_NUMBER_PATTERN.matcher(contractNumber).matches()) {
                errorMessage.append("Номер договора должен содержать только цифры!\n");
            } else {
                try {
                    int number = Integer.parseInt(contractNumber);
                    if (number <= 0) {
                        errorMessage.append("Номер договора должен быть положительным числом!\n");
                    }
                } catch (NumberFormatException e) {
                    errorMessage.append("Некорректный номер договора!\n");
                }
            }
        }
        if (landlordComboBox.getValue() == null) {
            errorMessage.append("Не выбран арендодатель!\n");
        }
        if (tenantComboBox.getValue() == null) {
            errorMessage.append("Не выбран квартиросъемщик!\n");
        }
        if (apartmentComboBox.getValue() == null) {
            errorMessage.append("Не выбрана квартира!\n");
        }
        if (signingDatePicker.getValue() == null) {
            errorMessage.append("Не указана дата подписания!\n");
        } else {
            LocalDate signingDate = signingDatePicker.getValue();
            LocalDate today = LocalDate.now();
            if (signingDate.isAfter(today)) {
                errorMessage.append("Дата подписания не может быть в будущем!\n");
            }
            if (signingDate.isBefore(today.minusYears(5))) {
                errorMessage.append("Дата подписания не может быть старше 5 лет!\n");
            }
        }
        String term = termField.getText();
        if (term == null || term.trim().isEmpty()) {
            errorMessage.append("Не указан срок договора!\n");
        } else if (term.length() > 100) {
            errorMessage.append("Срок договора слишком длинный (максимум 100 символов)!\n");
        } else if (!TERM_PATTERN.matcher(term).matches()) {
            errorMessage.append("Срок договора содержит недопустимые символы!\n");
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