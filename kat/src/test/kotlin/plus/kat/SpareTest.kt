package plus.kat

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SpareKtTest {

    @Test
    fun test_toJson() {
        assertNotNull(
            lookup(User::class)
        )
    }

    class User {
        internal val id = 0
        internal val name: String? = null
    }
}
