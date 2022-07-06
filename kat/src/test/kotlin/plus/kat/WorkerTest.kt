package plus.kat

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * @author kraity
 */
class WorkerKtTest {

    @Test
    fun test() {
        val supplier = Supplier.ins()
        val user = supplier.read<User>(
            Event(
                "{$:id(1)$:name(kraity)}"
            )
        )

        assertEquals(1, user.id)
        assertEquals("kraity", user.name)
    }

    class User(
        val id: Int,
        val name: String
    )
}
