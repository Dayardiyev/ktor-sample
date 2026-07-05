package com.example.routing

import com.example.model.CategoryAttributeDto
import com.example.model.CategoryDto
import com.example.model.ProductAttributeDto
import com.example.model.ProductDto
import com.example.service.CategoryAttributeService
import com.example.service.CategoryService
import com.example.service.ProductAttributeService
import com.example.service.ProductService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*

/**
 * REST-маршруты каталога. Каждая функция-расширение регистрирует пять эндпоинтов
 * (список, чтение по id, создание, обновление, удаление) — классический CRUD.
 *
 * Соглашения по кодам ответа:
 *   GET    /list       -> 200 + массив
 *   GET    /{id}       -> 200 + объект | 404
 *   POST   /           -> 201 + созданный объект
 *   PUT    /{id}       -> 200 + объект | 404
 *   DELETE /{id}       -> 204 | 404
 */

/** Достаёт числовой id из пути или бросает исключение (его перехватит StatusPages -> 400). */
private fun RoutingContext.pathId(): Int =
    call.parameters["id"]?.toIntOrNull()
        ?: throw IllegalArgumentException("Параметр 'id' должен быть числом")

fun Route.categoryRoutes() = route("/categories") {
    get {
        call.respond(CategoryService.getAll())
    }
    get("/{id}") {
        val category = CategoryService.getById(pathId())
        if (category != null) call.respond(category) else call.respond(HttpStatusCode.NotFound)
    }
    post {
        val created = CategoryService.create(call.receive<CategoryDto>())
        call.respond(HttpStatusCode.Created, created)
    }
    put("/{id}") {
        val updated = CategoryService.update(pathId(), call.receive<CategoryDto>())
        if (updated) call.respond(CategoryService.getById(pathId())!!) else call.respond(HttpStatusCode.NotFound)
    }
    delete("/{id}") {
        val deleted = CategoryService.delete(pathId())
        call.respond(if (deleted) HttpStatusCode.NoContent else HttpStatusCode.NotFound)
    }
}

fun Route.categoryAttributeRoutes() = route("/category-attributes") {
    get {
        val categoryId = call.request.queryParameters["categoryId"]?.toIntOrNull()
        call.respond(CategoryAttributeService.getAll(categoryId))
    }
    get("/{id}") {
        val attr = CategoryAttributeService.getById(pathId())
        if (attr != null) call.respond(attr) else call.respond(HttpStatusCode.NotFound)
    }
    post {
        val created = CategoryAttributeService.create(call.receive<CategoryAttributeDto>())
        call.respond(HttpStatusCode.Created, created)
    }
    put("/{id}") {
        val updated = CategoryAttributeService.update(pathId(), call.receive<CategoryAttributeDto>())
        if (updated) call.respond(CategoryAttributeService.getById(pathId())!!) else call.respond(HttpStatusCode.NotFound)
    }
    delete("/{id}") {
        val deleted = CategoryAttributeService.delete(pathId())
        call.respond(if (deleted) HttpStatusCode.NoContent else HttpStatusCode.NotFound)
    }
}

fun Route.productRoutes() = route("/products") {
    get {
        val categoryId = call.request.queryParameters["categoryId"]?.toIntOrNull()
        call.respond(ProductService.getAll(categoryId))
    }
    get("/{id}") {
        val product = ProductService.getById(pathId())
        if (product != null) call.respond(product) else call.respond(HttpStatusCode.NotFound)
    }
    post {
        val created = ProductService.create(call.receive<ProductDto>())
        call.respond(HttpStatusCode.Created, created)
    }
    put("/{id}") {
        val updated = ProductService.update(pathId(), call.receive<ProductDto>())
        if (updated) call.respond(ProductService.getById(pathId())!!) else call.respond(HttpStatusCode.NotFound)
    }
    delete("/{id}") {
        val deleted = ProductService.delete(pathId())
        call.respond(if (deleted) HttpStatusCode.NoContent else HttpStatusCode.NotFound)
    }
}

fun Route.productAttributeRoutes() = route("/product-attributes") {
    get {
        val productId = call.request.queryParameters["productId"]?.toIntOrNull()
        call.respond(ProductAttributeService.getAll(productId))
    }
    get("/{id}") {
        val attr = ProductAttributeService.getById(pathId())
        if (attr != null) call.respond(attr) else call.respond(HttpStatusCode.NotFound)
    }
    post {
        val created = ProductAttributeService.create(call.receive<ProductAttributeDto>())
        call.respond(HttpStatusCode.Created, created)
    }
    put("/{id}") {
        val updated = ProductAttributeService.update(pathId(), call.receive<ProductAttributeDto>())
        if (updated) call.respond(ProductAttributeService.getById(pathId())!!) else call.respond(HttpStatusCode.NotFound)
    }
    delete("/{id}") {
        val deleted = ProductAttributeService.delete(pathId())
        call.respond(if (deleted) HttpStatusCode.NoContent else HttpStatusCode.NotFound)
    }
}
