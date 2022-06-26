package plus.kat

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

import plus.kat.anno.Expose

class JsonKtTest {

    @Test
    fun test_toJson() {
        assertEquals(
            """{"id":1,"name":"kraity"}""",
            mapOf("id" to 1, "name" to "kraity").toJson()
        )
    }

    @Test
    fun test_read() {
        val user = parse<User>(
            """{"id":1,"name":"kraity"}"""
        )

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
