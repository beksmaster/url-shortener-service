# URL Shortener Service

Учебный REST API для создания коротких ссылок на Spring Boot.

Сервис генерирует короткий код для исходного URL, сохраняет ссылку в PostgreSQL
и перенаправляет пользователя по короткому адресу. При первом переходе ссылка
помещается в Redis с TTL, равным оставшемуся сроку её действия.

## Возможности

- создание коротких ссылок с заданным сроком действия;
- генерация уникального семисимвольного кода в Base62;
- перенаправление на исходный URL;
- подсчёт количества переходов;
- кэширование ссылок в Redis;
- управление схемой базы данных через Flyway;
- документация API в Swagger UI;
- интеграционные тесты с PostgreSQL в Testcontainers.

## Технологии

- Java 21;
- Spring Boot 4.0.7;
- Spring Web MVC, Spring Data JPA и Bean Validation;
- PostgreSQL 17;
- Redis 8;
- Flyway;
- Maven;
- JUnit 6 и Testcontainers;
- Docker и Docker Compose;
- Springdoc OpenAPI.

## Архитектура

Приложение построено по слоистой архитектуре:

```text
HTTP-запрос
    │
    ▼
Controller ──► Service ──► Repository ──► PostgreSQL
                  │
                  └──────► Redis

Spring Boot Actuator ──► Prometheus ──► Grafana
        │
        └── /actuator/prometheus
```

- `controller` принимает HTTP-запросы и формирует ответы;
- `service` содержит бизнес-логику, генерирует короткие коды и управляет кэшем;
- `repository` работает с PostgreSQL через Spring Data JPA;
- Redis ускоряет повторное разрешение коротких ссылок;
- Flyway создаёт и обновляет структуру базы данных.
- Spring Boot Actuator публикует технические и бизнес-метрики через `/actuator/prometheus`;
- Prometheus собирает метрики приложения;
- Grafana использует Prometheus как источник данных для дашбордов и визуализации метрик.

## Основные пакеты

```text
src/main/java/kg/megalab/urlshortenerservice
├── config       общие настройки и константы API
├── controller   REST-контроллеры
├── dto          модели запросов и ответов
├── entity       JPA-сущности
├── exception    исключения и глобальный обработчик ошибок
├── mapper       преобразование сущностей и DTO
├── repository   доступ к данным
└── service      бизнес-логика, генерация кодов и работа с Redis
```

## Требования

Для локальной разработки необходимы:

- JDK 21;
- Maven 3.9+ или Maven Wrapper;
- Docker с поддержкой Docker Compose.

Проверить окружение:

```bash
java -version
mvn -version
docker version
docker compose version
```

Java, используемая Maven, должна иметь версию 21.

## Настройка окружения

Создайте в корне проекта файл `.env`:

```env
DB_NAME=url_shortener
DB_USERNAME=postgres
DB_PASSWORD=postgres
```

Не добавляйте `.env` с реальными учётными данными в систему контроля версий.

## Быстрый запуск через Docker Compose

```bash
docker compose up --build
```

Docker Compose соберёт приложение и запустит PostgreSQL, Redis и сам сервис.

После запуска доступны:

- API: <http://localhost:8080>
- Swagger UI: <http://localhost:8080/swagger-ui/index.html>
- OpenAPI JSON: <http://localhost:8080/v3/api-docs>

Остановить сервисы:

```bash
docker compose down
```

Остановить сервисы и удалить данные PostgreSQL:

```bash
docker compose down -v
```

## Локальный запуск приложения

### 1. Запустить инфраструктуру

```bash
docker compose up -d postgres redis
```

PostgreSQL будет доступен на порту `5433`, Redis — на порту `6379`.

### 2. Запустить приложение

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

## Использование API

### Создание короткой ссылки

`POST /`

```bash
curl -X POST "http://localhost:8080/" \
  -H "Content-Type: application/json" \
  -d '{
    "originalUrl": "https://roadmap.sh/java",
    "expiresAt": "2026-08-06T15:05:35Z"
  }'
```

Поле `expiresAt` передаётся в формате ISO 8601.

Пример ответа (`201 Created`):

```json
{
  "id": 2,
  "originalUrl": "https://roadmap.sh/java",
  "shortCode": "J7O9l2Z",
  "shortUrl": "http://localhost:8080/J7O9l2Z",
  "createdAt": "2026-07-06T15:05:55.525055Z",
  "expiresAt": "2026-08-06T15:05:35Z",
  "clickCount": 0
}
```

### Переход по короткой ссылке

`GET /{shortCode}`

```bash
curl -i "http://localhost:8080/J7O9l2Z"
```

Если код найден и ссылка ещё действует, сервис возвращает:

```http
HTTP/1.1 302 Found
Location: https://roadmap.sh/java
```

Для неизвестного или просроченного кода возвращается `404 Not Found`.

## Эндпоинты

| Метод | Путь | Описание | Успешный ответ |
|---|---|---|---|
| `POST` | `/` | Создать короткую ссылку | `201 Created` |
| `GET` | `/{shortCode}` | Перейти по короткой ссылке | `302 Found` |

## Тестирование

Интеграционные тесты автоматически запускают PostgreSQL 17 и Redis 8
через Testcontainers, поэтому локально требуется только работающий Docker:

```bash
./mvnw test
```

В Windows:

```powershell
.\mvnw.cmd test
```

## Метрики

Prometheus-метрики доступны через Spring Boot Actuator:

```text
http://localhost:8080/actuator/prometheus
```

Сервис публикует следующие счётчики:

| Метрика в приложении | Имя в Prometheus | Описание |
|---|---|---|
| `url.creation` | `url_creation_total` | Созданные короткие ссылки |
| `url.redirect` | `url_redirect_total` | Перенаправления по коротким ссылкам |
| `url.cache.hit` | `url_cache_hit_total` | Попадания в Redis-кэш |
| `url.cache.miss` | `url_cache_miss_total` | Промахи Redis-кэша |

При экспорте точки в имени заменяются на `_`, а для `Counter` добавляется суффикс `_total`. Имя `url.created` использовать не следует: суффикс `_created` зарезервирован Prometheus, поэтому такая метрика экспортируется как `url_total`.

### Дашборд Grafana

Пример дашборда Grafana с метриками создания ссылок, редиректов, кэша и использования CPU:

![Дашборд Grafana](docs/images/grafana-dashboard.png)
