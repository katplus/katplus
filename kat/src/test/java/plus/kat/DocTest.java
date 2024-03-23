package plus.kat;

import org.junit.jupiter.api.Test;

import plus.kat.actor.*;

import java.io.*;
import java.util.*;

import static plus.kat.Pure.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class DocTest {

    static class User {
        @Magic("id")
        private int id;

        @Magic("name")
        private String name;

        @Magic({"blocked", "disabled"})
        private boolean blocked;
    }

    @Test
    public void test_pretty() throws IOException {
        User user = new User();
        user.id = 1;
        user.name = "kraity";

        String pretty =
            "<plus.kat.DocTest$User>\n" +
                "  <id>1</id>\n" +
                "  <name>kraity</name>\n" +
                "  <blocked>false</blocked>\n" +
                "</plus.kat.DocTest$User>";
        try (Chan chan = Doc.pretty(user)) {
            assertEquals(pretty, chan.toString());
        }

        Map<String, Object> extra = new HashMap<>();
        extra.put("developers", new User[]{user});

        String string =
            "<Map>\n" +
                "  <developers>\n" +
                "    <plus.kat.DocTest$User>\n" +
                "      <id>1</id>\n" +
                "      <name>kraity</name>\n" +
                "      <blocked>false</blocked>\n" +
                "    </plus.kat.DocTest$User>\n" +
                "  </developers>\n" +
                "</Map>";
        try (Chan chan = Doc.pretty(extra)) {
            assertEquals(string, chan.toString());
        }
    }

    @Test
    public void test_down() throws IOException {
        Spare<User> spare =
            Spare.of(User.class);

        User user = spare.down(
            Flow.of(
                "<author><id>1</id><name>kraity</name><extra id=\"1\" /><extra><key>\"/\"</key><key>true</key><!-- comment --><data><![CDATA[this is data]]></data></extra><blocked>true</blocked></author>"
            )
        );

        assertNotNull(user);
        assertEquals(1, user.id);
        assertTrue(user.blocked);
        assertEquals("kraity", user.name);
    }

    @Test
    public void test_mark() throws IOException {
        User user = new User();
        user.id = 1;
        user.name = "kraity";

        HashMap<String, Object> data = new HashMap<>();
        data.put("title", "mark");
        data.put("author", user);

        try (Chan chan = Doc.encode(data)) {
            assertEquals(
                "<Map><author><id>1</id><name>kraity</name><blocked>false</blocked></author><title>mark</title></Map>", chan.toString()
            );
        }
    }

    static class Article {
        @Magic("title")
        private String title;

        @Magic("author")
        private User author;

        @Magic("extra")
        private Map<String, Object> extra;
    }

    @Test
    public void test_parse() throws IOException {
        Spare<Article> spare =
            Spare.of(Article.class);

        String text = "<plus.kat.DocTest$Article><title>mark</title><author><id>1</id><name>kraity</name><blocked>false</blocked></author><extra><date>2022-22-22</date></extra></plus.kat.DocTest$Article>";

        Article article = spare.down(
            Flow.of(text)
        );
        assertNotNull(article);

        try (Chan chan = spare.mark(article)) {
            assertEquals(
                text, chan.toString()
            );
        }
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void test_parse2() throws IOException {
        Map data = Spare.of(Map.class).down(
            Flow.of(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                    "<channel>\n" +
                    "<title>KAT+</title>\n" +
                    "<link>https://kat.plus/</link>\n" +
                    "<atom:link href=\"https://kat.plus/\"/>\n" +
                    "<item id=\"1\">\n" +
                    "<title>&lt;this is &apos;KAT&apos; &amp; &quot;+&quot;&gt;</title>\n" +
                    "<link>https://kat.plus/MzE.html</link>\n" +
                    "<view value=\"∞\">99+</view>\n" +
                    "<author>kraity</author>\n" +
                    "<!-- This is a comment -->\n" +
                    "<content><![CDATA[this is data]]></content>\n" +
                    "</item>\n" +
                    "</channel>"
            )
        );

        assertNotNull(data);
        assertEquals("KAT+", data.get("title"));
        assertEquals("{title=KAT+, link=https://kat.plus/, atom:link={href=https://kat.plus/}, item={id=1, title=<this is 'KAT' & \"+\">, link=https://kat.plus/MzE.html, view={value=∞, =99+}, author=kraity, content=this is data}}", data.toString());
    }

    @Test
    public void test_json_channel() throws IOException {
        try (Chan chan = doc(it -> {
            it.set("id", 100001);
            it.set("title", "kat");
            it.set("meta", meta -> {
                meta.set("tag", "kat");
                meta.set("view", 9999);
            });
            it.set("author", "User", user -> {
                user.set("id", 1);
                user.set("name", "kraity");
            });
        })) {
            assertEquals(
                "<Map><id>100001</id><title>kat</title><meta><tag>kat</tag><view>9999</view></meta><author><id>1</id><name>kraity</name></author></Map>", chan.toString()
            );
        }
    }

    @Test
    public void test_encode1() throws IOException {
        Map<Object, Object> data = new HashMap<>();
        data.put("别名", "陆之岇");
        try (Chan chan = Doc.encode(data)) {
            assertEquals(
                "<Map><别名>陆之岇</别名></Map>", chan.toString()
            );
        }

        try (Chan chan = Doc.encode(data, Flag.UNICODE)) {
            assertEquals(
                "<Map><\\u522B\\u540D>\\u9646\\u4E4B\\u5C87</\\u522B\\u540D></Map>", chan.toString()
            );
        }
    }
}
