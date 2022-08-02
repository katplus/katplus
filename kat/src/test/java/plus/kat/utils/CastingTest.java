package plus.kat.utils;

import org.junit.jupiter.api.Test;
import plus.kat.Spare;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author kraity
 */
public class CastingTest {

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
            User user = Casting.cast(
                User.class, text
            );
            assertNotNull(user);
            assertEquals(1, user.id);
            assertEquals("kraity", user.name);
        }
    }

    static class User {
        public int id;
        public String name;
    }
}
