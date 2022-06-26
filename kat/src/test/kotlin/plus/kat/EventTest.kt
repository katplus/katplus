package plus.kat

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * @author kraity
 */
class EventKtTest {

    @Test
    fun test_char_reader_0() {
        val map = read<Map<*, *>>(
            object : Event<Map<Int, Long>>(
                "\${d:16(123)b:32(456)}"
            ) {}
        )

        map!!.let {
            assertEquals(123L, it[16])
            assertEquals(456L, it[32])
        }
    }
}
