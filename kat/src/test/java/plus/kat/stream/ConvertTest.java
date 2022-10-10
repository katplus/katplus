package plus.kat.stream;

import org.junit.jupiter.api.Test;

import static plus.kat.stream.Convert.*;
import static org.junit.jupiter.api.Assertions.*;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author kraity
 */
public class ConvertTest {

    @Test
    public void test_byte_array_to_char() {
        byte[] d0 = "k".getBytes(UTF_8);
        assertEquals('k', Convert.toChar(d0, d0.length, '\0'));

        byte[] d1 = "\n".getBytes(UTF_8);
        assertEquals('\n', Convert.toChar(d1, d1.length, '\0'));

        byte[] d2 = "Σ".getBytes(UTF_8);
        assertEquals(2, d2.length);
        assertEquals('\0', Convert.toChar(d2, 1, '\0'));
        assertEquals('Σ', Convert.toChar(d2, d2.length, '\0'));

        byte[] d3 = "陆".getBytes(UTF_8);
        assertEquals(3, d3.length);
        assertEquals('\0', Convert.toChar(d3, 1, '\0'));
        assertEquals('\0', Convert.toChar(d3, 2, '\0'));
        assertEquals('陆', Convert.toChar(d3, d3.length, '\0'));
        assertEquals('\0', Convert.toChar(new byte[]{d3[1], d3[2]}, 2, '\0'));
    }

    static class User {
        public int id;
        public String name;
    }

    @Test
    public void test() {
        String[] list = new String[]{
            "{:id(1):name(kraity)}",
            "${$:id(1)$:name(kraity)}",
            "${i:id(1)s:name(kraity)}",
            "  {  :id(1)  :name(kraity)}  ",
            "  {  $:id(1)  $:name(kraity)}  ",
            "{\"id\":1,\"name\":\"kraity\"}",
            "  {  'id':1,  'name':'kraity'}   ",
            "  {  'id':1,  'name':\"kraity\"}   ",
            "  {  \"id\":1,  \"name\":\"kraity\"}   ",
            "<User><id>1</id><name>kraity</name></User>"
        };

        for (String text : list) {
            User user = Convert.toObject(
                User.class, text
            );
            assertNotNull(user);
            assertEquals(1, user.id);
            assertEquals("kraity", user.name);
        }
    }

    @Test
    public void test_byte_array_to_char_array() {
        byte[] d0 = "kraity".getBytes(UTF_8);
        assertEquals("kraity", new String(toChars(d0, 0, d0.length)));

        byte[] d1 = "陆之岇".getBytes(UTF_8);
        assertEquals("陆之岇", new String(toChars(d1, 0, d1.length)));

        byte[] d2 = "😀".getBytes(UTF_8);
        assertEquals("😀", new String(toChars(d2, 0, d2.length)));

        byte[] d3 = "陆之岇+😀+katplus".getBytes(UTF_8);
        assertEquals("陆之岇+😀+katplus", new String(toChars(d3, 0, d3.length)));
    }
}
