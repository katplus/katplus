package plus.kat;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class SugarTest {

    @Test
    public void test() throws IOException {
        User user = new User();
        user.id = 1;
        user.name = "kraity";

        Supplier supplier = Supplier.ins();
        Chan chan = Sugar.write(
            supplier, user, "Master"
        );

        assertEquals("plus.kat.SugarTest$User:Master{i:id(1)s:name(kraity)}", chan.toString());
    }

    static class User {
        public int id;
        public String name;
    }
}
