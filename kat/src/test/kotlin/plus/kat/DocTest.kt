package plus.kat

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class DocKtTest {

    @Test
    fun test_toJson() {
        assertEquals(
            "<User><id>1</id><name>kraity</name></User>",
            mapOf("id" to 1, "name" to "kraity").toDoc("User")
        )
    }

    @Test
    fun test_parse() {
        assertEquals(
            """{title=KAT+, link=https://kat.plus/, atom:link={href=https://kat.plus/}, item={id=1, title=<this is 'KAT' & "+">, link=https://kat.plus/MzE.html, view={value=∞}, author=kraity, content=this is data}}""",
            Doc.decode(
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
            ).toString()
        )
    }
}
