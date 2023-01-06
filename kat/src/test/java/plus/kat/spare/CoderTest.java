package plus.kat.spare;

import org.junit.jupiter.api.Test;

import plus.kat.anno.Embed;
import plus.kat.anno.Expose;
import plus.kat.anno.NotNull;

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
            new Event<>(
                "Note{i:id(101)s:title(kat+)s:status(PUBLISH)}"
            )
        );

        assertNotNull(note);
        assertEquals(
            "open", note.status
        );
        try (Chan chan = spare.write(note)) {
            assertEquals(
                "Note{i:id(101)s:title(kat+)s:status(OPEN)}", chan.toString()
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

    @Embed("Note")
    static class Note {
        @Expose("id")
        private int id;

        @Expose("title")
        private String title;

        @Expose(value = "status", with = StatusCoder.class)
        private String status;
    }

    static class StatusCoder implements Coder<String> {

        @Override
        public String getSpace() {
            return "s";
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
            @NotNull Flow flow,
            @NotNull Object value
        ) throws IOException {
            String val = value.toString();
            if (val.equals("open")) {
                flow.emit("OPEN");
            } else {
                flow.emit("NULL");
            }
        }
    }
}
