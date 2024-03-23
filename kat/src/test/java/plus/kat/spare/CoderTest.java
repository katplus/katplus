package plus.kat.spare;

import org.junit.jupiter.api.Test;

import plus.kat.actor.Magic;
import plus.kat.actor.Magus;
import plus.kat.actor.NotNull;

import plus.kat.*;
import plus.kat.chain.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class CoderTest {
    @Test
    public void test() throws IOException {
        Spare<Note> spare =
            Spare.of(Note.class);

        Note note = spare.read(
            Flow.of(
                "{id=101,title=kat+,status=PUBLISH}"
            )
        );

        assertNotNull(note);
        assertEquals("open", note.status);
        try (Chan chan = spare.write(note)) {
            assertEquals(
                "{id=101,title=\"kat+\",status=\"OPEN\"}", chan.toString()
            );
        }
        try (Chan chan = spare.mark(note)) {
            assertEquals(
                "<Note><id>101</id><title>kat+</title><status>OPEN</status></Note>", chan.toString()
            );
        }
        try (Chan chan = spare.serial(note)) {
            assertEquals(
                "{\"id\":101,\"title\":\"kat+\",\"status\":\"OPEN\"}", chan.toString()
            );
        }
    }

    @Magus("Note")
    static class Note {
        @Magic("id")
        private int id;

        @Magic("title")
        private String title;

        @Magic(value = "status", agent = StatusCoder.class)
        private String status;
    }

    static class StatusCoder implements Coder<String> {

        @Override
        public String getSpace() {
            return "String";
        }

        @Override
        public Border getBorder(
            @NotNull Flag flag
        ) {
            return Border.QUOTE;
        }

        @Override
        public String read(
            @NotNull Flag flag,
            @NotNull Value value
        ) {
            if (value.equals("PUBLISH")) {
                return "open";
            }

            if (value.equals("RECYCLE")) {
                return "spam";
            }

            return "null";
        }

        @Override
        public void write(
            @NotNull Flux flux,
            @NotNull Object value
        ) throws IOException {
            String val = value.toString();
            if (val.equals("open")) {
                flux.emit("OPEN");
            } else {
                flux.emit("NULL");
            }
        }
    }
}
