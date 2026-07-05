package com.example

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Тест поднимает приложение с реальным конфигом application.yaml (включая модули
 * и подключение к БД), поэтому для запуска нужен доступный PostgreSQL (ktor_catalog).
 *
 * `environment { config = ApplicationConfig("application.yaml") }` заставляет
 * testApplication загрузить те же модули, что и продакшн-запуск.
 */
class ServerTest {

    @Test
    fun `root redirects to swagger`() = testApplication {
        environment { config = ApplicationConfig("application.yaml") }
        // Отключаем автоследование за редиректом, чтобы проверить сам 302.
        val client = createClient { followRedirects = false }
        val response = client.get("/")
        assertEquals(HttpStatusCode.Found, response.status)
        assertEquals("/swagger", response.headers[HttpHeaders.Location])
    }

    @Test
    fun `categories endpoint returns seeded data`() = testApplication {
        environment { config = ApplicationConfig("application.yaml") }
        val response = client.get("/categories")
        assertEquals(HttpStatusCode.OK, response.status)
        // После seed в БД есть хотя бы одна категория.
        assertTrue(response.bodyAsText().contains("Ноутбуки"))
    }
}
