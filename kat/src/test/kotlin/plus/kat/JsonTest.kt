package plus.kat

import plus.kat.actor.*

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class JsonKtTest {

    class User {
        @Magic("id")
        val id = 0

        @Magic("name")
        val name: String? = null
    }

    @Test
    fun test_json() {
        assertEquals(
            """{"id":1,"name":"kraity"}""",
            json {
                it["id"] = 1
                it["name"] = "kraity"
            }.use {
                it.toString()
            }
        )
    }

    @Test
    fun test_Json() {
        val json = json {
            it["id"] = 1
            it["name"] = "kraity"
        }

        json.use {
            assertSame(Json::class.java, json::class.java)
            assertEquals(
                """{"id":1,"name":"kraity"}""", json.toString()
            )
        }
    }

    @Test
    fun test_toJson() {
        assertEquals(
            """{"id":1,"name":"kraity"}""",
            Json.encode(mapOf("id" to 1, "name" to "kraity")).use { it.toString() }
        )
    }

    @Test
    fun test_parse() {
        val user = spare<User>().parse(
            Flow.of(
                """{"id":1,"name":"kraity"}"""
            )
        )

        assertNotNull(user)
        assertEquals(1, user.id)
        assertEquals("kraity", user.name)
    }
}
