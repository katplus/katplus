package plus.kat.spare;

import org.junit.jupiter.api.Test;

import plus.kat.actor.Magic;
import plus.kat.actor.Magus;

import plus.kat.Chan;
import plus.kat.Flow;
import plus.kat.Spare;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class RecordSpareTest {

    @Magus(agent = RecordSpare.class)
    static class Art {

        private final int id;

        @Magic("meta")
        private final String tag;

        private final String name;

        public Art(
            int id,
            String tag,
            String name
        ) {
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

        @Magic("alias")
        public String name() {
            return "name->" + name;
        }
    }

    @Test
    public void test_base() throws IOException {
        Spare<Art> spare =
            Spare.of(Art.class);
        assertInstanceOf(
            RecordSpare.class, spare
        );

        Art art = spare.read(
            Flow.of("{id=1,name=kraity,meta=katplus}")
        );

        assertNotNull(art);
        assertEquals(1, art.id);
        assertEquals("kraity", art.name);
        try (Chan chan = spare.write(art)) {
            assertEquals("{id=1,meta=\"tag->katplus\",alias=\"name->kraity\"}", chan.toString());
        }
    }
}
