package plus.kat

import plus.kat.*
import plus.kat.actor.*
import plus.kat.entity.*

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class SpareKtTest {

    @Magus
    class User(
        @Magic("id")
        var id: Int = 1,

        @Magic("name")
        var name: String = "kraity"
    )

    @Test
    fun test_marker() {
        val spare = spare<User>()

        val user0 = spare.read(
            Flow.of("{}")
        )
        assertNotNull(user0)
        assertEquals(1, user0.id)
        assertEquals("kraity", user0.name)
        assertEquals("""{name="kraity",id=1}""", spare.write(user0).use { it.toString() })

        val user2 = spare<User>().read(
            Flow.of("{id=2}")
        )
        assertNotNull(user2)
        assertEquals(2, user2.id)
        assertEquals("kraity", user2.name)
        assertEquals("""{name="kraity",id=2}""", spare.write(user2).use { it.toString() })
    }
}
