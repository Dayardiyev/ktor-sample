package com.example.service

import com.example.db.Categories
import com.example.db.dbQuery
import com.example.model.CategoryDto
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insertAndGetId
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.update

/**
 * CRUD-логика для категорий. В Ktor нет DI-контейнера как в Spring, поэтому
 * сервис — обычный `object` (синглтон). Все методы suspend, так как обращаются
 * к БД через реактивный R2DBC.
 */
object CategoryService {

    private fun toDto(row: ResultRow) = CategoryDto(
        id = row[Categories.id].value,
        name = row[Categories.name],
        description = row[Categories.description],
    )

    suspend fun getAll(): List<CategoryDto> = dbQuery {
        Categories.selectAll().map(::toDto).toList()
    }

    suspend fun getById(id: Int): CategoryDto? = dbQuery {
        Categories.selectAll().where { Categories.id eq id }.map(::toDto).singleOrNull()
    }

    suspend fun create(dto: CategoryDto): CategoryDto = dbQuery {
        val newId = Categories.insertAndGetId {
            it[name] = dto.name
            it[description] = dto.description
        }.value
        dto.copy(id = newId)
    }

    /** Возвращает true, если строка с таким id существовала и была обновлена. */
    suspend fun update(id: Int, dto: CategoryDto): Boolean = dbQuery {
        Categories.update({ Categories.id eq id }) {
            it[name] = dto.name
            it[description] = dto.description
        } > 0
    }

    /** Возвращает true, если строка с таким id была удалена. */
    suspend fun delete(id: Int): Boolean = dbQuery {
        Categories.deleteWhere { Categories.id eq id } > 0
    }
}
