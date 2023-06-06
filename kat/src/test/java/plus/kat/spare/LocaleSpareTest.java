package plus.kat.spare;

import org.junit.jupiter.api.Test;

import plus.kat.Chan;
import plus.kat.Flag;
import plus.kat.Flow;
import plus.kat.Spare;

import java.util.Locale;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class LocaleSpareTest {

    @Test
    public void test_base() throws Throwable {
        Spare<Locale> spare
            = LocaleSpare.INSTANCE;

        assertEquals("zh", spare.read(Flow.of("zh")).toString());
        assertEquals("zh_CN", spare.read(Flow.of("zh_CN")).toString());
        assertEquals("abc", spare.read(Flow.of("abc")).toString());
        assertEquals("ab_CD", spare.read(Flow.of("ab_CD")).toString());
        assertEquals("ab_CD_EF", spare.read(Flow.of("ab_CD_EF")).toString());

        try (Chan chan = spare.write(Locale.CHINESE)) {
            assertEquals("\"zh\"", chan.toString());
        }

        try (Chan chan = spare.write(Locale.SIMPLIFIED_CHINESE)) {
            assertEquals("\"zh_CN\"", chan.toString());
        }

        try (Chan chan = spare.write(Locale.SIMPLIFIED_CHINESE, Flag.NORM)) {
            assertEquals("@Locale \"zh_CN\"", chan.toString());
        }
    }

    @Test
    public void test_preset() throws Throwable {
        Spare<Locale> spare
            = LocaleSpare.INSTANCE;

        for (Field field : Locale.class.getFields()) {
            if (field.getType() == Locale.class) {
                Locale locale = (Locale) field.get(null);
                String text = locale.toString();
                if (text.isEmpty()) {
                    continue;
                }

                assertSame(locale, spare.read(Flow.of(text)));
                assertSame(locale, spare.read(Flow.of(text.toLowerCase())));
                assertSame(locale, spare.read(Flow.of(text.toUpperCase())));
            }
        }
    }
}
