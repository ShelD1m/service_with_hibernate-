package com.dmitry.hibernate_1.controller;

import javafx.stage.Stage;

public interface DialogController<T> {
    void setDialogStage(Stage dialogStage);
    void setEntity(T entity); // Для передачи сущности на редактирование или новой для заполнения
    boolean isOkClicked();
    T getEntity(); // Для получения измененной/новой сущности
}