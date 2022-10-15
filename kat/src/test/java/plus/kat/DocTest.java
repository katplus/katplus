package plus.kat;

import org.junit.jupiter.api.Test;

import plus.kat.anno.Embed;
import plus.kat.anno.Expose;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static plus.kat.Spare.lookup;

public class DocTest {

    @Test
    public void test_pretty() {
        User user = new User();
        user.id = 1;
        user.name = "kraity";

        String pretty =
            "<User>\n" +
                "  <id>1</id>\n" +
                "  <name>kraity</name>\n" +
                "  <blocked>false</blocked>\n" +
                "</User>";
        assertEquals(pretty, Doc.pretty(user));

        Map<String, Object> extra = new HashMap<>();
        extra.put("k", new User[]{user});

        String string =
            "<M>\n" +
                "  <k>\n" +
                "    <User>\n" +
                "      <id>1</id>\n" +
                "      <name>kraity</name>\n" +
                "      <blocked>false</blocked>\n" +
                "    </User>\n" +
                "  </k>\n" +
                "</M>";
        assertEquals(string, Doc.pretty(extra));
    }

    @Test
    public void test_down() {
        Supplier supplier = Supplier.ins();

        User user = supplier.down(
            User.class, Event.ascii(
                "<author><id>1</id><name>kraity</name><extra id=\"1\" /><extra><key>\"/\"</key><key>true</key><!-- comment --><data><![CDATA[this is data]]></data></extra><blocked>true</blocked></author>"
            )
        );

        assertNotNull(user);
        assertEquals(1, user.id);
        assertTrue(user.blocked);
        assertEquals("kraity", user.name);
    }

    @Test
    public void test_markup() {
        lookup(User.class);

        User user = new User();
        user.id = 1;
        user.name = "kraity";

        HashMap<String, Object> data = new HashMap<>();
        data.put("title", "mark");
        data.put("author", user);

        assertEquals(
            "<article><author><id>1</id><name>kraity</name><blocked>false</blocked></author><title>mark</title></article>",
            Doc.encode("article", data)
        );
    }

    @Test
    public void test_parse() throws IOException {
        Spare<Article> spare =
            lookup(Article.class);

        String text = "<plus.kat.article><title>mark</title><author><id>1</id><name>kraity</name><blocked>false</blocked></author><extra><date>2022-22-22</date></extra></plus.kat.article>";

        Article article = spare.down(text);
        assertNotNull(article);

        try (Chan chan = spare.mark(article)) {
            assertEquals(
                text, chan.toString()
            );
        }
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void test_parse2() {
        Supplier supplier = Supplier.ins();

        String text = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
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
            "</channel>";

        Map data = supplier.down(
            Map.class, new Event<>(text)
        );

        assertNotNull(data);
        assertEquals("KAT+", data.get("title"));
        assertEquals("{title=KAT+, link=https://kat.plus/, atom:link={href=https://kat.plus/}, item={id=1, title=<this is 'KAT' & \"+\">, link=https://kat.plus/MzE.html, view={value=∞}, author=kraity, content=this is data}}", data.toString());
    }

    @Test
    public void test_json_channel() throws IOException {
        String text = Sugar.doc("Story", it -> {
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
        });

        assertEquals(
            "<Story><id>100001</id><title>kat</title><meta><tag>kat</tag><view>9999</view></meta><author><id>1</id><name>kraity</name></author></Story>", text
        );
    }

    @Test
    public void test_encode1() {
        Map<Object, Object> data = new HashMap<>();
        data.put("别名", "陆之岇");
        assertEquals(
            "<M><别名>陆之岇</别名></M>", Doc.encode(data)
        );
        assertEquals(
            "<M><\\u522B\\u540D>\\u9646\\u4E4B\\u5C87</\\u522B\\u540D></M>", Doc.encode(data, Flag.UNICODE)
        );
    }

    @Embed("User")
    static class User {
        @Expose("id")
        private int id;

        @Expose("name")
        private String name;

        @Expose({"blocked", "disabled"})
        private boolean blocked;
    }

    @Embed("plus.kat.article")
    static class Article {
        @Expose("title")
        private String title;

        @Expose("author")
        private User author;

        @Expose("extra")
        private Map<String, Object> extra;
    }
}
