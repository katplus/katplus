package plus.kat

import plus.kat.actor.Magic
import plus.kat.actor.Magus

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * @author kraity
 */
class PureKtTest {

    @Magus("plus.kat.PureUser")
    class User(
        @Magic("id")
        var id: Int = 1,

        @Magic("name")
        var name: String = "kraity"
    )

    @Test
    fun test_write() {
        val user = User()
        val entity = Entity {
            it["id"] = 1
            it["name"] = "kraity"
        }

        assertEquals("""{name="kraity",id=1}""", Kat(user).use { it.toString() })
        assertEquals("""{id=1,name="kraity"}""", kat(entity).use { it.toString() })
        assertEquals(
            """<plus.kat.PureUser><name>kraity</name><id>1</id></plus.kat.PureUser>""",
            Doc(user).use { it.toString() })
        assertEquals(
            """<Map><id>1</id><name>kraity</name></Map>""",
            doc(entity).use { it.toString() })
        assertEquals("""{"name":"kraity","id":1}""", Json(user).use { it.toString() })
        assertEquals("""{"id":1,"name":"kraity"}""", json(entity).use { it.toString() })
    }
}
