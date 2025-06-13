package com.dmitry.hibernate_1.controller;

import com.dmitry.hibernate_1.MainApp;
import com.dmitry.hibernate_1.dao.*;
import com.dmitry.hibernate_1.model.*;
import com.dmitry.hibernate_1.util.HibernateUtil;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.hibernate.Session;

import java.time.LocalDate;
import java.time.Instant;
import java.time.ZoneId;

import java.time.format.DateTimeParseException;
import java.util.*;


import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;

public class MainWindowController {

    @FXML
    private Label entityLabel;
    @FXML
    private TableView<Object> mainTableView;
    @FXML
    private Button addButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private TextField searchIdField;
    @FXML
    private Button searchButton;
    @FXML
    private Button queryButton;
    @FXML
    private Button terminateContractButton;

    private final OrganizationDao organizationDao = new OrganizationDaoImpl();
    private final AddressDao addressDao = new AddressDaoImpl();
    private final ApartmentDao apartmentDao = new ApartmentDaoImpl();
    private final ContractSigningDao signContractDao = new ContractSigningDaoImpl();
    private final ContractTerminationDao contractTerminationDao = new ContractTerminationDaoImpl();
    private final LandlordDao landlordDao = new LandlordDaoImpl();
    private final PaymentDao paymentDao = new PaymentDaoImpl();
    private final ServiceDao serviceDao = new ServiceDaoImpl();
    private final TenantDao tenantDao = new TenantDaoImpl();

    private enum CurrentEntityType {
        NONE("Выберите сущность"),
        ORGANIZATION("Организации", Organization.class),
        ADDRESS("Адреса", Address.class),
        APARTMENT("Квартиры", Apartment.class),
        SIGN_CONTRACT("Договоры (Подписание)", ContractSigning.class),
        CONTRACT_TERMINATION("Договоры (Расторжение)", ContractTermination.class),
        LANDLORD("Арендодатели", Landlord.class),
        PAYMENT("Платежи", Payment.class),
        SERVICE("Услуги", Service.class),
        TENANT("Квартиросъемщики", Tenant.class);

        private final String displayName;
        private final Class<?> entityClass;

        CurrentEntityType(String displayName, Class<?> entityClass) {
            this.displayName = displayName;
            this.entityClass = entityClass;
        }

        CurrentEntityType(String displayName) {
            this.displayName = displayName;
            this.entityClass = null;
        }

        public String getDisplayName() {
            return displayName;
        }

        public Class<?> getEntityClass() {
            return entityClass;
        }
    }

    @FXML
    private void handleQueryButton() {
        switch (currentView) {
            case APARTMENT -> openApartmentQueryDialog();
            case PAYMENT -> openPaymentQueryDialog();
            case LANDLORD -> openLandlordQueryDialog();
            case TENANT -> openTenantQueryDialog();
            case ORGANIZATION -> openOrganizationQueryDialog();
            default ->
                    showAlert(Alert.AlertType.INFORMATION, "Нет запроса", "Для этой сущности нет расширенного запроса.");
        }
    }


    private void executeQuery5(LocalDate fromDate) {
        String hql = "SELECT s.name, p.date FROM Payment p JOIN p.service s WHERE p.date >= :fromDate ORDER BY p.date DESC";

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Object[]> results = session.createQuery(hql, Object[].class)
                    .setParameter("fromDate", fromDate)
                    .list();

            tableData.clear();
            mainTableView.getColumns().clear();

            TableColumn<Object, String> serviceCol = new TableColumn<>("Услуга");
            serviceCol.setCellValueFactory(cell -> new SimpleStringProperty((String) ((Object[]) cell.getValue())[0]));

            TableColumn<Object, LocalDate> dateCol = new TableColumn<>("Дата оплаты");
            dateCol.setCellValueFactory(cell -> new SimpleObjectProperty<>((LocalDate) ((Object[]) cell.getValue())[1]));

            mainTableView.getColumns().addAll(serviceCol, dateCol);
            tableData.addAll(results);
            entityLabel.setText("Запрос: Платежи с " + fromDate);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Ошибка запроса", "Не удалось выполнить запрос: " + e.getMessage());
        }
    }


    private void openPaymentQueryDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PaymentQueryDialog.fxml"));
            Parent page = loader.load();
            PaymentQueryDialogController controller = loader.getController();

            Stage dialogStage = createDialogStage("Фильтр платежей по дате", page);
            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();

            if (controller.isOkClicked()) {
                LocalDate fromDate = controller.getFromDate();
                if (fromDate != null) {
                    executeQuery5(fromDate);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void executeQuery2(String landlordName) {
        String hql = "SELECT l.fullName, a.apartmentId FROM Apartment a JOIN a.landlordId l"
                + (landlordName != null && !landlordName.isBlank() ? " WHERE l.fullName LIKE :landlordName" : "")
                + " ORDER BY l.fullName";

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            org.hibernate.query.Query<Object[]> query = session.createQuery(hql, Object[].class);

            if (landlordName != null && !landlordName.isBlank()) {
                query.setParameter("landlordName", "%" + landlordName + "%");
            }

            List<Object[]> results = query.list();

            tableData.clear();
            mainTableView.getColumns().clear();

            TableColumn<Object, String> nameCol = new TableColumn<>("ФИО");
            nameCol.setCellValueFactory(cell -> new SimpleStringProperty((String) ((Object[]) cell.getValue())[0]));

            TableColumn<Object, Integer> idCol = new TableColumn<>("ID Квартиры");
            idCol.setCellValueFactory(cell -> new SimpleObjectProperty<>((Integer) ((Object[]) cell.getValue())[1]));

            mainTableView.getColumns().addAll(nameCol, idCol);
            tableData.addAll(results);

            entityLabel.setText("Запрос: Арендодатели и их квартиры");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Ошибка запроса", "Не удалось выполнить запрос: " + e.getMessage());
        }
    }


    private void openLandlordQueryDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LandlordQueryDialog.fxml"));
            Parent page = loader.load();
            LandlordQueryDialogController controller = loader.getController();

            Stage dialogStage = createDialogStage("Фильтр арендодателей по имени", page);
            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();

            if (controller.isOkClicked()) {
                String name = controller.getLandlordName();
                executeQuery2(name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void executeQuery3() {
        String hql = "SELECT DISTINCT t.fullName FROM Tenant t JOIN t.contractSignings s";

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<String> results = session.createQuery(hql, String.class).list();

            tableData.clear();
            mainTableView.getColumns().clear();

            TableColumn<Object, String> nameCol = new TableColumn<>("ФИО жильца");
            nameCol.setCellValueFactory(cell -> new SimpleStringProperty((String) cell.getValue()));

            mainTableView.getColumns().add(nameCol);
            tableData.addAll(results);

            entityLabel.setText("Запрос: Жильцы с договорами");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Ошибка запроса", "Не удалось выполнить запрос: " + e.getMessage());
        }
    }


    private void openTenantQueryDialog() {
        executeQuery3();
    }


    private void openOrganizationQueryDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/OrganizationQueryDialog.fxml"));
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Фильтр организаций по названию");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(mainTableView.getScene().getWindow());
            dialogStage.setScene(new Scene(page));

            OrganizationQueryDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            if (controller.isOkClicked()) {
                String orgNameFromDialog = controller.getOrganizationName();

                System.out.println("Диалог закрыт с OK. Ищем организации, содержащие: '" + orgNameFromDialog + "'");

                executeQuery4(orgNameFromDialog);

            } else {
                System.out.println("Диалог закрыт кнопкой 'Отмена' или крестиком.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void executeQuery4(String organizationName) {
        if (organizationName == null || organizationName.isBlank()) {
            System.out.println("Поле для поиска пустое. Загружаем все организации...");
            handleOrganizationsButton(null);
            return;
        }

        String hql = """
        SELECT o.organizationName, s.name, COUNT(p.id)
        FROM Payment p
        JOIN p.organization o
        JOIN p.service s
        WHERE o.organizationName LIKE :orgNamePattern
        GROUP BY o.organizationName, s.name
        HAVING COUNT(p.id) > 0
        ORDER BY COUNT(p.id) DESC
        """;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Object[]> results = session.createQuery(hql, Object[].class)
                    .setParameter("orgNamePattern", "%" + organizationName + "%")
                    .list();

            tableData.clear();
            mainTableView.getColumns().clear();

            if (results.isEmpty()) {
                mainTableView.setPlaceholder(new Label("Организации, содержащие '" + organizationName + "', не найдены."));
            }

            TableColumn<Object, String> orgCol = new TableColumn<>("Организация");
            orgCol.setCellValueFactory(cell -> new SimpleStringProperty((String) ((Object[]) cell.getValue())[0]));

            TableColumn<Object, String> serviceCol = new TableColumn<>("Услуга");
            serviceCol.setCellValueFactory(cell -> new SimpleStringProperty((String) ((Object[]) cell.getValue())[1]));

            TableColumn<Object, Long> countCol = new TableColumn<>("Кол-во оплат");
            countCol.setCellValueFactory(cell -> new SimpleObjectProperty<>(((Number) ((Object[]) cell.getValue())[2]).longValue()));

            mainTableView.getColumns().addAll(orgCol, serviceCol, countCol);
            tableData.addAll(results);

            entityLabel.setText("Результат запроса по организациям");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Ошибка запроса", "Не удалось выполнить запрос: " + e.getMessage());
        }
    }


    private Stage createDialogStage(String title, Parent content) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle(title);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setScene(new Scene(content));
        return dialogStage;
    }

    private CurrentEntityType currentView = CurrentEntityType.NONE;
    private final ObservableList<Object> tableData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        entityLabel.setText(CurrentEntityType.NONE.getDisplayName());
        mainTableView.setItems(tableData);
        mainTableView.setPlaceholder(new Label("Нет данных для отображения."));
        updateButtonStates();

        mainTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            updateButtonStates();
        });
    }

    private void updateButtonStates() {
        boolean itemSelected = mainTableView.getSelectionModel().getSelectedItem() != null;
        boolean viewSelected = currentView != CurrentEntityType.NONE;
        addButton.setDisable(!viewSelected);
        editButton.setDisable(!itemSelected || !viewSelected);
        deleteButton.setDisable(!itemSelected || !viewSelected);
        searchButton.setDisable(!viewSelected);
        searchIdField.setDisable(!viewSelected);
        queryButton.setDisable(false);
    }

    private void openApartmentQueryDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ApartmentQueryDialog.fxml"));
            Parent page = loader.load();
            ApartmentQueryDialogController controller = loader.getController();

            Stage dialogStage = createDialogStage("Фильтр квартир по кол-ву комнат", page);
            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();

            if (controller.isOkClicked()) {
                String roomCountStr = controller.getRoomCount();
                try {
                    int minRooms = Integer.parseInt(roomCountStr);
                    executeQuery1(minRooms);
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Ошибка", "Введено неверное числовое значение.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void executeQuery1(int minRooms) {
        String sql = "SELECT apartment_id, room_count, square_meters FROM rental.apartment WHERE room_count > :minRooms ORDER BY room_count DESC, square_meters DESC";

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Object[]> results = session.createNativeQuery(sql)
                    .setParameter("minRooms", minRooms)
                    .list();

            tableData.clear();
            mainTableView.getColumns().clear();
            TableColumn<Object, Integer> idCol = new TableColumn<>("Кв. ID");
            idCol.setCellValueFactory(cell -> new SimpleObjectProperty<>((Integer) ((Object[]) cell.getValue())[0]));
            TableColumn<Object, Integer> roomCol = new TableColumn<>("Комнат");
            roomCol.setCellValueFactory(cell -> new SimpleObjectProperty<>((Integer) ((Object[]) cell.getValue())[1]));
            TableColumn<Object, java.math.BigDecimal> areaCol = new TableColumn<>("Площадь");
            areaCol.setCellValueFactory(cell -> new SimpleObjectProperty<>((java.math.BigDecimal) ((Object[]) cell.getValue())[2]));
            mainTableView.getColumns().addAll(idCol, roomCol, areaCol);
            tableData.addAll(results);
            entityLabel.setText("Квартиры с кол-вом комнат > " + minRooms);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось выполнить запрос: " + e.getMessage());
        }
    }


    @FXML
    void handleOrganizationsButton(ActionEvent event) {
        loadDataForEntityType(CurrentEntityType.ORGANIZATION, organizationDao.findAll());
    }

    @FXML
    void handleAddressesButton(ActionEvent event) {
        loadDataForEntityType(CurrentEntityType.ADDRESS, addressDao.findAll());
    }

    @FXML
    void handleApartmentsButton(ActionEvent event) {
        loadDataForEntityType(CurrentEntityType.APARTMENT, apartmentDao.findAll());
    }

    @FXML
    void handleSignContractsButton(ActionEvent event) {
        loadDataForEntityType(CurrentEntityType.SIGN_CONTRACT, signContractDao.findAll());
    }

    @FXML
    void handleContractTerminationsButton(ActionEvent event) {
        loadDataForEntityType(CurrentEntityType.CONTRACT_TERMINATION, contractTerminationDao.findAll());
    }

    @FXML
    void handleLandlordsButton(ActionEvent event) {
        loadDataForEntityType(CurrentEntityType.LANDLORD, landlordDao.findAll());
    }

    @FXML
    void handlePaymentsButton(ActionEvent event) {
        loadDataForEntityType(CurrentEntityType.PAYMENT, paymentDao.findAll());
    }

    @FXML
    void handleServicesButton(ActionEvent event) {
        loadDataForEntityType(CurrentEntityType.SERVICE, serviceDao.findAll());
    }

    @FXML
    void handleTenantsButton(ActionEvent event) {
        loadDataForEntityType(CurrentEntityType.TENANT, tenantDao.findAll());
    }


    private <T> void loadDataForEntityType(CurrentEntityType entityType, List<T> dataList) {
        currentView = entityType;
        entityLabel.setText(entityType.getDisplayName());
        mainTableView.getColumns().clear();
        tableData.clear();

        if (dataList == null) {
            dataList = Collections.emptyList();
        }

        if (entityType.getEntityClass() == null) {
            mainTableView.setPlaceholder(new Label("Выберите сущность для отображения."));
            updateButtonStates();
            return;
        }

        if (entityType == CurrentEntityType.ORGANIZATION) {
            TableColumn<Object, Integer> idCol = new TableColumn<>("ID");
            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            TableColumn<Object, String> nameCol = new TableColumn<>("Название");
            nameCol.setCellValueFactory(new PropertyValueFactory<>("organizationName"));
            TableColumn<Object, String> taxCol = new TableColumn<>("ИНН");
            taxCol.setCellValueFactory(new PropertyValueFactory<>("taxId"));
            TableColumn<Object, String> webCol = new TableColumn<>("Сайт");
            webCol.setCellValueFactory(new PropertyValueFactory<>("websiteUrl"));
            mainTableView.getColumns().addAll(idCol, nameCol, taxCol, webCol);
        } else if (entityType == CurrentEntityType.ADDRESS) {
            TableColumn<Object, Integer> idCol = new TableColumn<>("Addr.ID");
            idCol.setCellValueFactory(new PropertyValueFactory<>("addressId"));
            TableColumn<Object, Integer> aptIdCol = new TableColumn<>("Кв.ID");
            aptIdCol.setCellValueFactory(cellData -> {
                Address adr = (Address) cellData.getValue();
                return adr.getApartment() != null ? new SimpleObjectProperty<>(adr.getApartment().getApartmentId()) : new SimpleObjectProperty<>(null);
            });
            TableColumn<Object, String> cityCol = new TableColumn<>("Город");
            cityCol.setCellValueFactory(new PropertyValueFactory<>("cityName"));
            TableColumn<Object, String> streetCol = new TableColumn<>("Улица");
            streetCol.setCellValueFactory(new PropertyValueFactory<>("streetName"));
            TableColumn<Object, String> houseCol = new TableColumn<>("Дом");
            houseCol.setCellValueFactory(new PropertyValueFactory<>("houseNumber"));
            TableColumn<Object, String> aptNumCol = new TableColumn<>("Кв(номер)");
            aptNumCol.setCellValueFactory(new PropertyValueFactory<>("apartmentNumber"));
            mainTableView.getColumns().addAll(idCol, aptIdCol, cityCol, streetCol, houseCol, aptNumCol);
        } else if (entityType == CurrentEntityType.APARTMENT) {
            TableColumn<Object, Integer> idCol = new TableColumn<>("Кв.ID");
            idCol.setCellValueFactory(new PropertyValueFactory<>("apartmentId"));
            TableColumn<Object, String> ownerCol = new TableColumn<>("Владелец");
            ownerCol.setCellValueFactory(cellData -> {
                Apartment apt = (Apartment) cellData.getValue();
                return apt.getLandlordId() != null ? new SimpleStringProperty(apt.getLandlordId().getFullName() + " (ID:" + apt.getLandlordId().getLandlordId() + ")") : new SimpleStringProperty("");
            });
            TableColumn<Object, Integer> roomsCol = new TableColumn<>("Комнат");
            roomsCol.setCellValueFactory(new PropertyValueFactory<>("roomCount"));
            TableColumn<Object, BigDecimal> areaCol = new TableColumn<>("Площадь");
            areaCol.setCellValueFactory(new PropertyValueFactory<>("squareMeters"));
            mainTableView.getColumns().addAll(idCol, ownerCol, roomsCol, areaCol);
        } else if (entityType == CurrentEntityType.LANDLORD) {
            TableColumn<Object, Integer> idCol = new TableColumn<>("ID");
            idCol.setCellValueFactory(new PropertyValueFactory<>("landlordId"));
            TableColumn<Object, String> nameCol = new TableColumn<>("ФИО");
            nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
            TableColumn<Object, String> passportCol = new TableColumn<>("Паспорт");
            passportCol.setCellValueFactory(new PropertyValueFactory<>("passportNumber"));
            TableColumn<Object, String> phoneCol = new TableColumn<>("Телефон");
            phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
            mainTableView.getColumns().addAll(idCol, nameCol, passportCol, phoneCol);
        } else if (entityType == CurrentEntityType.TENANT) {
            TableColumn<Object, Integer> idCol = new TableColumn<>("ID");
            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            TableColumn<Object, String> nameCol = new TableColumn<>("ФИО");
            nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
            TableColumn<Object, String> passportCol = new TableColumn<>("Паспорт");
            passportCol.setCellValueFactory(new PropertyValueFactory<>("passportNumber"));
            TableColumn<Object, String> phoneCol = new TableColumn<>("Телефон");
            phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
            mainTableView.getColumns().addAll(idCol, nameCol, passportCol, phoneCol);
        } else if (entityType == CurrentEntityType.SERVICE) {
            TableColumn<Object, Integer> idCol = new TableColumn<>("Код");
            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            TableColumn<Object, String> nameCol = new TableColumn<>("Название");
            nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            TableColumn<Object, BigDecimal> costCol = new TableColumn<>("Стоимость");
            costCol.setCellValueFactory(new PropertyValueFactory<>("cost"));
            TableColumn<Object, String> typeCol = new TableColumn<>("Тип");
            typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
            mainTableView.getColumns().addAll(idCol, nameCol, costCol, typeCol);
        } else if (entityType == CurrentEntityType.SIGN_CONTRACT) {
            TableColumn<Object, Integer> idCol = new TableColumn<>("ID дог.");
            idCol.setCellValueFactory(new PropertyValueFactory<>("contractNumber"));
            TableColumn<Object, String> landlordCol = new TableColumn<>("Арендодатель");
            landlordCol.setCellValueFactory(cellData -> {
                ContractSigning sc = (ContractSigning) cellData.getValue();
                return sc.getLandlord() != null ? new SimpleStringProperty(sc.getLandlord().getFullName()) : new SimpleStringProperty("");
            });
            TableColumn<Object, String> tenantCol = new TableColumn<>("Арендатор");
            tenantCol.setCellValueFactory(cellData -> {
                ContractSigning sc = (ContractSigning) cellData.getValue();
                return sc.getTenant() != null ? new SimpleStringProperty(sc.getTenant().getFullName()) : new SimpleStringProperty("");
            });
            TableColumn<Object, String> apartmentCol = new TableColumn<>("Квартира (ID)");
            apartmentCol.setCellValueFactory(cellData -> {
                ContractSigning sc = (ContractSigning) cellData.getValue();
                return sc.getApartment() != null ? new SimpleStringProperty(String.valueOf(sc.getApartment().getApartmentId())) : new SimpleStringProperty("");
            });
            TableColumn<Object, LocalDate> dateCol = new TableColumn<>("Дата подп.");
            dateCol.setCellValueFactory(new PropertyValueFactory<>("signingDate"));
            TableColumn<Object, String> durationCol = new TableColumn<>("Срок");
            durationCol.setCellValueFactory(new PropertyValueFactory<>("term"));
            mainTableView.getColumns().addAll(idCol, landlordCol, tenantCol, apartmentCol, dateCol, durationCol);
        } else if (entityType == CurrentEntityType.CONTRACT_TERMINATION) {
            TableColumn<Object, Integer> idCol = new TableColumn<>("ID расторж. (ID дог.)");
            idCol.setCellValueFactory(new PropertyValueFactory<>("contractNumberFk"));
            TableColumn<Object, String> reasonCol = new TableColumn<>("Причина");
            reasonCol.setCellValueFactory(new PropertyValueFactory<>("reason"));
            TableColumn<Object, LocalDate> dateCol = new TableColumn<>("Дата расторж.");
            dateCol.setCellValueFactory(new PropertyValueFactory<>("terminationDate"));
            TableColumn<Object, Integer> landlordSnapCol = new TableColumn<>("ID Арендод.(snap)");
            landlordSnapCol.setCellValueFactory(new PropertyValueFactory<>("landlordIdSnapshot"));
            mainTableView.getColumns().addAll(idCol, reasonCol, dateCol, landlordSnapCol);
        } else if (entityType == CurrentEntityType.PAYMENT) {
            TableColumn<Object, Integer> idCol = new TableColumn<>("ID плат.");
            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            TableColumn<Object, String> tenantCol = new TableColumn<>("Плательщик (Арендатор)");
            tenantCol.setCellValueFactory(cellData -> {
                Payment p = (Payment) cellData.getValue();
                return p.getTenant() != null ? new SimpleStringProperty(p.getTenant().getFullName()) : new SimpleStringProperty("");
            });
            TableColumn<Object, Integer> serviceCol = new TableColumn<>("Услуга");
            serviceCol.setCellValueFactory(cellData -> {
                Payment p = (Payment) cellData.getValue();
                return new SimpleIntegerProperty(
                        p.getService() != null ? p.getService().getId() : 0
                ).asObject();
            });
            TableColumn<Object, String> orgCol = new TableColumn<>("Организация (получ.)");
            orgCol.setCellValueFactory(cellData -> {
                Payment p = (Payment) cellData.getValue();
                return p.getOrganization() != null ? new SimpleStringProperty(p.getOrganization().getOrganizationName()) : new SimpleStringProperty("");
            });
            TableColumn<Object, LocalDate> dateCol = new TableColumn<>("Дата платежа");
            dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
            mainTableView.getColumns().addAll(idCol, tenantCol, serviceCol, orgCol, dateCol);
        } else {
            if (entityType.getEntityClass() != null) {
                Field[] fields = entityType.getEntityClass().getDeclaredFields();
                for (Field field : fields) {
                    if (java.util.Collection.class.isAssignableFrom(field.getType()) ||
                            (field.getType().getName().startsWith("com.dmitry.hibernate_1.model") &&
                                    !isSimpleDisplayableRelatedField(field, entityType.getEntityClass()))) {
                        continue;
                    }
                    TableColumn<Object, Object> col = new TableColumn<>(field.getName());
                    col.setCellValueFactory(new PropertyValueFactory<>(field.getName()));
                    mainTableView.getColumns().add(col);
                }
            }
        }


        if (!dataList.isEmpty()) {
            tableData.addAll(dataList);
        } else {
            mainTableView.setPlaceholder(new Label("Нет данных для " + entityType.getDisplayName()));
        }
        updateButtonStates();
    }

    private boolean isSimpleDisplayableRelatedField(Field field, Class<?> ownerClass) {
        return false;
    }


    @FXML
    private void handleAddButton() {
        if (currentView == CurrentEntityType.NONE || currentView.getEntityClass() == null) {
            showAlert(Alert.AlertType.WARNING, "Не выбрана сущность", "Пожалуйста, выберите тип сущности для добавления.");
            return;
        }
        System.out.println("handleAddButton: currentView = " + currentView);

        try {
            Object newEntity = currentView.getEntityClass().getDeclaredConstructor().newInstance();
            System.out.println("Создан новый объект: " + newEntity.getClass().getSimpleName());
            boolean okClicked = openEntityDialog(newEntity, currentView.getDisplayName(), false);
            if (okClicked) {
                System.out.println("Диалог закрыт с OK. Сохранение сущности...");
                saveOrUpdateEntity(newEntity, false);
                reloadCurrentViewData();
            } else {
                System.out.println("Диалог закрыт с Cancel.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось создать или открыть диалог для добавления: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditButton() {
        Object selectedItem = mainTableView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert(Alert.AlertType.WARNING, "Не выбрана запись", "Пожалуйста, выберите запись для редактирования.");
            return;
        }
        if (currentView == CurrentEntityType.NONE || currentView.getEntityClass() == null) {
            return;
        }
        System.out.println("handleEditButton: currentView = " + currentView + ", selectedItem = " + selectedItem);

        boolean okClicked = openEntityDialog(selectedItem, currentView.getDisplayName(), true);

        if (okClicked) {
            System.out.println("Диалог закрыт с OK. Обновление сущности...");
            saveOrUpdateEntity(selectedItem, true);
            mainTableView.refresh();
        } else {
            System.out.println("Диалог закрыт с Cancel.");
        }
    }


    private void saveOrUpdateEntity(Object entity, boolean isEdit) {
        try {
            if (entity instanceof Organization) {
                if (isEdit) organizationDao.update((Organization) entity);
                else organizationDao.save((Organization) entity);
            } else if (entity instanceof Address) {
                if (isEdit) addressDao.update((Address) entity);
                else addressDao.save((Address) entity);
            } else if (entity instanceof Apartment) {
                if (isEdit) apartmentDao.update((Apartment) entity);
                else apartmentDao.save((Apartment) entity);
            } else if (entity instanceof ContractSigning) {
                if (isEdit) signContractDao.update((ContractSigning) entity);
                else signContractDao.save((ContractSigning) entity);
            } else if (entity instanceof ContractTermination) {
                if (isEdit) contractTerminationDao.update((ContractTermination) entity);
                else contractTerminationDao.save((ContractTermination) entity);
            } else if (entity instanceof Landlord) {
                if (isEdit) landlordDao.update((Landlord) entity);
                else landlordDao.save((Landlord) entity);
            } else if (entity instanceof Payment) {
                if (isEdit) paymentDao.update((Payment) entity);
                else paymentDao.save((Payment) entity);
            } else if (entity instanceof Service) {
                if (isEdit) serviceDao.update((Service) entity);
                else serviceDao.save((Service) entity);
            } else if (entity instanceof Tenant) {
                if (isEdit) tenantDao.update((Tenant) entity);
                else tenantDao.save((Tenant) entity);
            } else {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Неизвестный тип сущности для сохранения/обновления.");
                return;
            }

            String action = isEdit ? "обновлена" : "добавлена";
            showAlert(Alert.AlertType.INFORMATION, "Успех", "Запись " + action + ".");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Ошибка сохранения", "Не удалось сохранить запись: " + e.getMessage() +
                    (e.getCause() != null ? "\nПричина: " + e.getCause().getMessage() : ""));
        }
    }

    private boolean openEntityDialog(Object entityData, String entityDisplayName, boolean isEditMode) {
        if (currentView == CurrentEntityType.NONE || currentView.getEntityClass() == null) return false;

        String fxmlFile = "";
        String dialogTitle = (isEditMode ? "Редактировать " : "Добавить ") + entityDisplayName;
        switch (currentView) {
            case ORGANIZATION:
                fxmlFile = "/fxml/OrganizationDialog.fxml";
                break;
            case ADDRESS:
                fxmlFile = "/fxml/AddressDialog.fxml";
                break;
            case APARTMENT:
                fxmlFile = "/fxml/ApartmentDialog.fxml";
                break;
            case SIGN_CONTRACT:
                fxmlFile = "/fxml/ContractSigningDialog.fxml";
                break;
            case CONTRACT_TERMINATION:
                fxmlFile = "/fxml/ContractTerminationDialog.fxml";
                break;
            case LANDLORD:
                fxmlFile = "/fxml/LandlordDialog.fxml";
                break;
            case PAYMENT:
                fxmlFile = "/fxml/PaymentDialog.fxml";
                break;
            case SERVICE:
                fxmlFile = "/fxml/ServiceDialog.fxml";
                break;
            case TENANT:
                fxmlFile = "/fxml/TenantDialog.fxml";
                break;
            default:
                showAlert(Alert.AlertType.WARNING, "Не реализовано", "Диалог для '" + entityDisplayName + "' еще не создан.");
                return false;
        }

        try {
            System.out.println("Пытаюсь загрузить FXML: " + fxmlFile + " для сущности " + entityData.getClass().getSimpleName());
            URL resourceUrl = MainApp.class.getResource(fxmlFile);
            if (resourceUrl == null) {
                System.err.println("FXML Resource NOT FOUND: " + fxmlFile);
                showAlert(Alert.AlertType.ERROR, "Ошибка FXML", "Не удалось найти FXML файл: " + fxmlFile + ". Проверьте путь в 'resources/fxml/'.");
                return false;
            }
            System.out.println("FXML Resource URL: " + resourceUrl);

            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(dialogTitle);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            if (mainTableView.getScene() != null && mainTableView.getScene().getWindow() != null) {
                dialogStage.initOwner(mainTableView.getScene().getWindow());
            } else {

            }


            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            DialogController<Object> controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setEntity(entityData);

            System.out.println("Показываю диалоговое окно...");
            dialogStage.showAndWait();

            return controller.isOkClicked();

        } catch (IOException e) {
            System.err.println("IOException при загрузке FXML: " + fxmlFile);
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Ошибка загрузки диалога", "Не удалось открыть диалоговое окно: " + e.getMessage());
            return false;
        } catch (IllegalStateException e) {
            System.err.println("IllegalStateException (возможно, FXML не найден или проблема с FXMLLoader): " + fxmlFile);
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Ошибка состояния", "Проблема при подготовке диалога: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Непредвиденная ошибка при открытии диалога: " + fxmlFile);
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Непредвиденная ошибка", "Произошла ошибка: " + e.getMessage());
            return false;
        }
    }

    @FXML
    private void handleTerminateContractButton() {
        Object selected = mainTableView.getSelectionModel().getSelectedItem();
        if (!(selected instanceof ContractSigning)) {
            showAlert(Alert.AlertType.WARNING, "Ошибка", "Выберите договор для расторжения.");
            return;
        }
        ContractSigning contractToTerminate = (ContractSigning) selected;
        if (contractToTerminate.getContractTermination() != null) {
            showAlert(Alert.AlertType.INFORMATION, "Информация", "Этот договор уже расторгнут.");
            return;
        }

        ContractTermination newTermination = new ContractTermination();
        String fxmlFile = "/fxml/ContractTerminationDialog.fxml";
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxmlFile));
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Расторжение договора №" + contractToTerminate.getContractNumber());
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(mainTableView.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            ContractTerminationDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setEntity(newTermination, contractToTerminate);

            dialogStage.showAndWait();

            if (controller.isOkClicked()) {
                ContractTermination returnedTermination = controller.getEntity();
                try {
                    contractTerminationDao.save(returnedTermination);
                    showAlert(Alert.AlertType.INFORMATION, "Успех", "Договор расторгнут.");
                    reloadCurrentViewData();
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Ошибка сохранения", "Не удалось сохранить расторжение: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Ошибка загрузки", "Не удалось открыть диалог расторжения.");
        }
    }

    @FXML
    private void handleDeleteButton() {
        Object selectedItem = mainTableView.getSelectionModel().getSelectedItem();
        if (selectedItem == null || currentView == CurrentEntityType.NONE) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение удаления");
        alert.setHeaderText("Вы уверены, что хотите удалить выбранную запись?");
        alert.setContentText(selectedItem.toString());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean deletedSuccessfully = false;
            try {
                switch (currentView) {
                    case ORGANIZATION:
                        organizationDao.delete((Organization) selectedItem);
                        deletedSuccessfully = true;
                        break;
                    case ADDRESS:
                        addressDao.delete((Address) selectedItem);
                        deletedSuccessfully = true;
                        break;
                    case APARTMENT:
                        apartmentDao.delete((Apartment) selectedItem);
                        deletedSuccessfully = true;
                        break;
                    case SIGN_CONTRACT:
                        signContractDao.delete((ContractSigning) selectedItem);
                        deletedSuccessfully = true;
                        break;
                    case CONTRACT_TERMINATION:
                        contractTerminationDao.delete((ContractTermination) selectedItem);
                        deletedSuccessfully = true;
                        break;
                    case LANDLORD:
                        landlordDao.delete((Landlord) selectedItem);
                        deletedSuccessfully = true;
                        break;
                    case PAYMENT:
                        paymentDao.delete((Payment) selectedItem);
                        deletedSuccessfully = true;
                        break;
                    case SERVICE:
                        serviceDao.delete((Service) selectedItem);
                        deletedSuccessfully = true;
                        break;
                    case TENANT:
                        tenantDao.delete((Tenant) selectedItem);
                        deletedSuccessfully = true;
                        break;
                    default:
                        showAlert(Alert.AlertType.ERROR, "Ошибка", "Неизвестный тип сущности для удаления.");
                        break;
                }

                if (deletedSuccessfully) {
                    tableData.remove(selectedItem);
                    showAlert(Alert.AlertType.INFORMATION, "Успех", "Запись удалена.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Ошибка удаления", "Не удалось удалить запись: " + e.getMessage() +
                        (e.getCause() != null ? "\nПричина: " + e.getCause().getMessage() : ""));
            }
        }
        updateButtonStates();
    }

    @FXML
    private void handleSearchButton() {
        String idText = searchIdField.getText().trim();
        if (currentView == CurrentEntityType.NONE) {
            showAlert(Alert.AlertType.WARNING, "Не выбрана сущность", "Пожалуйста, выберите тип сущности для поиска.");
            return;
        }
        if (idText.isEmpty()) {
            reloadCurrentViewData();
            return;
        }

        Object foundEntity = null;
        try {
            switch (currentView) {
                case SIGN_CONTRACT:
                    foundEntity = signContractDao.findById(idText);
                    break;
                case CONTRACT_TERMINATION:
                    foundEntity = contractTerminationDao.findById(idText);
                    break;
                case ORGANIZATION:
                case ADDRESS:
                case APARTMENT:
                case LANDLORD:
                case PAYMENT:
                case SERVICE:
                case TENANT:
                    int id = Integer.parseInt(idText);
                    if (currentView == CurrentEntityType.ORGANIZATION) foundEntity = organizationDao.findById(id);
                    else if (currentView == CurrentEntityType.ADDRESS) foundEntity = addressDao.findById(id);
                    else if (currentView == CurrentEntityType.APARTMENT) foundEntity = apartmentDao.findById(id);
                    else if (currentView == CurrentEntityType.LANDLORD) foundEntity = landlordDao.findById(id);
                    else if (currentView == CurrentEntityType.PAYMENT) foundEntity = paymentDao.findById(id);
                    else if (currentView == CurrentEntityType.SERVICE) foundEntity = serviceDao.findById(id);
                    else if (currentView == CurrentEntityType.TENANT) foundEntity = tenantDao.findById(id);
                    break;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка ввода", "ID должен быть числом для '" + currentView.getDisplayName() + "' (или строкой для некоторых договоров).");
            return;
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Ошибка поиска", "Произошла ошибка при поиске: " + e.getMessage());
            return;
        }

        tableData.clear();
        if (foundEntity != null) {
            tableData.add(foundEntity);
            mainTableView.getSelectionModel().selectFirst();
        } else {
            mainTableView.setPlaceholder(new Label("Запись с ID '" + idText + "' не найдена для '" + currentView.getDisplayName() + "'."));
            showAlert(Alert.AlertType.INFORMATION, "Поиск по ID", "Запись с ID '" + idText + "' не найдена.");
        }
        updateButtonStates();
    }

    private void reloadCurrentViewData() {
        if (currentView == CurrentEntityType.NONE) return;
        switch (currentView) {
            case ORGANIZATION:
                handleOrganizationsButton(null);
                break;
            case ADDRESS:
                handleAddressesButton(null);
                break;
            case APARTMENT:
                handleApartmentsButton(null);
                break;
            case SIGN_CONTRACT:
                handleSignContractsButton(null);
                break;
            case CONTRACT_TERMINATION:
                handleContractTerminationsButton(null);
                break;
            case LANDLORD:
                handleLandlordsButton(null);
                break;
            case PAYMENT:
                handlePaymentsButton(null);
                break;
            case SERVICE:
                handleServicesButton(null);
                break;
            case TENANT:
                handleTenantsButton(null);
                break;
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}