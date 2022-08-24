package plus.kat

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

import plus.kat.anno.Embed
import plus.kat.anno.Expose

class KatKtTest {

    @Test
    fun test_kat() {
        assertEquals(
            "User{i:id(1)s:name(kraity)}",
            kat("User") {
                it["id"] = 1
                it["name"] = "kraity"
            }
        )
    }

    @Test
    fun test_Kat() {
        val kat = Kat("User") {
            it["id"] = 1
            it["name"] = "kraity"
        }

        assertSame(Chan::class.java, kat::class.java)
        assertEquals(
            "User{i:id(1)s:name(kraity)}", kat.toString()
        )
    }

    @Test
    fun test_to() {
        val text = "User{i:id(1)s:name(kraity)b:disabled(1)}"
        val user = text.read<User>()

        assertEquals(1, user.id)
        assertTrue(user.blocked)
        assertEquals("kraity", user.name)
    }

    @Test
    fun test_toKat() {
        assertEquals(
            "M{i:id(1)s:name(kraity)}",
            mapOf("id" to 1, "name" to "kraity").toKat()
        )
    }

    @Test
    fun test_read() {
        val text = "User{i:id(1)s:name(kraity)b:disabled(1)}"
        val user = text.read<User>()

        assertEquals(1, user.id)
        assertTrue(user.blocked)
        assertEquals("kraity", user.name)
    }

    @Test
    fun test_read1() {
        val supplier = Supplier.ins()
        val user = supplier.read<User>(
            Event(
                "User{i:id(1)s:name(kraity)b:disabled(1)}"
            )
        )

        assertEquals(1, user.id)
        assertTrue(user.blocked)
        assertEquals("kraity", user.name)
        assertEquals("User{i:id(1)s:name(kraity)b:blocked(1)}", supplier.write(user).toString())
    }

    @Test
    fun test_read2() {
        val supplier = Supplier.ins()
        val user = supplier.cast<User>(
            mapOf(
                "id" to 1,
                "name" to "kraity"
            )
        )

        assertEquals(1, user.id)
        assertEquals("kraity", user.name)
        assertEquals("User{i:id(1)s:name(kraity)b:blocked(0)}", supplier.write(user).toString())
    }

    @Test
    fun test_encode() {
        assertEquals(
            "A{i(1)s(kraity)}",
            arrayOf(1, "kraity").toKat()
        )
        assertEquals(
            "A{i(1)i(2)i(3)}",
            arrayOf(1, 2, 3).toKat()
        )
        assertEquals(
            "A{i(1)i(2)l(3)f(4.0)d(5.0)d(6.5)}",
            arrayOf(1, 2, 3L, 4F, 5.0, 6.5).toKat()
        )
        assertEquals(
            "A{s(1)s(2)s(3)}",
            arrayOf("1", "2", "3").toKat()
        )
    }

    @Embed("User")
    class User {
        @Expose("id")
        val id = 0

        @Expose("name")
        val name: String? = null

        @Expose("blocked", "disabled")
        val blocked = false
    }
}
