package com.example.db

import io.ktor.server.application.*
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.SchemaUtils
import org.jetbrains.exposed.v1.r2dbc.insertAndGetId
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import kotlinx.coroutines.flow.firstOrNull
import java.math.BigDecimal

/**
 * Единая точка подключения к PostgreSQL.
 *
 * Проект использует Exposed поверх R2DBC — это неблокирующий (реактивный) доступ
 * к БД, работающий на корутинах. Поэтому все запросы к БД выполняются внутри
 * `suspendTransaction { ... }` и являются suspend-функциями.
 */
object DatabaseFactory {

    // Инициализируется один раз при старте приложения в configureDatabases().
    lateinit var database: R2dbcDatabase
        private set

    suspend fun init(url: String, user: String, password: String) {
        database = R2dbcDatabase.connect(url = url, user = user, password = password)

        // Автоматически создаёт таблицы, если их ещё нет (аналог ddl-auto=update).
        suspendTransaction(database) {
            SchemaUtils.create(Categories, CategoryAttributes, Products, ProductAttributes)
        }

        seedIfEmpty()
    }

    /** Наполняет БД демо-данными при первом запуске (когда таблица categories пуста). */
    private suspend fun seedIfEmpty() = suspendTransaction(database) {
        val alreadySeeded = Categories.selectAll().firstOrNull() != null
        if (alreadySeeded) return@suspendTransaction

        val laptops: EntityID<Int> = Categories.insertAndGetId {
            it[name] = "Ноутбуки"
            it[description] = "Портативные компьютеры"
        }

        val ramAttr = CategoryAttributes.insertAndGetId {
            it[categoryId] = laptops
            it[name] = "Оперативная память, ГБ"
            it[dataType] = "NUMBER"
        }
        val colorAttr = CategoryAttributes.insertAndGetId {
            it[categoryId] = laptops
            it[name] = "Цвет"
            it[dataType] = "STRING"
        }

        val macbook = Products.insertAndGetId {
            it[categoryId] = laptops
            it[name] = "MacBook Air 13"
            it[description] = "Лёгкий ноутбук на Apple M-серии"
            it[price] = BigDecimal("1299.00")
        }

        ProductAttributes.insertAndGetId {
            it[productId] = macbook
            it[categoryAttributeId] = ramAttr
            it[value] = "16"
        }
        ProductAttributes.insertAndGetId {
            it[productId] = macbook
            it[categoryAttributeId] = colorAttr
            it[value] = "Серебристый"
        }
    }
}

/** Короткий помощник: выполнить блок запросов в транзакции на общем подключении. */
suspend fun <T> dbQuery(block: suspend () -> T): T =
    suspendTransaction(DatabaseFactory.database) { block() }

/** Ktor-модуль: читает настройки из application.yaml и поднимает подключение к БД. */
suspend fun Application.configureDatabases() {
    val config = environment.config
    DatabaseFactory.init(
        url = config.property("postgres.url").getString(),
        user = config.property("postgres.user").getString(),
        password = config.property("postgres.password").getString(),
    )
}
