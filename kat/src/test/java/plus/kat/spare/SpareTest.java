package plus.kat.spare;

import org.junit.jupiter.api.Test;

import plus.kat.*;
import plus.kat.anno.Embed;
import plus.kat.anno.Expose;
import plus.kat.anno.Format;
import plus.kat.reflex.ArrayType;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
            Spare.lookup(Role.class);

        Role role = spare.read(
            new Event<>(
                "${$:now(2022-01-11 11:11:11)$:last(1641910260)$:time(1641871353000)$:date(2022-02-22T22:22:22.222Z)$:just(03,三月 2022)$:local(2022-02-22)}"
            )
        );

        assertNotNull(role);
        assertEquals("Role{Date:now(2022-01-11 11:11:11)Date:last(2022-01-11 22:11:00)Date:time(2022-01-11 11:22:33)Date:date(2022-02-22T22:22:22.222Z)Date:just(03,三月 2022)LocalDate:local(2022-02-22)}", spare.write(role).toString());
    }

    public static class Art {

        private final int id;

        @Expose("meta")
        private final String tag;

        private final String name;

        public Art(int id, String tag, String name) {
            this.id = id;
            this.tag = tag;
            this.name = name;
        }

        public int id() {
            return id;
        }

        public String tag() {
            return "tag->" + tag;
        }

        @Expose("alias")
        public String name() {
            return "name->" + name;
        }
    }

    @Test
    public void test_record() {
        Spare<Art> spare = new RecordSpare<>(
            Art.class, Supplier.ins()
        );

        Art a1 = spare.read(
            "{:id(1):name(kraity):meta(katplus)}"
        );

        assertNotNull(a1);
        assertEquals(1, a1.id);
        assertEquals("kraity", a1.name);
        assertEquals("{\"id\":1,\"meta\":\"tag->katplus\",\"alias\":\"name->kraity\"}", spare.serial(a1).toString());

        Map<String, Object> map = new HashMap<>();
        map.put("id", 1);
        map.put("name", "kraity");
        map.put("kraity", "katplus");

        Art a2 = spare.cast(map);
        assertNotNull(a2);
        assertEquals(1, a2.id);
        assertEquals("kraity", a2.name);
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
        Spare<Meta> spare = Spare.lookup(Meta.class);

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
        assertEquals("UUID(092f7929-d2d6-44d6-9cc1-694c2e360c56)", Kat.encode(uuid));
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

    @Test
    public void test_Currency_read() {
        CurrencySpare spare = CurrencySpare.INSTANCE;

        Currency c0 = spare.read("$(CNY)");
        assertNotNull(c0);
        assertEquals("CNY", c0.getCurrencyCode());
        assertEquals("Currency(CNY)", Kat.encode(c0));

        Currency c1 = spare.read("$(USD)");
        assertNotNull(c1);
        assertEquals("USD", c1.getCurrencyCode());
        assertEquals("Currency(USD)", Kat.encode(c1));
    }

    @Test
    public void test_Locale_read() {
        LocaleSpare spare = LocaleSpare.INSTANCE;

        assertEquals("zh", spare.read("$(zh)").toString());
        assertEquals("zh_CN", spare.read("$(zh_CN)").toString());
        assertEquals("Locale(zh_CN)", Kat.encode(spare.read("$(zh_CN)")));
    }

    @Test
    public void test_BitSet_read() {
        BitSetSpare spare = BitSetSpare.INSTANCE;

        BitSet b0 = spare.read("${(1)(0)(1)(0)}");
        assertEquals("BitSet{i(1)i(0)i(1)}", Kat.encode(b0));

        BitSet b1 = spare.parse("[1,0,1,0]");
        assertEquals("[1,0,1]", Json.encode(b1));
    }

    @Test
    public void test_URL_read() {
        URLSpare spare = URLSpare.INSTANCE;

        URL u0 = spare.read("$(https://kat.plus/user.kat)");
        assertEquals("URL(https://kat.plus/user.kat)", Kat.encode(u0));
    }

    @Test
    public void test_URI_read() {
        URISpare spare = URISpare.INSTANCE;

        URI u0 = spare.read("$(https://kat.plus/user.kat)");
        assertEquals("URI(https://kat.plus/user.kat)", Kat.encode(u0));
    }

    @Test
    public void test_Instant_read() {
        InstantSpare spare = InstantSpare.INSTANCE;

        Instant i0 = spare.read("$(1645540424)");
        assertEquals("Instant(1645540424000)", Kat.encode(i0));

        Instant i1 = spare.read("$(1645540424000)");
        assertEquals("Instant(1645540424000)", Kat.encode(i1));
    }

    @Test
    public void test_ByteBuffer_read() {
        ByteBufferSpare spare = ByteBufferSpare.INSTANCE;

        ByteBuffer buf = spare.read("$(0123456789)");
        assertEquals("0123456789", new String(buf.array()));
        assertEquals("s(0123456789)", Kat.encode(buf));
    }

    @Test
    public void test_local_date() {
        LocalDateSpare spare = LocalDateSpare.INSTANCE;

        LocalDate localDate = spare.read("$(2022-02-22)");
        assertNotNull(localDate);
        assertEquals("LocalDate(2022-02-22)", Kat.encode(localDate));
    }

    @Test
    public void test_local_time() {
        LocalTimeSpare spare = LocalTimeSpare.INSTANCE;

        LocalTime localTime = spare.read("$(22:22:22.123)");
        assertNotNull(localTime);
        assertEquals("LocalTime(22:22:22.123)", Kat.encode(localTime));
    }

    @Test
    public void test_local_date_time() {
        LocalDateTimeSpare spare = LocalDateTimeSpare.INSTANCE;

        LocalDateTime localDateTime = spare.read("$(2022-02-22T22:22:22.123)");
        assertNotNull(localDateTime);
        assertEquals("LocalDateTime(2022-02-22T22:22:22.123)", Kat.encode(localDateTime));
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
