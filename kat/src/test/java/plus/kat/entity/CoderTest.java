package plus.kat.entity;

import org.junit.jupiter.api.Test;

import plus.kat.*;
import plus.kat.anno.Embed;
import plus.kat.anno.Expose;
import plus.kat.anno.NotNull;
import plus.kat.chain.Space;
import plus.kat.chain.Value;
import plus.kat.Flow;
import plus.kat.spare.Coder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class CoderTest {
    @Test
    public void test() throws IOException {
        Spare<Note> spare =
            Spare.lookup(Note.class);

        Note note = spare.read(
            Event.ascii(
                "Note{i:id(101)s:title(kat+)s:status(PUBLISH)}"
            )
        );

        assertNotNull(note);
        assertEquals(
            "open", note.status
        );
        assertEquals(
            "Note{i:id(101)s:title(kat+)s:status(OPEN)}", spare.write(note).toString()
        );
        assertEquals(
            "<Note><id>101</id><title>kat+</title><status>OPEN</status></Note>", spare.mark(note).toString()
        );
        assertEquals(
            "{\"id\":101,\"title\":\"kat+\",\"status\":\"OPEN\"}", spare.serial(note).toString()
        );
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
        public Space getSpace() {
            return Space.$s;
        }

        @Override
        public String read(
            @NotNull Flag flag,
            @NotNull Value value
        ) {
            if (value.is("PUBLISH")) {
                return "open";
            }

            if (value.is("RECYCLE")) {
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
