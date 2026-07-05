package com.example

import com.example.routing.categoryAttributeRoutes
import com.example.routing.categoryRoutes
import com.example.routing.productAttributeRoutes
import com.example.routing.productRoutes
import io.ktor.server.application.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        // Корень перенаправляет на Swagger UI для удобства.
        get("/") {
            call.respondRedirect("/swagger")
        }

        // Swagger UI, читает спецификацию из openapi/documentation.yaml (см. resources).
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")

        // CRUD-эндпоинты каталога.
        categoryRoutes()
        categoryAttributeRoutes()
        productRoutes()
        productAttributeRoutes()
    }
}
