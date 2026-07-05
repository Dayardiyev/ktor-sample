package com.example.model

import kotlinx.serialization.Serializable

/**
 * DTO — объекты, которые сериализуются в/из JSON (аналог @RestController DTO в Spring).
 * @Serializable подключает генерацию сериализатора kotlinx.serialization на этапе компиляции.
 *
 * `id` — nullable и по умолчанию null: клиент не присылает id при создании (POST),
 * а на чтение (GET) сервер его заполняет.
 */

@Serializable
data class CategoryDto(
    val id: Int? = null,
    val name: String,
    val description: String? = null,
)

@Serializable
data class CategoryAttributeDto(
    val id: Int? = null,
    val categoryId: Int,
    val name: String,
    val dataType: String,
)

@Serializable
data class ProductDto(
    val id: Int? = null,
    val categoryId: Int,
    val name: String,
    val description: String? = null,
    val price: Double,
)

@Serializable
data class ProductAttributeDto(
    val id: Int? = null,
    val productId: Int,
    val categoryAttributeId: Int,
    val value: String,
)
