# Bike Rent System (Microservices)

> **Обратите внимание:** Этот проект разработан в образовательных целях.
> Главная цель — **изучение и демонстрация современных технологий** (Microservices, Event-Driven Architecture, gRPC, GraphQL, Keycloak, Observability), а не создание коммерчески готового продукта.

Микросервисная система для автоматизации проката велосипедов. Проект демонстрирует полный цикл обработки аренды: от управления парком и бронирования до расчета динамической стоимости и сбора статистики в реальном времени.

**Методология разработки:**
Проект реализует гибридное взаимодействие сервисов: синхронное (REST, gRPC) для критических операций и асинхронное (RabbitMQ) для аналитики и уведомлений.

---

## Технологический стек

Проект построен на базе **Java 21** и **Spring Boot 3.5**.

### Архитектура и Коммуникация
*   **REST API & HATEOAS**: Основной вход для клиентов (уровень зрелости Richardson Maturity Model Level 3).
*   **GraphQL (Netflix DGS)**: Альтернативный API для гибкой выборки связанных данных (Клиент -> Аренда -> Велосипед).
*   **gRPC**: Высокопроизводительное взаимодействие между `Core` и служебными сервисами (`Pricing`, `Statistics`).
*   **RabbitMQ**: Асинхронная шина событий.
    *   Используются `TopicExchange` для событий системы и `FanoutExchange` для финансовых событий.
    *   Реализован механизм **Dead Letter Queue (DLQ)** для обработки ошибок.
*   **WebSocket**: Push-уведомления о статусах аренды в реальном времени.

### Данные и Безопасность
*   **PostgreSQL**: Реляционная база данных (используется подход Database per Service).
*   **Keycloak**: Identity Provider (OAuth2 / OIDC). Аутентификация и RBAC (Role Based Access Control).
*   **ModelMapper**: Преобразование DTO <-> Entity.

### Observability (Наблюдаемость)
*   **Zipkin**: Распределенная трассировка запросов (Distributed Tracing).
*   **Prometheus & Grafana**: Сбор метрик и визуализация состояния системы.
*   **Logback (Logstash encoder)**: Структурированное логирование в JSON с `traceId` и `spanId`.

### Инфраструктура и CI/CD
*   **Docker & Docker Compose**: Оркестрация среды.
*   **Jenkins**: Pipeline для автоматической сборки и деплоя.

---

## Структура сервисов

| Сервис | Порт (Ext/Int) | Описание |
| :--- | :--- | :--- |
| **`bikerent-core`** | `:8089` / `:8080` | **Monolith Core**. Управление велосипедами, клиентами, бронированием и активными арендами. REST и GraphQL точки входа. |
| **`pricing-service`** | `:9090` (gRPC) | Расчет стоимости аренды. Учитывает длительность, программу лояльности и штрафы/бонусы. |
| **`statistics-service`** | `:8083` / `:9091` | Агрегатор бизнес-метрик. Слушает события RabbitMQ и отдает данные по gRPC админам. |
| **`notification-service`**| `:8082` | WebSocket сервер. Транслирует события (регистрация, старт/конец аренды) в браузер. |
| **`jenkins`** | `:8088` | CI/CD сервер для сборки проекта. |

---

## Документация API

Проект предоставляет исчерпывающую документацию:

*   **Swagger UI (REST)**: Интерактивная документация.
    *   URL: `http://localhost:8089/swagger-ui/index.html`
*   **GraphQL (GraphiQL)**: Интерфейс для отладки GraphQL запросов.
    *   URL: `http://localhost:8089/graphiql`
    *   Endpoint: `/graphql`
*   **OpenAPI Docs**: `http://localhost:8089/v3/api-docs`

---

## Запуск проекта

Для запуска требуются **Docker Desktop** и **Java 21** (для локальной сборки).

### 1. Сборка артефактов
Поскольку проект использует общие библиотеки, важен порядок сборки:

```bash
# 1. Сборка общих контрактов и API
mvn clean install -f bikerent-contracts/pom.xml
mvn clean install -f bikerent-api/pom.xml

# 2. Сборка микросервисов
mvn clean package -DskipTests
```

### 2. Запуск инфраструктуры
```bash
docker compose up -d --build
```
*Первый запуск может занять время из-за инициализации Keycloak и Postgres.*

### 3. Настройка Keycloak (Auth)
По умолчанию в `docker-compose` запускается Keycloak на порту `:8180`.
1.  Вход: `admin` / `admin`.
2.  Импорт Realm: настройки должны подтянуться автоматически, но если нет — создайте Realm `bikerent-realm` и клиента `bikerent-api`.
3.  Создайте пользователя с ролью `ADMIN` для доступа к защищенным эндпоинтам (согласно `SecurityConfig`).

---

## Сценарий использования (Demo Flow)

### Шаг 1: Мониторинг событий
Откройте **Notification Service** в браузере, чтобы видеть события в реальном времени:
*   URL: [http://localhost:8082/index.html](http://localhost:8082/index.html)

### Шаг 2: Управление парком (Admin)
Через Swagger UI создайте велосипед:
*   `POST /api/bicycles`: `{ "modelName": "Scott Aspect", "type": "MOUNTAIN", "pricePerHour": 500 }`
*   *Событие:* В статистике (gRPC) отобразится, но RabbitMQ событие `bicycle.deleted` сработает только при удалении.

### Шаг 3: Клиентский путь
1.  **Регистрация**: `POST /api/customers`.
    *   *RabbitMQ:* Отправляется событие `customer.registered`.
    *   *Notification:* В веб-интерфейсе появится "НОВЫЙ КЛИЕНТ".
2.  **Бронирование**: `POST /api/bookings`.
    *   Система проверит конфликты времени в БД.
    *   *RabbitMQ:* `booking.created`.
3.  **Начало аренды**: `POST /api/rentals/from-booking/{id}`.
    *   Статус брони меняется на COMPLETED, создается активная аренда.
    *   *RabbitMQ:* `rental.started`.

### Шаг 4: Завершение и Расчет (gRPC)
Вызовите `POST /api/rentals/{id}/complete`.
1.  Core идет в **Pricing Service** по gRPC.
2.  Pricing Service считает скидки ( >5 часов = -15%, лояльность).
3.  Core обновляет баллы лояльности клиента.
4.  *RabbitMQ:* Отправляется Fanout событие `rental.ended` (финансовое).
5.  **Statistics Service** ловит событие и обновляет `totalRevenue`.

---

## Полезные ссылки

*   **Main App (Core):** [http://localhost:8089](http://localhost:8089)
*   **Notification Client:** [http://localhost:8082/index.html](http://localhost:8082/index.html)
*   **Jenkins:** [http://localhost:8088](http://localhost:8088)
*   **RabbitMQ Management:** [http://localhost:15672](http://localhost:15672) (guest / guest)
*   **Zipkin (Tracing):** [http://localhost:9411](http://localhost:9411)
*   **Grafana:** [http://localhost:3000](http://localhost:3000) (admin / admin)
*   **Prometheus:** [http://localhost:9090](http://localhost:9090)