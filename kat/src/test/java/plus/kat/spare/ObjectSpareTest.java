package plus.kat.spare;

import org.junit.jupiter.api.Test;

import plus.kat.Flow;
import plus.kat.Spare;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class ObjectSpareTest {

    @Test
    public void test_read() throws IOException {
        Spare<Object> spare = ObjectSpare.INSTANCE;

        assertEquals(123, spare.read(Flow.of("123")));
        assertEquals(Integer.MAX_VALUE, spare.read(Flow.of("2147483647")));
        assertEquals(Integer.MIN_VALUE, spare.read(Flow.of("-2147483648")));

        assertNotEquals(123L, spare.read(Flow.of("123")));
        assertEquals(2147483648L, spare.read(Flow.of("2147483648")));
        assertEquals(-2147483649L, spare.read(Flow.of("-2147483649")));
        assertEquals(Long.MAX_VALUE, spare.read(Flow.of("9223372036854775807")));
        assertEquals(Long.MIN_VALUE, spare.read(Flow.of("-9223372036854775808")));

        assertNotEquals(true, spare.read(Flow.of("1")));
        assertEquals(true, spare.read(Flow.of("true")));
        assertEquals(true, spare.read(Flow.of("TRUE")));
        assertEquals(true, spare.read(Flow.of("True")));
        assertEquals(false, spare.read(Flow.of("false")));
        assertEquals(false, spare.read(Flow.of("FALSE")));
        assertEquals(false, spare.read(Flow.of("False")));
        assertEquals("TRue", spare.read(Flow.of("TRue")));
        assertEquals("FALse", spare.read(Flow.of("FALse")));

        assertEquals(".", spare.read(Flow.of(".")));
        assertEquals(.0D, spare.read(Flow.of(".0")));
        assertEquals(0.D, spare.read(Flow.of("0.D")));
        assertEquals(.123D, spare.read(Flow.of(".123")));
        assertEquals("1A", spare.read(Flow.of("1A")));
        assertEquals(255, spare.read(Flow.of("0xFF")));
        assertEquals("0xGG", spare.read(Flow.of("0xGG")));
        assertEquals("test", spare.read(Flow.of("test")));
        assertEquals("kraity", spare.read(Flow.of("kraity")));

        assertNull(spare.read(Flow.of("null")));
        assertNull(spare.read(Flow.of("\"null\"")));
        assertEquals("-", spare.read(Flow.of("-")));
        assertEquals("-A", spare.read(Flow.of("-A")));
        assertEquals(12, spare.read(Flow.of("12")));
        assertEquals("123.456AA", spare.read(Flow.of("123.456AA")));
        assertEquals("-123.456AA", spare.read(Flow.of("-123.456AA")));
        assertEquals(12.35555, spare.read(Flow.of("12.35555")));
        assertEquals(12.355555555555554, spare.read(Flow.of("12.355555555555554")));
    }
}
