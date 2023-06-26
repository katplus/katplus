package plus.kat

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class DocKtTest {

    @Test
    fun test_doc() {
        assertEquals(
            "<Map><id>1</id><name>kraity</name></Map>",
            doc {
                it["id"] = 1
                it["name"] = "kraity"
            }.use {
                it.toString()
            }
        )
    }

    @Test
    fun test_Doc() {
        val doc = doc {
            it["id"] = 1
            it["name"] = "kraity"
        }

        doc.use {
            assertSame(Doc::class.java, it::class.java)
            assertEquals(
                "<Map><id>1</id><name>kraity</name></Map>", it.toString()
            )
        }
    }

    @Test
    fun test_toDoc() {
        assertEquals(
            "<Map><id>1</id><name>kraity</name></Map>",
            Doc.encode(mapOf("id" to 1, "name" to "kraity")).use { it.toString() }
        )
    }

    @Test
    fun test_down() {
        val spare = spare<Map<*, *>>()
        val map = spare.down(
            Flow.of(
                """
<?xml version="1.0" encoding="UTF-8" ?>
<channel>
<title>KAT+</title>
<link>https://kat.plus/</link>
<atom:link href="https://kat.plus/"/>
<item id="1">
<title>&lt;this is &apos;KAT&apos; &amp; &quot;+&quot;&gt;</title>
<link>https://kat.plus/MzE.html</link>
<view value="∞">99+</view>
<author>kraity</author>
<!-- This is a comment -->
<content><![CDATA[this is data]]></content>
</item>
</channel>"""
            )
        )
        assertEquals(
            """{title=KAT+, link=https://kat.plus/, atom:link={href=https://kat.plus/}, item={id=1, title=<this is 'KAT' & "+">, link=https://kat.plus/MzE.html, view={value=∞}, author=kraity, content=this is data}}""",
            map.toString()
        )
    }
}
