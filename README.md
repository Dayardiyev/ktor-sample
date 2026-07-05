# ktor-sample — REST API каталога товаров

Стек: **Ktor 3.5** + **Exposed 1.3 (R2DBC, реактивный доступ к БД)** + **PostgreSQL**, документация — **Swagger UI**.

## Что внутри

Модель данных — «строгий EAV» (Entity-Attribute-Value): категории описывают, какие
атрибуты у них есть, а товары хранят значения этих атрибутов.

| Таблица               | Назначение                                              | Внешние ключи                          |
|-----------------------|---------------------------------------------------------|----------------------------------------|
| `categories`          | Категории товаров                                       | —                                      |
| `category_attributes` | Какие атрибуты есть у категории (напр. «Цвет», «Размер») | `category_id → categories`             |
| `products`            | Товары                                                  | `category_id → categories`             |
| `product_attributes`  | Значение атрибута у конкретного товара                  | `product_id → products`, `category_attribute_id → category_attributes` |

Для каждой таблицы есть полный CRUD (список / чтение по id / создание / обновление / удаление).

## Требования

* JDK 21
* PostgreSQL (локально на `localhost:5432`)

Настройки подключения — в `src/main/resources/application.yaml` (блок `postgres`).
Таблицы создаются автоматически при старте (`SchemaUtils.create`), при первом запуске
добавляются демо-данные (одна категория с товаром и атрибутами).

## Запуск

```bash
./gradlew run       # запустить сервер на http://localhost:8080
./gradlew build     # сборка
```

## Swagger

После запуска открой **http://localhost:8080/swagger** — там можно протестировать все
эндпоинты прямо из браузера. Корень `/` перенаправляет на Swagger UI.
Спецификация OpenAPI: `src/main/resources/openapi/documentation.yaml`.

## Эндпоинты

```
GET    /categories                 POST /categories
GET    /categories/{id}            PUT  /categories/{id}     DELETE /categories/{id}

GET    /category-attributes[?categoryId=]   POST /category-attributes
GET    /category-attributes/{id}   PUT  /category-attributes/{id}   DELETE /category-attributes/{id}

GET    /products[?categoryId=]     POST /products
GET    /products/{id}              PUT  /products/{id}       DELETE /products/{id}

GET    /product-attributes[?productId=]     POST /product-attributes
GET    /product-attributes/{id}    PUT  /product-attributes/{id}    DELETE /product-attributes/{id}
```

Пример:

```bash
curl -X POST http://localhost:8080/categories \
  -H 'Content-Type: application/json' \
  -d '{"name":"Смартфоны","description":"Мобильные телефоны"}'
```

## Структура проекта

| Файл / пакет                          | Назначение                                            |
|---------------------------------------|-------------------------------------------------------|
| `main.kt`                             | точка входа с `main()` (`EngineMain`)                 |
| `application.yaml`                    | конфигурация приложения + список модулей              |
| `db/Tables.kt`                        | описание таблиц Exposed                               |
| `db/DatabaseFactory.kt`               | подключение к БД + инициализация схемы/seed           |
| `model/Dtos.kt`                       | DTO / request-response модели                         |
| `service/*Service.kt`                 | бизнес-логика и запросы к БД                          |
| `routing/CatalogRoutes.kt`            | маршруты и HTTP-коды                                  |
| `Serialization.kt` / `StatusPages.kt` | настройка JSON-сериализации + обработка ошибок        |

Все обращения к БД — `suspend`-функции (корутины), т.к. R2DBC неблокирующий.
Чтобы увидеть SQL, который генерирует Exposed, раскомментируй логгер `Exposed` в `logback.xml`.
