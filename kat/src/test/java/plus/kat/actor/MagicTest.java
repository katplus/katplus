package plus.kat.actor;

import org.junit.jupiter.api.Test;

import plus.kat.*;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class MagicTest {

    @Test
    public void test_with_other() {
        Sample sample = new Sample();
        sample.alias = "kat";
        sample.entity = new Model("kraity");

        assertEquals(
            "{alias=\"kat\",entity={name=\"kraity\"}}", Kat.encode(sample)
        );
    }

    interface Entity {
        String getName();
    }

    static class Model implements Entity {
        String name;

        Model(String s) {
            name = s;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    static class Sample {
        public String alias;

        public Entity entity;
    }

    static class Region {
        @Magic("date")
        private Date date = new Date(1645539742000L);

        private Date time = new Date(1645539742000L);

        @Magic("time")
        public void setTime(
            Date time
        ) {
            this.time = time;
        }

        public Date getTime() {
            return time;
        }
    }

    @Test
    public void test_with_default() {
        Spare<Region> spare
            = Spare.of(Region.class);

        Region region = spare.read(
            Flow.of(
                "{date=null,time=\"\"}"
            )
        );

        assertNotNull(region);
        assertNotNull(region.date);
        assertNotNull(region.time);

        assertEquals(1645539742000L, region.date.getTime());
        assertEquals(1645539742000L, region.time.getTime());
    }

    @Test
    public void test_with_not_default() {
        Spare<Region> spare
            = Spare.of(Region.class);

        Region region = spare.read(
            Flow.of(
                "{date=\"2022-06-26 18:06:00\",time=\"2022-06-26 18:12:00\"}"
            )
        );

        assertNotNull(region);
        assertNotNull(region.date);
        assertNotNull(region.time);

        assertEquals(1656237960000L, region.date.getTime());
        assertEquals(1656238320000L, region.time.getTime());
    }
}
