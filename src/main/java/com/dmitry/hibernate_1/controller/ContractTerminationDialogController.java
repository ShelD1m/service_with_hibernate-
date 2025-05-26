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
    private ContractSigning relatedContract; // Связанный договор, из которого берем данные для снэпшота
    private boolean okClicked = false;
    private boolean isEditMode = false; // Расторжение обычно не "редактируется", а создается один раз. Но для полноты...

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

    /**
     * Устанавливает сущность для редактирования/заполнения.
     * @param contractTermination Объект ContractTermination.
     * @param relatedContract Связанный ContractSigning. Обязателен, если это новое расторжение.
     */
    public void setEntity(ContractTermination contractTermination, ContractSigning relatedContract) {
        this.contractTermination = contractTermination;
        this.relatedContract = relatedContract; // Сохраняем для снэпшотов

        // ContractTermination.contractNumberFk это PK, равный relatedContract.contractNumber
        // и @MapsId свяжет его при сохранении.
        // Поле 'contractNumberFk' в модели будет заполнено автоматически через @MapsId, если
        // правильно установлен объект this.contractTermination.setContractSigning(relatedContract)
        // Но для отображения можно сделать так:

        if (relatedContract != null) {
            contractNumberFkField.setText(relatedContract.getContractNumber()+"");
        } else if (contractTermination.getContractNumberFk()+"" != null) {
            // Режим редактирования (редко для расторжения), relatedContract может быть null, если загрузили только расторжение
            contractNumberFkField.setText(contractTermination.getContractNumberFk()+"");
        }


        if (contractTermination.getTerminationDate() != null) { // Для режима редактирования
            isEditMode = true; // Или если ID расторжения уже есть (а у нас PK = номеру договора)
            terminationDatePicker.setValue(contractTermination.getTerminationDate());
            reasonTextArea.setText(contractTermination.getReason());
            landlordIdSnapshotField.setText(contractTermination.getLandlordIdSnapshot() != null ? String.valueOf(contractTermination.getLandlordIdSnapshot()) : "N/A");
            tenantIdSnapshotField.setText(contractTermination.getTenantIdSnapshot() != null ? String.valueOf(contractTermination.getTenantIdSnapshot()) : "N/A");
            apartmentIdSnapshotField.setText(contractTermination.getApartmentIdSnapshot() != null ? String.valueOf(contractTermination.getApartmentIdSnapshot()) : "N/A");
        } else { // Новое расторжение
            isEditMode = false;
            if (relatedContract != null) { // Заполняем снэпшоты из связанного договора
                landlordIdSnapshotField.setText(String.valueOf(relatedContract.getLandlord().getLandlordId()));
                tenantIdSnapshotField.setText(String.valueOf(relatedContract.getTenant().getId()));
                apartmentIdSnapshotField.setText(String.valueOf(relatedContract.getApartment().getApartmentId()));
            }
        }
    }

    // Перегрузка стандартного setEntity, чтобы обеспечить передачу relatedContract
    // Этот метод будет вызываться из MainWindowController
    @Override
    public void setEntity(ContractTermination entity) {
        // Этот метод сам по себе недостаточен. Нужен relatedContract.
        // Предполагается, что MainWindowController вызовет setEntity(termination, signiningContract)
        // или нужно изменить логику вызова.
        // Для простоты, если entity уже имеет contractSigning, используем его.
        if(entity != null && entity.getContractSigning() != null) {
            this.setEntity(entity, entity.getContractSigning());
        } else {
            this.contractTermination = entity; // Будет новый или пустой
            // Можно здесь вызывать исключение, если relatedContract не передан для нового расторжения
        }
    }


    @Override
    public boolean isOkClicked() { return okClicked; }

    @Override
    public ContractTermination getEntity() { return contractTermination; }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            // Связь ContractTermination -> ContractSigning (через @MapsId)
            // Должна быть установлена ПЕРЕД сохранением, если это новое расторжение.
            // В MainWindowController при вызове диалога нужно передать объект ContractSigning
            // и здесь установить: this.contractTermination.setContractSigning(this.relatedContract);
            if (!isEditMode && relatedContract != null) {
                this.contractTermination.setContractSigning(this.relatedContract);
                // Также заполняем поля-снэпшоты для новой записи, если они не были установлены в setEntity
                // или если пользователь их как-то мог изменить (хотя поля нередактируемы)
                contractTermination.setLandlordIdSnapshot(relatedContract.getLandlord().getLandlordId());
                contractTermination.setTenantIdSnapshot(relatedContract.getTenant().getId());
                contractTermination.setApartmentIdSnapshot(relatedContract.getApartment().getApartmentId());
            } // Иначе для редактирования, ContractSigning уже должен быть связан.

            contractTermination.setTerminationDate(terminationDatePicker.getValue());
            contractTermination.setReason(reasonTextArea.getText());

            okClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() { dialogStage.close(); }

    private boolean isInputValid() {
        String errorMessage = "";
        if (terminationDatePicker.getValue() == null) {
            errorMessage += "Не указана дата расторжения!\n";
        }
        if (relatedContract == null && !isEditMode) { // Для нового расторжения нужен связанный договор
            errorMessage += "Не указан договор для расторжения! (Внутренняя ошибка)\n";
        }
        // Причина может быть пустой

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