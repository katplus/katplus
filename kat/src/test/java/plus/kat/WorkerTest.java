package plus.kat;

import org.junit.jupiter.api.Test;
import plus.kat.anno.Expose;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class WorkerTest {

    @Test
    public void test() {
        Supplier supplier = Supplier.ins();

        Meta meta = supplier.read(
            Meta.class, new Event<>(
                "{:id(1):tag{:1(2):3(4)}}"
            )
        );

        assertNotNull(meta);
        assertEquals(1, meta.id);
        assertNotNull(meta.tag);
        assertEquals(2L, meta.tag.get(1));
        assertEquals(4L, meta.tag.get(3));
    }

    static class Meta {
        private int id;
        private Map<Integer, Long> tag;

        public Meta(
            @Expose("id") int id,
            @Expose("tag") Map<Integer, Long> tag
        ) {
            this.id = id;
            this.tag = tag;
        }
    }
}
