package plus.kat.spare;

import org.junit.jupiter.api.Test;

import plus.kat.*;
import plus.kat.actor.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class ProxySpareTest {

    @Magus
    interface Mate {
        @Magic(index = 0)
        int getId();

        void setId(int id);

        String getTag();

        void setTag(String tag);
    }

    static class Model implements Mate {

        private int id;
        private String tag;

        @Override
        public int getId() {
            return id;
        }

        @Override
        public void setId(int id) {
            this.id = id;
        }

        @Override
        public String getTag() {
            return tag;
        }

        @Override
        public void setTag(String tag) {
            this.tag = tag;
        }
    }

    @Test
    public void test() throws Throwable {
        Model m = new Model();
        m.setId(1);
        m.setTag("kat.plus");

        Spare<Mate> spare =
            Spare.of(Mate.class);

        assertNotNull(spare);
        try (Chan chan = spare.write(m)) {
            assertEquals(
                "{id=1,tag=\"kat.plus\"}", chan.toString()
            );
        }

        Mate mate = spare.read(
            Flow.of(
                "{id=1,tag=\"kat\"}"
            )
        );

        assertNotNull(mate);
        assertNotSame(
            Mate.class, mate.getClass()
        );
        assertNotSame(
            Model.class, mate.getClass()
        );

        mate.setId(2);
        mate.setTag("kat.plus");

        try (Chan chan = spare.write(mate)) {
            assertEquals("{id=2,tag=\"kat.plus\"}", chan.toString());
        }
    }

    @Magus
    interface Meta {
        int id();

        Meta id(int id);

        String name();

        Meta name(String name);
    }

    @Test
    public void test2() throws IOException {
        Spare<Meta> spare =
            Spare.of(Meta.class);
        Meta meta = spare.read(
            Flow.of(
                "{id=1,name=kraity}"
            )
        );

        assertNotNull(meta);
        assertEquals(1, meta.id());
        assertEquals("kraity", meta.name());

        assertSame(
            meta, meta.id(2).name("kat.plus")
        );
        assertEquals(2, meta.id());
        assertEquals("kat.plus", meta.name());
    }

    @Magus
    interface User {
        int id();

        String name();
    }

    @Test
    public void test3() throws IOException {
        Supplier supplier = Supplier.ins();
        User user = supplier.read(
            User.class, Flow.of(
                "{id=1,name=kraity}"
            )
        );

        assertNotNull(user);
        assertEquals(1, user.id());
        assertEquals("kraity", user.name());
    }

    @Magus
    interface Device {
        @Magic(
            index = 0,
            value = "id"
        )
        int systemId();

        @Magic("id")
        Device systemId(int id);

        @Magic(
            index = 1,
            value = "name"
        )
        String systemName();

        @Magic("name")
        Device systemName(String tag);
    }

    @Test
    public void test4() throws IOException {
        Spare<Device> spare =
            Spare.of(Device.class);
        assertInstanceOf(
            ProxySpare.class, spare
        );

        Device device = spare.read(
            Flow.of(
                "{id=1,name=kraity}"
            )
        );

        assertNotNull(device);
        assertEquals(1, device.systemId());
        assertEquals("kraity", device.systemName());

        assertSame(
            device, device.systemId(2).systemName("kat.plus")
        );
        assertEquals(2, device.systemId());
        assertEquals("kat.plus", device.systemName());

        try (Chan chan = spare.write(device)) {
            assertEquals("{id=2,name=\"kat.plus\"}", chan.toString());
        }
    }
}
