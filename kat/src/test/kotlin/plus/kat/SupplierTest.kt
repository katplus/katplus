package plus.kat

import plus.kat.actor.*
import plus.kat.spare.*

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * @author kraity
 */
class SupplierKtTest {

    class User(
        val id: Int,
        val name: String
    )

    class Model(
        @Magic("id")
        val id: Int,

        @Magic("tag")
        val tag: Map<Int, Long>
    )

    @Test
    fun test_lookup() {
        val supplier = Supplier.ins()
        assertNotNull(
            supplier.assign<User>()
        )
    }

    @Test
    fun test() {
        val user = Supplier.ins()
            .assign<User>().read(
                Flow.of(
                    "{arg0=1,arg1=kraity}"
                )
            )

        assertEquals(1, user.id)
        assertEquals("kraity", user.name)
    }

    @Test
    fun test1() {
        val model = Supplier.ins()
            .assign<Model>().read(
                Flow.of(
                    "{id=1,tag={1=2,3=4}}"
                )
            )

        assertNotNull(model)
        assertEquals(1, model.id)
        assertNotNull(model.tag)
        model.tag.let {
            assertEquals(2L, it[1])
            assertEquals(4L, it[3])
        }
    }
}
