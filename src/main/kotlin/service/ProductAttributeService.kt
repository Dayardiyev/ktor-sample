package com.example.service

import com.example.db.CategoryAttributes
import com.example.db.ProductAttributes
import com.example.db.Products
import com.example.db.dbQuery
import com.example.model.ProductAttributeDto
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insertAndGetId
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.update

object ProductAttributeService {

    private fun toDto(row: ResultRow) = ProductAttributeDto(
        id = row[ProductAttributes.id].value,
        productId = row[ProductAttributes.productId].value,
        categoryAttributeId = row[ProductAttributes.categoryAttributeId].value,
        value = row[ProductAttributes.value],
    )

    suspend fun getAll(productId: Int? = null): List<ProductAttributeDto> = dbQuery {
        var query = ProductAttributes.selectAll()
        // Обычно нужны атрибуты конкретного товара: GET /product-attributes?productId=1
        if (productId != null) {
            query = query.where { ProductAttributes.productId eq productId }
        }
        query.map(::toDto).toList()
    }

    suspend fun getById(id: Int): ProductAttributeDto? = dbQuery {
        ProductAttributes.selectAll().where { ProductAttributes.id eq id }.map(::toDto).singleOrNull()
    }

    suspend fun create(dto: ProductAttributeDto): ProductAttributeDto = dbQuery {
        val newId = ProductAttributes.insertAndGetId {
            it[productId] = EntityID(dto.productId, Products)
            it[categoryAttributeId] = EntityID(dto.categoryAttributeId, CategoryAttributes)
            it[value] = dto.value
        }.value
        dto.copy(id = newId)
    }

    suspend fun update(id: Int, dto: ProductAttributeDto): Boolean = dbQuery {
        ProductAttributes.update({ ProductAttributes.id eq id }) {
            it[productId] = EntityID(dto.productId, Products)
            it[categoryAttributeId] = EntityID(dto.categoryAttributeId, CategoryAttributes)
            it[value] = dto.value
        } > 0
    }

    suspend fun delete(id: Int): Boolean = dbQuery {
        ProductAttributes.deleteWhere { ProductAttributes.id eq id } > 0
    }
}
