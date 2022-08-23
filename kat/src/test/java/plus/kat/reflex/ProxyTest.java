package plus.kat.reflex;

import org.junit.jupiter.api.Test;

import plus.kat.*;
import plus.kat.anno.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class ProxyTest {

    @Test
    public void test() throws Throwable {
        Model m = new Model();
        m.setId(1);
        m.setTag("kat.plus");

        Supplier supplier = Supplier.ins();
        Spare<Meta> spare =
            supplier.lookup(Meta.class);

        assertNotNull(spare);
        assertEquals(
            "Meta{i:id(1)s:tag(kat.plus)}", spare.write(m).toString()
        );

        Meta meta = supplier.read(
            Meta.class, new Event<>(
                "{:id(1):tag(kat)}"
            )
        );

        assertNotNull(meta);
        assertNotSame(
            Meta.class, meta.getClass()
        );
        assertNotSame(
            Model.class, meta.getClass()
        );
        assertEquals(
            "{\"id\":1,\"tag\":\"kat\"}", Json.encode(meta)
        );

        meta.setId(2);
        meta.setTag("kat.plus");
        assertEquals(
            "Meta{i:id(2)s:tag(kat.plus)}", Kat.encode(meta)
        );
    }

    @Embed("Meta")
    interface Meta {
        @Expose(index = 0)
        int getId();

        void setId(
            int id
        );

        String getTag();

        void setTag(
            String tag
        );
    }

    static class Model implements Meta {

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

    @Embed
    interface User {
        int id();

        String name();
    }

    @Test
    public void test2() {
        Supplier supplier = Supplier.ins();
        User user = supplier.read(
            User.class, new Event<>(
                "{:id(1):name(kraity)}"
            )
        );

        assertNotNull(user);
        assertEquals(1, user.id());
        assertEquals("kraity", user.name());
    }

    @Embed
    interface Bean {
        int id();

        Bean id(
            int id
        );

        String name();

        Bean name(
            String name
        );
    }

    @Test
    public void test3() {
        Supplier supplier = Supplier.ins();
        Bean bean = supplier.read(
            Bean.class, new Event<>(
                "{:id(1):name(kraity)}"
            )
        );

        assertNotNull(bean);
        assertEquals(1, bean.id());
        assertEquals("kraity", bean.name());

        assertSame(
            bean, bean.id(2).name("kat.plus")
        );
        assertEquals(2, bean.id());
        assertEquals("kat.plus", bean.name());
    }
}
