# Система управления арендой квартир

**Система управления арендой квартир** — это настольное Java-приложение для автоматизации процессов аренды и обслуживания жилья.  
Программа позволяет вести учёт квартир, арендаторов, арендодателей, договоров, услуг и оплат с использованием реляционной базы данных PostgreSQL и ORM-фреймворка Hibernate.

---

## Описание проекта

Приложение реализует полный цикл управления данными об аренде недвижимости: от регистрации квартиры и заключения договора до расторжения и учёта платежей.  
Все операции выполняются через интуитивно понятный интерфейс JavaFX, а взаимодействие с базой данных — через Hibernate.

Функционал охватывает:
- управление сущностями (квартиры, жильцы, арендодатели, услуги, организации и т.д.);
- выполнение CRUD-операций (добавление, редактирование, удаление);
- работу с договорами и оплатами;
- выполнение SQL- и HQL-запросов для анализа данных;
- валидацию пользовательского ввода.

---

## Основные возможности

- Просмотр всех данных в табличной форме;  
- Добавление и редактирование сущностей через диалоговые окна;  
- Поиск записей по ID и параметрам;  
- Фильтрация и дополнительные аналитические запросы;  
- Работа с договорами (подписание и расторжение);  
- Учёт платежей и услуг;  
- Связи между таблицами на уровне ORM (OneToMany, ManyToOne и т.д.).

---

## Технологии и стек

- **Java 17+**
- **JavaFX** — графический интерфейс
- **Hibernate ORM** — работа с базой данных
- **PostgreSQL** — СУБД
- **FXML** — описание окон интерфейса
- **Maven** — сборка проекта

---

## Архитектура проекта

```
src/main/java/com/dmitry/hibernate_1/
├── controller/       # Контроллеры JavaFX
├── dao/              # DAO-классы для доступа к данным
├── model/            # Модели (сущности JPA)
├── util/             # Конфигурация Hibernate
└── MainApp.java      # Точка входа приложения
```

---

## Структура базы данных

База данных `rental` содержит ключевые таблицы:

- **address** — адреса квартир  
- **apartment** — квартиры  
- **tenant** — квартиросъёмщики  
- **landlord** — арендодатели  
- **sign_contract** — подписанные договоры  
- **contract_termination** — расторжения договоров  
- **service** — услуги  
- **organization** — организации  
- **payment** — платежи

Связи реализованы через внешние ключи и ORM-аннотации Hibernate (`@OneToMany`, `@ManyToOne`, `@OneToOne`).

---

## Интерфейс приложения

<p align="center">
  <img src="https://github.com/user-attachments/assets/1f852534-d915-4352-9c3d-eeefb0023afa" width="800"/>
  <br/>
  <em>Главное окно приложения</em>
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/0e099435-a68b-4563-a497-9ae63b819329" width="800"/>
  <br/>
  <em>Просмотр таблицы с данными</em>
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/7e6ba715-ddce-41ee-8fca-fed0353a16b0" width="800"/>
  <br/>
  <em>Поиск квартиры по ID</em>
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/7a197fb2-f4ce-4f29-97b8-05759ec876f3" width="800"/>
  <br/>
  <em>Добавление квартиры</em>
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/67b98251-0753-4a9a-b906-0df5cd7eb878" width="800"/>
  <br/>
  <em>Ввод данных при добавлении квартиры</em>
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/53022e28-971b-47f5-bd0f-4fa257b87179" width="800"/>
  <br/>
  <em>Проверка добавленной записи в таблице</em>
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/917df57b-9aab-431e-b5d2-b26d14df0246" width="800"/>
  <br/>
  <em>Изменение данных услуги</em>
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/0efe7cfb-ea8c-4b59-9274-73c7a3ee0495" width="800"/>
  <br/>
  <em>Выбор записи для удаления</em>
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/fc352fc5-a668-467b-bcfe-d8b1b3428077" width="800"/>
  <br/>
  <em>ERD-модель базы данных</em>
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/4e4f6f56-de75-40e6-8edf-62180ea91116" width="800"/>
  <br/>
  <em>ER-модели в нотациях Чена и IDEF1X</em>
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/4bc1ca5e-ac0b-4d6f-a655-e4332d3eaa6d" width="800"/>
  <br/>
  <em>ER-модели в нотациях Чена и IDEF1X</em>
</p>

---

## Запуск проекта

1. Установите **PostgreSQL** и создайте базу данных:
   ```sql
   CREATE DATABASE Service;
   ```

2. Укажите параметры подключения в `hibernate.cfg.xml`:
   ```xml
   <property name="hibernate.connection.url">jdbc:postgresql://localhost:5438/Service</property>
   <property name="hibernate.connection.username">postgres</property>
   <property name="hibernate.connection.password">postgres</property>
   ```

3. Соберите и запустите проект:
   ```bash
   mvn clean package
   java -jar target/service_with_hibernate.jar
   ```

---

## Возможные улучшения

- Добавление авторизации пользователей  
- Экспорт данных в Excel или PDF  
- Поддержка многоарендности (multi-tenant)  
- REST API для интеграции с веб-приложениями  
- Тёмная тема интерфейса  

