package plus.kat

import plus.kat.anno.Embed
import plus.kat.anno.Expose

import plus.kat.*

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class SpareKtTest {

    @Test
    fun test_flat() {
        val data = HashMap<String, Any?>()
        assertTrue(
            spare<User>().flat(
                User()
            ) { key, value ->
                data[key] = value
            }
        )
        assertEquals("{name=kraity, id=1}", data.toString())
    }

    @Test
    fun test_marker() {
        val user0 = "User{}".read<User>()
        assertNotNull(user0)
        assertEquals(1, user0.id)
        assertEquals("kraity", user0.name)
        assertEquals("User{s:name(kraity)i:id(1)}", user0.toKat())

        val user2 = "User{:id(2)}".read<User>()
        assertNotNull(user2)
        assertEquals(2, user2.id)
        assertEquals("kraity", user2.name)
        assertEquals("User{s:name(kraity)i:id(2)}", user2.toKat())
    }

    @Embed("User")
    class User(
        @Expose("id")
        var id: Int = 1,
        @Expose("name")
        var name: String = "kraity"
    )
}
