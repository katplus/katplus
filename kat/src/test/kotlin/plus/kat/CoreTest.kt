package plus.kat

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * @author kraity
 */
class CoreTestKt {

    @Test
    fun test_field72() {
        val model = spare<Model>().read(
            Flow.of(
                "{arg1=10,arg31=20,arg63=30,arg71=40}"
            )
        )
        assertNotNull(model)
        assertEquals(10, model.field1)
        assertEquals(100, model.field2)
        assertEquals(20, model.field31)
        assertEquals(100, model.field32)
        assertEquals(30, model.field63)
        assertEquals(100, model.field64)
        assertEquals(40, model.field71)
        assertEquals(100, model.field72)
    }

    data class Model(
        val field0: Int = 100,
        val field1: Int = 100,
        val field2: Int = 100,
        val field3: Int = 100,
        val field4: Int = 100,
        val field5: Int = 100,
        val field6: Int = 100,
        val field7: Int = 100,
        val field8: Int = 100,
        val field9: Int = 100,
        val field10: Int = 100,
        val field11: Int = 100,
        val field12: Int = 100,
        val field13: Int = 100,
        val field14: Int = 100,
        val field15: Int = 100,
        val field16: Int = 100,
        val field17: Int = 100,
        val field18: Int = 100,
        val field19: Int = 100,
        val field20: Int = 100,
        val field21: Int = 100,
        val field22: Int = 100,
        val field23: Int = 100,
        val field24: Int = 100,
        val field25: Int = 100,
        val field26: Int = 100,
        val field27: Int = 100,
        val field28: Int = 100,
        val field29: Int = 100,
        val field30: Int = 100,
        val field31: Int = 31,
        val field32: Int = 100,
        val field33: Int = 100,
        val field34: Int = 100,
        val field35: Int = 100,
        val field36: Int = 100,
        val field37: Int = 100,
        val field38: Int = 100,
        val field39: Int = 100,
        val field40: Int = 100,
        val field41: Int = 100,
        val field42: Int = 100,
        val field43: Int = 100,
        val field44: Int = 100,
        val field45: Int = 100,
        val field46: Int = 100,
        val field47: Int = 100,
        val field48: Int = 100,
        val field49: Int = 100,
        val field50: Int = 100,
        val field51: Int = 100,
        val field52: Int = 100,
        val field53: Int = 100,
        val field54: Int = 100,
        val field55: Int = 100,
        val field56: Int = 100,
        val field57: Int = 100,
        val field58: Int = 100,
        val field59: Int = 100,
        val field60: Int = 100,
        val field61: Int = 100,
        val field62: Int = 100,
        val field63: Int = 63,
        val field64: Int = 100,
        val field65: Int = 100,
        val field66: Int = 100,
        val field67: Int = 100,
        val field68: Int = 100,
        val field69: Int = 100,
        val field70: Int = 100,
        val field71: Int = 71,
        val field72: Int = 100
    )
}
