package plus.kat;

import org.junit.jupiter.api.Test;
import plus.kat.anno.Embed;
import plus.kat.anno.Expose;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

        assertEquals(
            text, spare.mark(article).toString()
        );
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
        Doc chan = new Doc();
        chan.set("Story", c -> {
            c.set("id", 100001);
            c.set("title", "KAT+");
            c.set("meta", $ -> {
                $.set("tag", "kat");
                $.set("view", 9999);
            });
            c.set("author", "User", $ -> {
                $.set("id", 1);
                $.set("name", "kraity");
            });
        });

        assertEquals(
            "<Story><id>100001</id><title>KAT+</title><meta><tag>kat</tag><view>9999</view></meta><author><id>1</id><name>kraity</name></author></Story>", chan.toString()
        );
    }

    @Test
    public void test_encode1() {
        assertEquals(
            "<s>\\u9646\\u4E4B\\u5C87</s>", Doc.encode(
                "陆之岇", Flag.UNICODE
            )
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
