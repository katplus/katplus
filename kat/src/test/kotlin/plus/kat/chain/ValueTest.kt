package plus.kat.chain

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * @author kraity
 */
class ValueKtTest {

    @Test
    fun test() {
        val value = Value("kat.plus")

        assertFalse(value.`is`('k'))
        assertTrue(value.`is`(3, '.'))
        assertFalse(value.`is`(3, '@'))
    }
}
