package plus.kat

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

import plus.kat.anno.Expose

class JsonKtTest {

    @Test
    fun test_json() {
        assertEquals(
            """{"id":1,"name":"kraity"}""",
            json {
                it["id"] = 1
                it["name"] = "kraity"
            }
        )
    }

    @Test
    fun test_Json() {
        val json = Json {
            it["id"] = 1
            it["name"] = "kraity"
        }

        assertSame(Json::class.java, json::class.java)
        assertEquals(
            """{"id":1,"name":"kraity"}""", json.toString()
        )
    }

    @Test
    fun test_toJson() {
        assertEquals(
            """{"id":1,"name":"kraity"}""",
            mapOf("id" to 1, "name" to "kraity").toJson()
        )
    }

    @Test
    fun test_parse() {
        val text = """{"id":1,"name":"kraity"}"""
        val user = text.parse<User>()

        assertNotNull(user)
        assertEquals(1, user.id)
        assertEquals("kraity", user.name)
    }

    class User {
        @Expose("id")
        internal val id = 0

        @Expose("name")
        internal val name: String? = null
    }
}
