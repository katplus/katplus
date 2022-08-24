package plus.kat

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import plus.kat.anno.Expose

/**
 * @author kraity
 */
class SupplierKtTest {

    @Test
    fun test_lookup() {
        val supplier = Supplier.ins()
        assertNotNull(
            supplier.lookup<User>()
        )
    }

    @Test
    fun test() {
        val supplier = Supplier.ins()
        val user = supplier.read<User>(
            Event("{$:arg0(1)$:arg1(kraity)}")
        )

        assertEquals(1, user.id)
        assertEquals("kraity", user.name)
    }

    @Test
    fun test1() {
        val supplier = Supplier.ins()
        val meta = supplier.read<Meta>(
            Event("{$:id(1)$:tag{$:1(2)$:3(4)}}")
        )

        assertEquals(1, meta.id)
        assertNotNull(meta.tag)
        meta.tag.let {
            assertEquals(2L, it[1])
            assertEquals(4L, it[3])
        }
    }

    class User(
        val id: Int,
        val name: String
    )

    class Meta(
        @Expose("id")
        val id: Int,

        @Expose("tag")
        val tag: Map<Int, Long>
    )
}
