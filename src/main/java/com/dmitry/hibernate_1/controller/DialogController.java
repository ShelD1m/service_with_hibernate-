package com.dmitry.hibernate_1.controller;

import javafx.stage.Stage;

public interface DialogController<T> {
    void setDialogStage(Stage dialogStage);
    void setEntity(T entity);
    boolean isOkClicked();
    T getEntity();
}