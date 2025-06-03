package com.dmitry.hibernate_1.controller;

import com.dmitry.hibernate_1.model.ContractSigning;
import com.dmitry.hibernate_1.model.ContractTermination;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;

public class ContractTerminationDialogController implements DialogController<ContractTermination> {

    @FXML private TextField contractNumberFkField; // Это PK и FK на ContractSigning.Номер
    @FXML private DatePicker terminationDatePicker;
    @FXML private TextArea reasonTextArea;
    @FXML private TextField landlordIdSnapshotField;
    @FXML private TextField tenantIdSnapshotField;
    @FXML private TextField apartmentIdSnapshotField;

    private Stage dialogStage;
    private ContractTermination contractTermination;
    private ContractSigning relatedContract;
    private boolean okClicked = false;
    private boolean isEditMode = false;

    @FXML
    private void initialize() {
        contractNumberFkField.setDisable(true);
        landlordIdSnapshotField.setDisable(true);
        tenantIdSnapshotField.setDisable(true);
        apartmentIdSnapshotField.setDisable(true);
        terminationDatePicker.setValue(LocalDate.now());
    }

    @Override
    public void setDialogStage(Stage dialogStage) { this.dialogStage = dialogStage; }

    public void setEntity(ContractTermination contractTermination, ContractSigning relatedContract) {
        this.contractTermination = contractTermination;
        this.relatedContract = relatedContract;


        if (relatedContract != null) {
            contractNumberFkField.setText(relatedContract.getContractNumber()+"");
        } else if (contractTermination.getContractNumberFk()+"" != null) {
            contractNumberFkField.setText(contractTermination.getContractNumberFk()+"");
        }


        if (contractTermination.getTerminationDate() != null) {
            isEditMode = true;
            terminationDatePicker.setValue(contractTermination.getTerminationDate());
            reasonTextArea.setText(contractTermination.getReason());
            landlordIdSnapshotField.setText(contractTermination.getLandlordIdSnapshot() != null ? String.valueOf(contractTermination.getLandlordIdSnapshot()) : "N/A");
            tenantIdSnapshotField.setText(contractTermination.getTenantIdSnapshot() != null ? String.valueOf(contractTermination.getTenantIdSnapshot()) : "N/A");
            apartmentIdSnapshotField.setText(contractTermination.getApartmentIdSnapshot() != null ? String.valueOf(contractTermination.getApartmentIdSnapshot()) : "N/A");
        } else {
            isEditMode = false;
            if (relatedContract != null) {
                landlordIdSnapshotField.setText(String.valueOf(relatedContract.getLandlord().getLandlordId()));
                tenantIdSnapshotField.setText(String.valueOf(relatedContract.getTenant().getId()));
                apartmentIdSnapshotField.setText(String.valueOf(relatedContract.getApartment().getApartmentId()));
            }
        }
    }

    @Override
    public void setEntity(ContractTermination entity) {
        if(entity != null && entity.getContractSigning() != null) {
            this.setEntity(entity, entity.getContractSigning());
        } else {
            this.contractTermination = entity;
        }
    }


    @Override
    public boolean isOkClicked() { return okClicked; }

    @Override
    public ContractTermination getEntity() { return contractTermination; }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            if (!isEditMode && relatedContract != null) {
                this.contractTermination.setContractSigning(this.relatedContract);
                contractTermination.setLandlordIdSnapshot(relatedContract.getLandlord().getLandlordId());
                contractTermination.setTenantIdSnapshot(relatedContract.getTenant().getId());
                contractTermination.setApartmentIdSnapshot(relatedContract.getApartment().getApartmentId());
            }

            contractTermination.setTerminationDate(terminationDatePicker.getValue());
            contractTermination.setReason(reasonTextArea.getText());

            okClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() { dialogStage.close(); }

    private boolean isInputValid() {
        StringBuilder errorMessage = new StringBuilder();
        if (terminationDatePicker.getValue() == null) {
            errorMessage.append("Не указана дата расторжения!\n");
        } else {
            LocalDate terminationDate = terminationDatePicker.getValue();
            if (terminationDate.isAfter(LocalDate.now())) {
                errorMessage.append("Дата расторжения не может быть в будущем!\n");
            }
            if (relatedContract != null && relatedContract.getSigningDate() != null &&
                    terminationDate.isBefore(relatedContract.getSigningDate())) {
                errorMessage.append("Дата расторжения не может быть раньше даты подписания контракта!\n");
            }
        }
        if (reasonTextArea.getText() == null || reasonTextArea.getText().trim().isEmpty()) {
            errorMessage.append("Не указана причина расторжения!\n");
        } else if (reasonTextArea.getText().length() > 1000) {
            errorMessage.append("Причина расторжения слишком длинная (максимум 1000 символов)!\n");
        }
        if (relatedContract == null && !isEditMode) {
            errorMessage.append("Не указан договор для расторжения! (Внутренняя ошибка)\n");
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