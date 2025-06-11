package com.dmitry.hibernate_1.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class TenantQueryDialogController {

    @FXML
    private TextField tenantNameField;

    private Stage dialogStage;
    private boolean okClicked = false;

    public void setDialogStage(Stage dialogStage) { this.dialogStage = dialogStage; }
    public boolean isOkClicked() { return okClicked; }
    public String getTenantName() { return tenantNameField.getText(); }

    @FXML private void handleOk() {
        okClicked = true;
        dialogStage.close();
    }
    @FXML private void handleCancel() {
        dialogStage.close();
    }
}