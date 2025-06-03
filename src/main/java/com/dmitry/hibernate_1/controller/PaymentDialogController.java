package com.dmitry.hibernate_1.controller;

import com.dmitry.hibernate_1.dao.*;
import com.dmitry.hibernate_1.model.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

public class PaymentDialogController implements DialogController<Payment> {

    @FXML private TextField idField;
    @FXML private ComboBox<Tenant> tenantComboBox;
    @FXML private ComboBox<Service> serviceComboBox;
    @FXML private ComboBox<Organization> organizationComboBox;
    @FXML private DatePicker datePicker;
    @FXML private TextField amountField;

    private Stage dialogStage;
    private Payment payment;
    private boolean okClicked = false;

    private final TenantDao tenantDao = new TenantDaoImpl();
    private final ServiceDao serviceDao = new ServiceDaoImpl();
    private final OrganizationDao organizationDao = new OrganizationDaoImpl();

    private static final Pattern AMOUNT_PATTERN = Pattern.compile("^\\d+(\\.\\d{1,2})?$");

    @FXML
    private void initialize() {
        idField.setDisable(true);
        loadComboBoxData();
        setupComboBoxConverters();
        datePicker.setValue(LocalDate.now());
    }

    private void loadComboBoxData() {
        tenantComboBox.setItems(FXCollections.observableArrayList(tenantDao.findAll()));
        serviceComboBox.setItems(FXCollections.observableArrayList(serviceDao.findAll()));
        organizationComboBox.setItems(FXCollections.observableArrayList(organizationDao.findAll()));
    }

    private void setupComboBoxConverters() {
        tenantComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(Tenant t) {
                return t == null ? null : t.getFullName() + " (ID:" + t.getId() + ")";
            }
            @Override public Tenant fromString(String s) { return null; }
        });

        serviceComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(Service s) {
                return s == null ? null : s.getName() + " (ID:" + s.getId() + ")";
            }
            @Override public Service fromString(String s) { return null; }
        });

        organizationComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(Organization o) {
                return o == null ? null : o.getOrganizationName() + " (ID:" + o.getId() + ")";
            }
            @Override public Organization fromString(String s) { return null; }
        });
    }

    @Override
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

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
    }

    @Override
    public boolean isOkClicked() {
        return okClicked;
    }

    @Override
    public Payment getEntity() {
        return payment;
    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            updatePaymentData();
            okClicked = true;
            dialogStage.close();
        }
    }

    private void updatePaymentData() {
        payment.setTenant(tenantComboBox.getValue());
        payment.setService(serviceComboBox.getValue());
        payment.setOrganization(organizationComboBox.getValue());
        payment.setDate(datePicker.getValue());

    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        StringBuilder errorMessage = new StringBuilder();
        if (tenantComboBox.getValue() == null) {
            errorMessage.append("Не выбран плательщик!\n");
        }
        if (serviceComboBox.getValue() == null) {
            errorMessage.append("Не выбрана услуга!\n");
        }
        if (organizationComboBox.getValue() == null) {
            errorMessage.append("Не выбрана организация-получатель!\n");
        }
        if (datePicker.getValue() == null) {
            errorMessage.append("Не указана дата платежа!\n");
        } else {
            LocalDate paymentDate = datePicker.getValue();
            LocalDate today = LocalDate.now();
            if (paymentDate.isAfter(today)) {
                errorMessage.append("Дата платежа не может быть в будущем!\n");
            }
            if (paymentDate.isBefore(today.minusYears(1))) {
                errorMessage.append("Дата платежа не может быть старше 1 года!\n");
            }
        }
        if (amountField.getText() == null || amountField.getText().trim().isEmpty()) {
            errorMessage.append("Не указана сумма платежа!\n");
        } else {
            try {
                BigDecimal amount = new BigDecimal(amountField.getText().replace(",", "."));
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    errorMessage.append("Сумма платежа должна быть положительной!\n");
                } else if (amount.compareTo(new BigDecimal("1000000")) > 0) {
                    errorMessage.append("Сумма платежа слишком большая (максимум 1 000 000)!\n");
                } else if (!AMOUNT_PATTERN.matcher(amountField.getText()).matches()) {
                    errorMessage.append("Сумма должна быть числом с максимум 2 знаками после запятой!\n");
                }
            } catch (NumberFormatException e) {
                errorMessage.append("Некорректный формат суммы платежа!\n");
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