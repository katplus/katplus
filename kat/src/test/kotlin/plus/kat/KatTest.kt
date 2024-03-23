package plus.kat

import plus.kat.actor.*

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class KatKtTest {

    class User {
        @Magic("id")
        val id = 0

        @Magic("name")
        val name: String? = null

        @Magic("blocked", "disabled")
        val blocked = false
    }

    class Model {
        @Magic("mid")
        val mid = ""

        @Magic("tag")
        var tag: Map<*, *>? = null

        @Magic("extra")
        var extra: Map<String, Any>? = null
    }

    @Test
    fun test_kat() {
        assertEquals(
            """{id=1,name="kraity"}""",
            kat {
                it["id"] = 1
                it["name"] = "kraity"
            }.use {
                it.toString()
            }
        )
        assertEquals(
            """{id=1,name="kraity"}""",
            kat {
                it["id"] = 1
                it["name"] = "kraity"
            }.use {
                it.toString()
            }
        )
    }

    @Test
    fun test_Kat() {
        val kat = kat {
            it["id"] = 1
            it["name"] = "kraity"
        }

        kat.use {
            assertSame(Kat::class.java, kat::class.java)
            assertEquals(
                """{id=1,name="kraity"}""", kat.toString()
            )
        }
    }

    @Test
    fun test_to() {
        val user = spare<User>().read(
            Flow.of(
                "{id=1,name=kraity,disabled=1}"
            )
        )

        assertEquals(1, user.id)
        assertTrue(user.blocked)
        assertEquals("kraity", user.name)
    }

    @Test
    fun test_toKat() {
        assertEquals(
            """{id=1,name="kraity"}""",
            Kat.encode(mapOf("id" to 1, "name" to "kraity")).use { it.toString() }
        )
    }

    @Test
    fun test_read() {
        val user = spare<User>().read(
            Flow.of(
                "{id=1,name=kraity,disabled=1}"
            )
        )

        assertEquals(1, user.id)
        assertTrue(user.blocked)
        assertEquals("kraity", user.name)
    }

    @Test
    fun test_read1() {
        val spare = spare<User>()
        val user = spare.read(
            Flow.of(
                "{id=1,name=kraity,disabled=1}"
            )
        )

        assertEquals(1, user.id)
        assertTrue(user.blocked)
        assertEquals("kraity", user.name)
        assertEquals("""{id=1,name="kraity",blocked=true}""", spare.write(user).use { it.toString() })
    }

    @Test
    fun test_encode() {
        assertEquals(
            """[1,"kraity"]""",
            Kat.encode(arrayOf(1, "kraity")).use { it.toString() }
        )
        assertEquals(
            "[1,2,3]",
            Kat.encode(arrayOf(1, 2, 3)).use { it.toString() }
        )
        assertEquals(
            "[1,2,3,4.0,5.0,6.5]",
            Kat.encode(arrayOf(1, 2, 3L, 4F, 5.0, 6.5)).use { it.toString() }
        )
        assertEquals(
            """["1","2","3"]""",
            Kat.encode(arrayOf("1", "2", "3")).use { it.toString() }
        )
    }

    @Test
    fun test_decode() {
        val model = spare<Model>().read(
            Flow.of(
                "{mid=kat.plus,tag={c1:Int=12,c2:Boolean=1,c3:Double=1.2},extra={c1:Int=12,c2:Boolean=1,c3:Double=1.2}}"
            )
        )

        assertEquals("kat.plus", model.mid)
        assertEquals(
            "@Map{c1:Int=12,c2:Boolean=true,c3:Double=1.2}",
            Kat.encode(model.tag, Flag.NORM).use { it.toString() })
        assertEquals(
            "@Map{c1:Int=12,c2:Boolean=true,c3:Double=1.2}",
            Kat.encode(model.extra, Flag.NORM).use { it.toString() })
    }
}
