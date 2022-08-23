package plus.kat

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import plus.kat.anno.Embed
import plus.kat.anno.Expose

class SpareKtTest {

    @Test
    fun test_marker() {
        val supplier = Supplier.ins()
        val spare = supplier.lookup<User>()
        val u1 = spare.read(
            "User{}"
        )
        assertNotNull(u1)
        assertEquals(1, u1.id)
        assertEquals("kraity", u1.name)
        assertEquals("User{s:name(kraity)i:id(1)}", u1.toKat())

        val u2 = spare.read(
            "User{:id(2)}"
        )
        assertNotNull(u2)
        assertEquals(2, u2.id)
        assertEquals("kraity", u2.name)
        assertEquals("User{s:name(kraity)i:id(2)}", u2.toKat())
    }

    @Embed("User")
    class User(
        @Expose("id")
        val id: Int = 1,
        @Expose("name")
        val name: String = "kraity"
    )
}
