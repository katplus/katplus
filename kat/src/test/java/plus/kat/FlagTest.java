package plus.kat;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class FlagTest {

    @Test
    public void test_DATE_AS_TIMESTAMP() {
        Meta meta = new Meta();

        meta.setDate(
            new Date(1641871353000L)
        );

        assertEquals(
            "{\"date\":\"2022-01-11 11:22:33\"}", new Json(meta).toString()
        );

        assertEquals(
            "{\"date\":\"1641871353000\"}",
            new Json(meta, Flag.DATE_AS_TIMESTAMP).toString()
        );
    }

    static class Meta {
        Date date;

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }
}
