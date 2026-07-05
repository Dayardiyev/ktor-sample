package com.example.service

import com.example.db.Categories
import com.example.db.Products
import com.example.db.dbQuery
import com.example.model.ProductDto
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insertAndGetId
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.update

object ProductService {

    private fun toDto(row: ResultRow) = ProductDto(
        id = row[Products.id].value,
        categoryId = row[Products.categoryId].value,
        name = row[Products.name],
        description = row[Products.description],
        // В БД цена хранится как DECIMAL(12,2) (BigDecimal), в JSON отдаём как число.
        price = row[Products.price].toDouble(),
    )

    suspend fun getAll(categoryId: Int? = null): List<ProductDto> = dbQuery {
        var query = Products.selectAll()
        if (categoryId != null) {
            query = query.where { Products.categoryId eq categoryId }
        }
        query.map(::toDto).toList()
    }

    suspend fun getById(id: Int): ProductDto? = dbQuery {
        Products.selectAll().where { Products.id eq id }.map(::toDto).singleOrNull()
    }

    suspend fun create(dto: ProductDto): ProductDto = dbQuery {
        val newId = Products.insertAndGetId {
            it[categoryId] = EntityID(dto.categoryId, Categories)
            it[name] = dto.name
            it[description] = dto.description
            it[price] = dto.price.toBigDecimal()
        }.value
        dto.copy(id = newId)
    }

    suspend fun update(id: Int, dto: ProductDto): Boolean = dbQuery {
        Products.update({ Products.id eq id }) {
            it[categoryId] = EntityID(dto.categoryId, Categories)
            it[name] = dto.name
            it[description] = dto.description
            it[price] = dto.price.toBigDecimal()
        } > 0
    }

    suspend fun delete(id: Int): Boolean = dbQuery {
        Products.deleteWhere { Products.id eq id } > 0
    }
}
