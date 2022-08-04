package plus.kat;

import org.junit.jupiter.api.Test;
import plus.kat.chain.Value;
import plus.kat.crash.IOCrash;
import plus.kat.crash.UnexpectedCrash;
import plus.kat.entity.Builder;
import plus.kat.entity.Coder;
import plus.kat.spare.StringSpare;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class FlagTest {

    static class Meta {
        public Date date;
        public Instant instant;
    }

    @Test
    public void test_date_as_timestamp() {
        Meta meta = new Meta();
        meta.date = new Date(1641871353000L);
        meta.instant = Instant.ofEpochMilli(1641871353123L);

        assertEquals(
            "{\"date\":\"2022-01-11 11:22:33\",\"instant\":\"2022-01-11T03:22:33.123Z\"}", new Json(meta).toString()
        );

        assertEquals(
            "{\"date\":1641871353000,\"instant\":\"2022-01-11T03:22:33.123Z\"}",
            Json.encode(meta, Flag.DATE_AS_TIMESTAMP)
        );

        assertEquals(
            "{\"date\":\"2022-01-11 11:22:33\",\"instant\":1641871353123}",
            Json.encode(meta, Flag.INSTANT_AS_TIMESTAMP)
        );
    }

    enum Role {
        A, B, C
    }

    static class Bean0 {
        public Role role;
    }

    @Test
    public void test_enum_as_index() {
        Bean0 bean = new Bean0();

        bean.role = Role.A;
        assertEquals("{\"role\":\"A\"}", Json.encode(bean));
        assertEquals("{\"role\":0}", Json.encode(bean, Flag.ENUM_AS_INDEX));

        bean.role = Role.B;
        assertEquals("{\"role\":\"B\"}", Json.encode(bean));
        assertEquals("{\"role\":1}", Json.encode(bean, Flag.ENUM_AS_INDEX));


        bean.role = Role.C;
        assertEquals("{\"role\":\"C\"}", Json.encode(bean));
        assertEquals("{\"role\":2}", Json.encode(bean, Flag.ENUM_AS_INDEX));
    }

    @Test
    public void test_getType() {
        Event<?> event = new Event<>();

        Flag flag = event.getFlag();
        assertNull(flag.getType());

        Type[] types = new Type[]{
            int.class,
            Long.class,
            Map.class,
            Iterable.class,
            BigInteger.class
        };

        for (Type type : types) {
            event.with(type);
            assertEquals(type, flag.getType());
        }
    }

    @Test
    public void test_getType2() {
        Type[] types = new Type[]{
            String.class,
            Value.class,
            StringBuilder.class
        };

        Spare<CharSequence> spare = new Spare<CharSequence>() {
            @Override
            public CharSequence getSpace() {
                return null;
            }

            @Override
            public Boolean getFlag() {
                return null;
            }

            @Override
            public boolean accept(Class<?> klass) {
                return false;
            }

            @Override
            public CharSequence read(
                Flag flag,
                Value value
            ) throws IOCrash {
                Type type = flag.getType();

                if (type == String.class) {
                    return value.toString();
                }

                if (type == Value.class) {
                    return new Value(value);
                }

                if (type == StringBuilder.class) {
                    return new StringBuilder(
                        value.toString()
                    );
                }

                throw new UnexpectedCrash();
            }

            @Override
            public Class<CharSequence> getType() {
                return null;
            }

            @Override
            public Builder<CharSequence> getBuilder(Type type) {
                return null;
            }
        };

        for (Type type : types) {
            Event<CharSequence> event =
                new Event<>("$(test)");
            event.with(type).with(spare);

            assertEquals(
                type, Kat.decode(event).getClass()
            );
        }
    }
}
