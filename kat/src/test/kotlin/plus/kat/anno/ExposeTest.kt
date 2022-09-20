package plus.kat.anno

import plus.kat.*

import plus.kat.It.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * @author kraity
 */
class ExposeKtTest {

    @Test
    fun test() {
        val supplier = Supplier.ins()
        val meta = supplier.read<Meta>(
            Event("{:id():tag()}")
        )

        assertNotNull(meta)
        assertNull(meta.id)
        assertEquals(2, meta.tag)
    }

    class Meta {
        var id: Number? = 1

        @set:Expose(
            require = NotNull
        )
        var tag: Number? = 2
    }
}
