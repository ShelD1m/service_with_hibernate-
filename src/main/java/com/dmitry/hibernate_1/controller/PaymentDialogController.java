package com.dmitry.hibernate_1.controller;

import com.dmitry.hibernate_1.dao.OrganizationDao;
import com.dmitry.hibernate_1.dao.OrganizationDaoImpl;
import com.dmitry.hibernate_1.dao.ServiceDao;
import com.dmitry.hibernate_1.dao.ServiceDaoImpl;
import com.dmitry.hibernate_1.dao.TenantDao;
import com.dmitry.hibernate_1.dao.TenantDaoImpl;
import com.dmitry.hibernate_1.model.Organization;
import com.dmitry.hibernate_1.model.Payment;
import com.dmitry.hibernate_1.model.Service;
import com.dmitry.hibernate_1.model.Tenant;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;

public class PaymentDialogController implements DialogController<Payment> {

    @FXML private TextField idField;
    @FXML private ComboBox<Tenant> tenantComboBox;
    @FXML private ComboBox<Service> serviceComboBox;
    @FXML private ComboBox<Organization> organizationComboBox;
    @FXML private DatePicker datePicker;
    // @FXML private TextField actualCostField; // Если используется

    private Stage dialogStage;
    private Payment payment;
    private boolean okClicked = false;

    private final TenantDao tenantDao = new TenantDaoImpl();
    private final ServiceDao serviceDao = new ServiceDaoImpl();
    private final OrganizationDao organizationDao = new OrganizationDaoImpl();

    @FXML
    private void initialize() {
        idField.setDisable(true);
        loadComboBoxData();
        setupComboBoxConverters();
        datePicker.setValue(LocalDate.now()); // Значение по умолчанию
    }

    private void loadComboBoxData() {
        tenantComboBox.setItems(FXCollections.observableArrayList(tenantDao.findAll()));
        serviceComboBox.setItems(FXCollections.observableArrayList(serviceDao.findAll()));
        organizationComboBox.setItems(FXCollections.observableArrayList(organizationDao.findAll()));
    }

    private void setupComboBoxConverters() {
        tenantComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(Tenant t) { return t == null ? null : t.getFullName() + " (ID:" + t.getId() + ")"; }
            @Override public Tenant fromString(String s) { return null; }
        });
        serviceComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(Service s) { return s == null ? null : s.getName() + " (ID:" + s.getId() + ")"; }
            @Override public Service fromString(String s) { return null; }
        });
        organizationComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(Organization o) { return o == null ? null : o.getOrganizationName() + " (ID:" + o.getId() + ")"; }
            @Override public Organization fromString(String s) { return null; }
        });
    }

    @Override
    public void setDialogStage(Stage dialogStage) { this.dialogStage = dialogStage; }

    @Override
    public void setEntity(Payment payment) {
        this.payment = payment;
        if (payment.getId() != 0) {
            idField.setText(String.valueOf(payment.getId()));
        } else {
            idField.setText("Авто");
        }
        tenantComboBox.setValue(payment.getTenant());
        serviceComboBox.setValue(payment.getService());
        organizationComboBox.setValue(payment.getOrganization());
        datePicker.setValue(payment.getDate() != null ? payment.getDate() : LocalDate.now());
        // if (payment.getActualCost() != null) actualCostField.setText(payment.getActualCost().toString());
    }

    @Override
    public boolean isOkClicked() { return okClicked; }

    @Override
    public Payment getEntity() { return payment; }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            payment.setTenant(tenantComboBox.getValue());
            payment.setService(serviceComboBox.getValue());
            payment.setOrganization(organizationComboBox.getValue());
            payment.setDate(datePicker.getValue());
            // if (!actualCostField.getText().isEmpty()) {
            //     payment.setActualCost(new BigDecimal(actualCostField.getText().replace(",", ".")));
            // }
            okClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() { dialogStage.close(); }

    private boolean isInputValid() {
        String errorMessage = "";
        if (tenantComboBox.getValue() == null) errorMessage += "Не выбран плательщик!\n";
        if (serviceComboBox.getValue() == null) errorMessage += "Не выбрана услуга!\n";
        if (organizationComboBox.getValue() == null) errorMessage += "Не выбрана организация-получатель!\n";
        if (datePicker.getValue() == null) errorMessage += "Не указана дата платежа!\n";
        // TODO: Проверка для actualCostField, если он используется

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showAlert("Некорректные данные", errorMessage);
            return false;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        // ... (код показа сообщения) ...
        alert.showAndWait();
    }
}