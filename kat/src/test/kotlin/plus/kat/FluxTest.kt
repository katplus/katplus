package plus.kat

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class FluxTest {

    @Test
    fun test_kat() {
        assertEquals(
            """{id=100001,title="KAT+",meta={view=99},author={id=1,name="kraity"}}""",
            kat("Story") {
                it["id"] = 100001
                it["title"] = "KAT+"
                it["meta"] = { meta ->
                    meta["view"] = 99
                }
                it["author", "User"] = { user ->
                    user["id"] = 1
                    user["name"] = "kraity"
                }
            }
        )
    }

    @Test
    fun test_json() {
        assertEquals(
            """{"id":100001,"title":"KAT+","author":{"id":1,"name":"kraity"}}""",
            json {
                it["id"] = 100001
                it["title"] = "KAT+"
                it["author"] = { user ->
                    user["id"] = 1
                    user["name"] = "kraity"
                }
            }
        )
    }

    @Test
    fun test_mark() {
        assertEquals(
            "<Story><id>100001</id><title>KAT+</title><author><id>1</id><name>kraity</name></author></Story>",
            doc("Story") {
                it["id"] = 100001
                it["title"] = "KAT+"
                it["author"] = { user ->
                    user["id"] = 1
                    user["name"] = "kraity"
                }
            }
        )
    }
}
