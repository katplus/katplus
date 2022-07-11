package plus.kat.spare;

import org.junit.jupiter.api.Test;

import plus.kat.Event;
import plus.kat.Spare;
import plus.kat.anno.Embed;
import plus.kat.anno.Expose;
import plus.kat.anno.Format;
import plus.kat.reflex.ArrayType;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

public class SpareTest {

    @Test
    public void test_object() {
        Spare<Object> spare = ObjectSpare.INSTANCE;

        assertEquals(123, spare.read("$(123)"));
        assertEquals(Integer.MAX_VALUE, spare.read("$(2147483647)"));
        assertEquals(Integer.MIN_VALUE, spare.read("$(-2147483648)"));

        assertNotEquals(123L, spare.read("$(123)"));
        assertEquals(2147483648L, spare.read("$(2147483648)"));
        assertEquals(-2147483649L, spare.read("$(-2147483649)"));
        assertEquals(Long.MAX_VALUE, spare.read("$(9223372036854775807)"));
        assertEquals(Long.MIN_VALUE, spare.read("$(-9223372036854775808)"));

        assertNotEquals(true, spare.read("$(1)"));
        assertEquals(true, spare.read("$(true)"));
        assertEquals(true, spare.read("$(TRUE)"));
        assertEquals("True", spare.read("$(True)"));
        assertEquals(false, spare.read("$(false)"));
        assertEquals(false, spare.read("$(FALSE)"));
        assertEquals("False", spare.read("$(False)"));
        assertEquals("null", spare.read("$(null)"));
        assertEquals("1A", spare.read("$(1A)"));
        assertEquals(255, spare.read("$(0xFF)"));
        assertEquals("0xGG", spare.read("$(0xGG)"));
        assertEquals("test", spare.read("$(test)"));
        assertEquals("kraity", spare.read("$(kraity)"));

        assertEquals("-", spare.read("$(-)"));
        assertEquals("-A", spare.read("$(-A)"));
        assertEquals(12, spare.read("$(12)"));
        assertEquals(12.35555, spare.read("$(12.35555)"));
        assertEquals(12.355555555555554, spare.read("$(12.355555555555554)"));
        assertEquals("123.456AA", spare.read("$(123.456AA)"));
        assertEquals("-123.456AA", spare.read("$(-123.456AA)"));
    }

    @Test
    public void test_date() {
        Spare<Role> spare =
            Spare.embed(Role.class);

        Role role = spare.read(
            new Event<>(
                "${$:now(2022-01-11 11:11:11)$:last(1641910260)$:time(1641871353000)$:date(2022-02-22T22:22:22.222Z)$:just(03,三月 2022)$:local(2022-02-22)}"
            )
        );

        assertNotNull(role);
        assertEquals("Role{Date:now(2022-01-11 11:11:11)Date:last(2022-01-11 22:11:00)Date:time(2022-01-11 11:22:33)Date:date(2022-02-22T22:22:22.222Z)Date:just(03,三月 2022)LocalDate:local(2022-02-22)}", spare.write(role).toString());
    }

    @Embed("Role")
    static class Role {
        @Expose("now")
        private Date now;

        @Expose("last")
        private Date last;

        @Expose("time")
        private Date time;

        @Expose("date")
        @Format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        private Date date;

        @Expose("just")
        @Format(value = "dd,MMMM yyyy", lang = "zh")
        private Date just;

        @Expose("local")
        @Format("yyyy-MM-dd")
        private LocalDate local;
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void test_list_read() {
        Spare<List> spare = ListSpare.INSTANCE;

        String[] s = {"1", "kraity"};
        List os = spare.cast(s);
        assertEquals("1", os.get(0));
        assertEquals("kraity", os.get(1));

        int[] i = {123, 456};
        List is = spare.cast(i);
        assertEquals(123, is.get(0));
        assertEquals(456, is.get(1));

        Long[] l = {123L, 456L};
        List ls = spare.cast(l);
        assertEquals(123L, ls.get(0));
        assertEquals(456L, ls.get(1));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void test_set_read() {
        Spare<Set> spare = SetSpare.INSTANCE;

        String[] s = {"1", "1", "kraity"};
        Set os = spare.cast(s);
        assertTrue(os.contains("1"));
        assertFalse(os.contains("id"));
        assertTrue(os.contains("kraity"));

        int[] i = {123, 456};
        Set is = spare.cast(i);
        assertTrue(is.contains(123));
        assertTrue(is.contains(456));

        Long[] l = {123L, 456L};
        Set ls = spare.cast(l);
        assertTrue(ls.contains(123L));
        assertTrue(ls.contains(456L));
    }

    @Test
    public void test_array_hook() throws Exception {
        for (Method method : Hook.class.getMethods()) {
            if (method.getName().equals("on")) {
                Spare<Object> spare = ArraySpare.INSTANCE;
                Event<Object[]> event = new Event<>(
                    "${${$(1)}${$:name(kraity)}}"
                );
                event.with(
                    ArrayType.of(
                        method.getGenericParameterTypes()
                    )
                );

                method.invoke(
                    new Hook(), spare.read(event)
                );
            }
        }
    }

    enum Meta {
        KAT, DOC, JSON
    }

    @Test
    public void test_enum_cast() {
        Spare<Meta> spare = Spare.embed(Meta.class);

        assertEquals(Meta.KAT, spare.cast(0));
        assertEquals(Meta.KAT, spare.cast("KAT"));
        assertEquals(Meta.KAT, spare.cast(Meta.KAT));
        assertEquals(Meta.JSON, spare.cast(2));
        assertEquals(Meta.JSON, spare.cast("JSON"));
        assertEquals(Meta.JSON, spare.cast(Meta.JSON));
    }


    @Test
    public void test_UUID_read() {
        UUIDSpare spare = UUIDSpare.INSTANCE;

        UUID uuid = spare.read(
            "$(092f7929-d2d6-44d6-9cc1-694c2e360c56)"
        );

        assertEquals("092f7929-d2d6-44d6-9cc1-694c2e360c56", uuid.toString());
    }

    @Test
    public void test_AtomicInteger_read() {
        AtomicIntegerSpare spare = AtomicIntegerSpare.INSTANCE;

        AtomicInteger atom = spare.read(
            "$(143)"
        );

        assertEquals(143, atom.get());
    }

    @Test
    public void test_AtomicLong_read() {
        AtomicLongSpare spare = AtomicLongSpare.INSTANCE;

        AtomicLong atom = spare.read(
            "$(14725836913579)"
        );

        assertEquals(14725836913579L, atom.get());
    }

    @Test
    public void test_AtomicBoolean_read() {
        AtomicBooleanSpare spare = AtomicBooleanSpare.INSTANCE;

        assertTrue(spare.read("$(1)").get());
        assertFalse(spare.read("$(0)").get());
        assertTrue(spare.read("$(true)").get());
        assertFalse(spare.read("$(false)").get());
        assertFalse(spare.read("$(katplus)").get());
    }

    static class Hook {
        public void on(
            ArrayList<Integer> data,
            HashMap<String, String> extra
        ) {
            assertEquals(1, data.size());
            assertEquals(1, extra.size());
            assertEquals(1, data.get(0));
            assertEquals("kraity", extra.get("name"));
        }
    }
}
