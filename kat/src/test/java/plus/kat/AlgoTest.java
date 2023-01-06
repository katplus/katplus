package plus.kat;

import org.junit.jupiter.api.Test;

import plus.kat.chain.*;

import static plus.kat.Algo.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class AlgoTest {

    static class User {
        public int id;
        public String name;
    }

    @Test
    public void test0() {
        assertEquals(KAT, KAT);
        assertEquals(DOC, DOC);
        assertEquals(JSON, JSON);
    }

    @Test
    public void test1() {
        assertEquals(DOC, new Algo("xml"));
        assertEquals(DOC, new Algo("XML"));
        assertEquals(JSON, new Algo("json"));
        assertEquals(JSON, new Algo("JSON"));
        assertEquals(KAT, new Algo('^', "kat"));
        assertEquals(JSON, new Algo('\\', "json"));

        assertNotEquals(KAT, JSON);
        assertNotEquals(DOC, JSON);
        assertNotEquals(KAT, new Algo("kat"));
        assertNotEquals(DOC, new Algo("json"));
        assertNotEquals(KAT, new Algo("json"));
        assertNotEquals(KAT, new Algo('^', "kit"));
        assertNotEquals(DOC, new Algo('^', "xml"));
        assertNotEquals(JSON, new Algo('^', "json"));
    }

    @Test
    public void test_of() {
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

        Spare<User> spare =
            Spare.of(User.class);
        for (String text : list) {
            Algo algo = Algo.of(text);
            assertNotNull(algo);
            assertEquals(
                algo, Algo.of(
                    new Chain(text)
                )
            );

            User user = spare.solve(
                algo, new Event<>(text)
            );
            assertNotNull(user);
            assertEquals(1, user.id);
            assertEquals("kraity", user.name);
        }
    }
}
