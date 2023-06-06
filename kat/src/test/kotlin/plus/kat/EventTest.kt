package plus.kat

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * @author kraity
 */
class EventKtTest {

    @Test
    fun test_reader() {
        val map = Kat.decode(
            object : Event<Map<Int, Long>>(
                "{16:Double=123,32:Boolean=456}"
            ) {}
        )

        assertNotNull(map)
        assertEquals(123L, map[16])
        assertEquals(456L, map[32])
    }
}
