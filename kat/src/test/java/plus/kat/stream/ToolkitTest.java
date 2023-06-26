package plus.kat.stream;

import org.junit.jupiter.api.Test;

import plus.kat.*;
import plus.kat.chain.*;
import plus.kat.spare.*;

import java.io.IOException;

import static plus.kat.spare.Parser.*;
import static plus.kat.stream.Toolkit.*;
import static org.junit.jupiter.api.Assertions.*;
import static java.nio.charset.StandardCharsets.*;

/**
 * @author kraity
 */
public class ToolkitTest {

    static class User {
        public int id;
        public String name;
    }

    @Test
    public void test_algoOf() throws IOException {
        String[] list = new String[]{
            "{id=1,name=kraity}",
            "{id=1,name=\"kraity\"}",
            "@Map{id:Int=1,name:String=\"kraity\"}",
            "  {  id = 1,  name = kraity}  ",
            "  {  id:Int = 1,  name:String = \"kraity\"}  ",
            "{\"id\":1,\"name\":\"kraity\"}",
            "  {  \"id\":1,  \"name\":\"kraity\"}   ",
            "  {  \"id\": 1,  \"name\": \"kraity\"}   ",
            "<User><id>1</id><name>kraity</name></User>"
        };

        Spare<User> spare =
            Spare.of(User.class);

        for (String text : list) {
            Value value = new Value(
                text.length(),
                text.getBytes(UTF_8)
            );

            Algo algo;
            assertNotNull(
                algo = algoOf(value)
            );

            try (Parser parser = with(spare)) {
                User user = parser.solve(
                    algo, Flow.of(value)
                );
                assertNotNull(user);
                assertEquals(1, user.id, text);
                assertEquals("kraity", user.name, text);
            }
        }
    }
}
