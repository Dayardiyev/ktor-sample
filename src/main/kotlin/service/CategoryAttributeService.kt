package com.example.service

import com.example.db.Categories
import com.example.db.CategoryAttributes
import com.example.db.dbQuery
import com.example.model.CategoryAttributeDto
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insertAndGetId
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.update

object CategoryAttributeService {

    private fun toDto(row: ResultRow) = CategoryAttributeDto(
        id = row[CategoryAttributes.id].value,
        categoryId = row[CategoryAttributes.categoryId].value,
        name = row[CategoryAttributes.name],
        dataType = row[CategoryAttributes.dataType],
    )

    suspend fun getAll(categoryId: Int? = null): List<CategoryAttributeDto> = dbQuery {
        var query = CategoryAttributes.selectAll()
        // Необязательный фильтр по категории: GET /category-attributes?categoryId=1
        if (categoryId != null) {
            query = query.where { CategoryAttributes.categoryId eq categoryId }
        }
        query.map(::toDto).toList()
    }

    suspend fun getById(id: Int): CategoryAttributeDto? = dbQuery {
        CategoryAttributes.selectAll().where { CategoryAttributes.id eq id }.map(::toDto).singleOrNull()
    }

    suspend fun create(dto: CategoryAttributeDto): CategoryAttributeDto = dbQuery {
        val newId = CategoryAttributes.insertAndGetId {
            it[categoryId] = EntityID(dto.categoryId, Categories)
            it[name] = dto.name
            it[dataType] = dto.dataType
        }.value
        dto.copy(id = newId)
    }

    suspend fun update(id: Int, dto: CategoryAttributeDto): Boolean = dbQuery {
        CategoryAttributes.update({ CategoryAttributes.id eq id }) {
            it[categoryId] = EntityID(dto.categoryId, Categories)
            it[name] = dto.name
            it[dataType] = dto.dataType
        } > 0
    }

    suspend fun delete(id: Int): Boolean = dbQuery {
        CategoryAttributes.deleteWhere { CategoryAttributes.id eq id } > 0
    }
}
