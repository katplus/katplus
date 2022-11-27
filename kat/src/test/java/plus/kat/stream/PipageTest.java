package plus.kat.stream;

import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.solver.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class PipageTest {

    @Test
    public void test() throws IOException {
        Work note = new Work();
        try (Radar radar = Radar.apply();
             Reader reader = new CharLatinReader(
                 ":id(2022):job(kat.plus)" +
                     ":master{:id(1):name(kat)}" +
                     ":partner{:id(2):name(plus)}"
             )) {
            radar.read(
                reader, note
            );
        }

        assertEquals(2022, note.id);
        assertEquals("kat.plus", note.job);

        assertEquals(1, note.master.id);
        assertEquals("kat", note.master.name);

        assertEquals(2, note.partner.id);
        assertEquals("plus", note.partner.name);
    }

    static class Work implements Pipage {

        private int id;
        private String job;

        final User master = new User();
        final User partner = new User();

        @Override
        public Pipage onOpen(
            Space space,
            Alias alias
        ) {
            if (alias.equals("master")) {
                return master;
            }
            if (alias.equals("partner")) {
                return partner;
            }
            throw new Collapse(
                "Illegal property name"
            );
        }

        @Override
        public void onEmit(
            Space space,
            Alias alias,
            Value value
        ) {
            if (alias.equals("id")) {
                id = value.toInt();
            } else if (alias.equals("job")) {
                job = value.toString();
            } else {
                throw new Collapse(
                    "Illegal property name"
                );
            }
        }

        @Override
        public Pipage onClose(
            boolean state,
            boolean alarm
        ) {
            return null; // No parent pipage
        }

        class User implements Pipage {

            private int id;
            private String name;

            @Override
            public Pipage onOpen(
                Space space,
                Alias alias
            ) {
                return null;
            }

            @Override
            public void onEmit(
                Space space,
                Alias alias,
                Value value
            ) {
                if (alias.equals("id")) {
                    id = value.toInt();
                } else if (alias.equals("name")) {
                    name = value.toString();
                } else {
                    throw new Collapse(
                        "Illegal property name"
                    );
                }
            }

            @Override
            public Pipage onClose(
                boolean state,
                boolean alarm
            ) {
                return Work.this;
            }
        }
    }
}
