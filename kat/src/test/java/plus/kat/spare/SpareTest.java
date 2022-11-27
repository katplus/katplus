package plus.kat.spare;

import org.junit.jupiter.api.Test;

import plus.kat.anno.Embed;
import plus.kat.anno.Expose;
import plus.kat.anno.Format;

import plus.kat.*;
import plus.kat.chain.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

public class SpareTest {

    static class User {
        public int id;
        public String name;
    }

    @Test
    public void test_supplier() {
        Supplier supplier = Supplier.ins();
        for (Class<?> c : new Class[]{
            int.class,
            long.class,
            float.class,
            double.class,
            byte.class,
            short.class,
            boolean.class}) {
            Spare<?> spare = supplier.lookup(c);
            assertNotNull(spare);

            // assert not null
            assertNotNull(spare.getSupplier());
        }

        Spare<?>[] spares = new Spare[]{
            ObjectSpare.INSTANCE,
            StringSpare.INSTANCE,
            IntegerSpare.INSTANCE,
            LongSpare.INSTANCE,
            FloatSpare.INSTANCE,
            DoubleSpare.INSTANCE,
            BooleanSpare.INSTANCE,
            ByteSpare.INSTANCE,
            ShortSpare.INSTANCE,
            CharSpare.INSTANCE,
            ByteArraySpare.INSTANCE,
            ArraySpare.INSTANCE,
            MapSpare.INSTANCE,
            SetSpare.INSTANCE,
            ListSpare.INSTANCE,
            BigIntegerSpare.INSTANCE,
            BigDecimalSpare.INSTANCE,
        };

        for (Spare<?> spare : spares) {
            // assert not null
            assertNotNull(spare.getSupplier());
        }
    }

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
        assertEquals(true, spare.read("$(True)"));
        assertEquals(false, spare.read("$(false)"));
        assertEquals(false, spare.read("$(FALSE)"));
        assertEquals(false, spare.read("$(False)"));
        assertEquals("TRue", spare.read("$(TRue)"));
        assertEquals("FALse", spare.read("$(FALse)"));

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

    @Embed("Time")
    static class Time {
        public Date now;
        public Date time;

        @Format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        public Date date;

        @Format(value = "dd,MMMM yyyy", lang = "zh")
        public Date just;
    }

    @Test
    public void test_date() throws IOException {
        Spare<Time> spare =
            Spare.lookup(Time.class);

        String text = "${$:now(2022-01-11 11:11:11)$:time(1641871353000)$:date(2022-02-22T22:22:22.222Z)$:just(03,三月 2022)}";

        Time time = spare.read(
            new Event<Time>(text).with(Flag.DIGIT_AS_DATE)
        );

        assertNotNull(time);
        try (Chan chan = spare.write(time)) {
            assertEquals("Time{Date:now(2022-01-11 11:11:11)Date:time(2022-01-11 11:22:33)Date:date(2022-02-22T22:22:22.222Z)Date:just(03,三月 2022)}", chan.toString());
        }
    }

    @Test
    public void test_date_cast() {
        DateSpare spare = DateSpare.INSTANCE;
        assertEquals(1641871353000L, spare.cast(1641871353).getTime());
        assertEquals(1641871353999L, spare.cast(1641871353999L).getTime());
    }

    @Embed(with = RecordSpare.class)
    static class Art {

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
    public void test_record() throws IOException {
        Spare<Art> spare =
            Spare.lookup(Art.class);

        Art a1 = spare.read(
            "{:id(1):name(kraity):meta(katplus)}"
        );

        assertNotNull(a1);
        assertEquals(1, a1.id);
        assertEquals("kraity", a1.name);
        try (Chan chan = spare.serial(a1)) {
            assertEquals("{\"id\":1,\"meta\":\"tag->katplus\",\"alias\":\"name->kraity\"}", chan.toString());
        }

        Map<String, Object> map = new HashMap<>();
        map.put("id", 1);
        map.put("name", "kraity");
        map.put("kraity", "katplus");

        Art a2 = spare.cast(map);
        assertNotNull(a2);
        assertEquals(1, a2.id);
        assertEquals("kraity", a2.name);
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
    public void test_array_cast() {
        Supplier supplier = Supplier.ins();
        Spare<User[]> spare = supplier.lookup(User[].class);

        Map<String, Object> u0 = new HashMap<>();
        u0.put("id", "0");
        u0.put("name", "kat");

        Map<String, Object> u1 = new HashMap<>();
        u1.put("id", "1");
        u1.put("name", "kraity");

        List<Object> list = new ArrayList<>();
        list.add(u0);
        list.add(u1);

        User[] users = spare.cast(list);
        assertNotNull(users);
        assertEquals(2, users.length);

        User user0 = users[0];
        assertNotNull(user0);
        assertEquals(0, user0.id);
        assertEquals("kat", user0.name);

        User user1 = users[1];
        assertNotNull(user1);
        assertEquals(1, user1.id);
        assertEquals("kraity", user1.name);
    }

    @Test
    public void test_array_apply() {
        Supplier supplier = Supplier.ins();
        Spare<User[]> spare = supplier.lookup(User[].class);

        Map<String, Map<String, Object>> map = new HashMap<>();
        Map<String, Object> u0 = new HashMap<>();
        map.put("u0", u0);
        u0.put("id", "0");
        u0.put("name", "kat");

        Map<String, Object> u1 = new HashMap<>();
        map.put("u1", u1);
        u1.put("id", "1");
        u1.put("name", "kraity");

        User[] users = spare.apply(
            Spoiler.of(map)
        );

        assertNotNull(users);
        assertEquals(2, users.length);

        User user0 = users[0];
        assertNotNull(user0);
        assertEquals(0, user0.id);
        assertEquals("kat", user0.name);

        User user1 = users[1];
        assertNotNull(user1);
        assertEquals(1, user1.id);
        assertEquals("kraity", user1.name);
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
        assertEquals(Meta.DOC, spare.cast(1));
        assertEquals(Meta.DOC, spare.cast("DOC"));
        assertEquals(Meta.DOC, spare.cast(Meta.DOC));
        assertEquals(Meta.JSON, spare.cast(2));
        assertEquals(Meta.JSON, spare.cast("JSON"));
        assertEquals(Meta.JSON, spare.cast(Meta.JSON));
    }

    @Test
    public void test_UUID_read() {
        UUIDSpare spare = UUIDSpare.INSTANCE;

        String uid = "UUID(" + spare.apply() + ")";
        assertEquals(uid, Kat.encode(spare.read(uid)));

        UUID uuid = spare.read(
            "$(092f7929-d2d6-44d6-9cc1-694c2e360c56)"
        );

        assertEquals("092f7929-d2d6-44d6-9cc1-694c2e360c56", uuid.toString());
        assertEquals("UUID(092f7929-d2d6-44d6-9cc1-694c2e360c56)", Kat.encode(uuid));

        assertNull(spare.cast(""));
        assertNull(spare.cast("092f7929-d2d6"));
        assertNull(spare.cast("092f7929-d2d6-44d6-9cc1"));
        assertEquals(uuid, spare.cast("092f7929-d2d6-44d6-9cc1-694c2e360c56"));
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
    public void test_Locale_read() throws Exception {
        LocaleSpare spare = LocaleSpare.INSTANCE;

        assertEquals("zh", spare.read("$(zh)").toString());
        assertEquals("zh_CN", spare.read("$(zh_CN)").toString());
        assertEquals("Locale(zh_CN)", Kat.encode(spare.read("$(zh_CN)")));

        assertNull(LocaleSpare.lookup(""));
        assertNull(LocaleSpare.lookup("A"));
        assertEquals("ab", LocaleSpare.lookup("AB").toString());
        assertEquals("abc", LocaleSpare.lookup("ABC").toString());
        assertEquals("abc_DE", LocaleSpare.lookup("ABC_DE").toString());
        assertEquals("abc_DE_FGH", LocaleSpare.lookup("ABC_DE_FGH").toString());

        for (Field field : Locale.class.getFields()) {
            if (field.getType() == Locale.class) {
                Locale locale = (Locale) field.get(null);
                String text = locale.toString();
                if (text.isEmpty()) {
                    continue;
                }

                assertSame(locale, LocaleSpare.lookup(text));
                assertSame(locale, LocaleSpare.lookup(text.toLowerCase()));
                assertSame(locale, LocaleSpare.lookup(text.toUpperCase()));
            }
        }
    }

    @Test
    public void test_File_read() {
        FileSpare spare = FileSpare.INSTANCE;

        File f0 = spare.read("$(file:\\kat.plus\\user.kat)");
        assertEquals("File(file:\\kat.plus\\user.kat)", Kat.encode(f0));
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
    public void test_ByteBuffer_read() {
        ByteBufferSpare spare = ByteBufferSpare.INSTANCE;
        assertNull(spare.cast(null));

        ByteBuffer buf = spare.read("$(a3JhaXR5)");
        assertEquals("kraity", new String(buf.array()));
        assertEquals("B(a3JhaXR5)", Kat.encode(buf));
        assertEquals("\"a3JhaXR5\"", Json.encode(buf));

        assertThrows(IllegalStateException.class, () -> spare.cast(1));
        assertThrows(IllegalStateException.class, () -> spare.cast(true));
    }

    @Test
    public void test_bytes() {
        ByteArraySpare spare = ByteArraySpare.INSTANCE;
        assertNull(spare.cast(null));
        byte[] bytes = "kraity".getBytes();
        assertSame(bytes, spare.cast(bytes));
        assertArrayEquals(bytes, spare.cast("a3JhaXR5"));
        assertThrows(IllegalStateException.class, () -> spare.cast(1));
        assertThrows(IllegalStateException.class, () -> spare.cast(true));
    }

    @Test
    public void test_chain() {
        ChainSpare spare = ChainSpare.INSTANCE;
        assertNull(spare.cast(null));

        Chain chain = spare.cast("");
        assertNotNull(chain);
        assertEquals(0, chain.length());

        chain.join("kat.plus");
        assertEquals("s(kat.plus)", Kat.encode(chain));

        assertThrows(IllegalStateException.class, () -> spare.cast(1));
        assertThrows(IllegalStateException.class, () -> spare.cast(true));
    }

    @Test
    public void test_string() {
        StringSpare spare = StringSpare.INSTANCE;
        assertNull(spare.cast(null));
        assertEquals("1", spare.cast(1));
        assertEquals("143", spare.cast(143L));
        assertEquals("kraity", spare.cast("kraity"));
        assertEquals("a3JhaXR5", spare.cast("kraity".getBytes()));
        assertEquals("kraity", spare.cast("kraity".toCharArray()));
    }

    @Test
    public void test_string_buffer() {
        StringBufferSpare spare = StringBufferSpare.INSTANCE;

        StringBuffer sb = spare.read("$(kat)");
        assertNotNull(sb);

        sb.append(".plus");
        assertEquals("s(kat.plus)", Kat.encode(sb));
    }

    @Test
    public void test_string_builder() {
        StringBuilderSpare spare = StringBuilderSpare.INSTANCE;

        StringBuilder sb = spare.read("$(kat)");
        assertNotNull(sb);

        sb.append(".plus");
        assertEquals("s(kat.plus)", Kat.encode(sb));
    }

    static class Hook {
        public void test(
            List<Integer> list,
            Map<String, String> data
        ) {
            assertEquals(1, data.size());
            assertEquals(2, list.size());
            assertEquals(1, list.get(0));
            assertEquals(2, list.get(1));
            assertEquals("kraity", data.get("name"));
        }
    }

    @Test
    public void test_array_hook() throws Exception {
        Type type = null;
        Method method = null;

        for (Method m : Hook.class.getDeclaredMethods()) {
            if (m.getName().equals("test")) {
                type = new ParameterizedType() {
                    @Override
                    public Type getOwnerType() {
                        return null;
                    }

                    @Override
                    public Type getRawType() {
                        return Object[].class;
                    }

                    @Override
                    public Type[] getActualTypeArguments() {
                        return m.getGenericParameterTypes();
                    }
                };
                method = m;
            }
        }

        assertNotNull(type);
        assertNotNull(method);

        Spare<Object[]> spare =
            Supplier.ins().lookup(
                Space.wipe(type), null
            );
        method.invoke(
            new Hook(), spare.read(
                new Event<Object[]>("{{(1)(2)}{:name(kraity)}}").with(type)
            )
        );
    }
}
