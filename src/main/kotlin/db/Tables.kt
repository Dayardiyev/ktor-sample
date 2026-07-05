package com.example.db

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

/**
 * Определения таблиц Exposed.
 *
 * В Exposed таблица описывается как `object`, наследующий IntIdTable —
 * это автоматически добавляет автоинкрементный PK-столбец `id` типа Int
 * (аналог @Id @GeneratedValue в JPA).
 *
 * Модель — «строгий EAV» (Entity-Attribute-Value):
 *   categories            — категории товаров
 *   category_attributes   — какие атрибуты есть у категории (напр. «Цвет», «Размер»)
 *   products              — товары, принадлежат категории
 *   product_attributes    — значение конкретного атрибута у конкретного товара
 */

object Categories : IntIdTable("categories") {
    val name = varchar("name", 100)
    val description = text("description").nullable()
}

object CategoryAttributes : IntIdTable("category_attributes") {
    // reference(...) создаёт столбец-внешний ключ (FK) на Categories.id.
    // onDelete = CASCADE — при удалении категории удалятся и её атрибуты.
    val categoryId = reference("category_id", Categories, onDelete = ReferenceOption.CASCADE)
    val name = varchar("name", 100)
    val dataType = varchar("data_type", 50) // напр. STRING, NUMBER, BOOLEAN
}

object Products : IntIdTable("products") {
    val categoryId = reference("category_id", Categories, onDelete = ReferenceOption.CASCADE)
    val name = varchar("name", 150)
    val description = text("description").nullable()
    val price = decimal("price", precision = 12, scale = 2)
}

object ProductAttributes : IntIdTable("product_attributes") {
    val productId = reference("product_id", Products, onDelete = ReferenceOption.CASCADE)
    val categoryAttributeId = reference("category_attribute_id", CategoryAttributes, onDelete = ReferenceOption.CASCADE)
    val value = varchar("value", 255)
}
