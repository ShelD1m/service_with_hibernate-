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
  <img src="https://github.com/user-attachments/assets/05a63995-b8ec-4a6d-ae97-0aee1fc2c9b0" width="800"/>
  <br/>
  <em>Главное окно приложения</em>
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/909b8454-b78f-404e-adb7-1bf368fe880a" width="800"/>
  <br/>
  <em>Просмотр таблицы с данными</em>
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/64406b67-d82d-41ce-9874-ba31da36b96d" width="800"/>
  <br/>
  <em>Поиск квартиры по ID</em>
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/79940a0c-5d3f-4a8a-8670-692553eafe64" width="800"/>
  <br/>
  <em>Добавление квартиры</em>
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/fbec1923-9487-462e-abcd-c6d78c6b45fd" width="800"/>
  <br/>
  <em>Проверка корректности введённых данных</em>
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/ca5a93fe-6ebb-4fcf-b4a4-938c83a99843" width="800"/>
  <br/>
  <em>Список доступных арендодателей при добавлении</em>
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/3bab68ec-8d77-402e-83e6-f00ebef87729" width="800"/>
  <br/>
  <em>Редактирование услуги</em>
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/e715fa8a-54e9-4e9d-9440-1ea31028b211" width="800"/>
  <br/>
  <em>Удаление записи из таблицы</em>
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/3201a46f-38d0-4957-9f0c-bc8328436548" width="800"/>
  <br/>
  <em>Окно подтверждения удаления</em>
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/defd1478-3b19-4e15-882a-e238b3270d5a" width="800"/>
  <br/>
  <em>Результат SQL-запроса в приложении</em>
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/2a703d81-c141-4fba-8767-14a3ca83af75" width="800"/>
  <br/>
  <em>ERD-модель базы данных</em>
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/f59ce052-0be3-46e0-93d8-c0121daa06ab" width="800"/>
  <br/>
  <em>Пример вывода результатов запроса</em>
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

