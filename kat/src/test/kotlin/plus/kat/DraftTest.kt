package plus.kat

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * @author kraity
 */
class DraftKtTest {

    @Test
    fun test_base() {
        val map = Kat.decode(
            object : Draft<Map<Int, Long>>(
                "{16:Double=123,32:Boolean=456}"
            ) {}
        )

        assertNotNull(map)
        assertEquals(123L, map[16])
        assertEquals(456L, map[32])
    }
}
