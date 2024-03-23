package plus.kat.spare;

import org.junit.jupiter.api.Test;

import plus.kat.chain.*;

import static plus.kat.Algo.*;
import static plus.kat.spare.BinarySpare.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class BinarySpareTest {

    @SuppressWarnings("deprecation")
    static Value value(
        Value ins, String alias
    ) {
        int size = alias.length();
        alias.getBytes(0, size, ins.flow(), 0);
        return ins.slip(size);
    }

    @Test
    public void test_algoOf() {
        Value v = new Value(64);
        assertEquals(KAT, algoOf(value(v, "{id=1,name=kraity}")));
        assertEquals(KAT, algoOf(value(v, "{id=1,name=\"kraity\"}")));
        assertEquals(KAT, algoOf(value(v, "{\"id\"=1,\"name\"=kraity}")));
        assertEquals(KAT, algoOf(value(v, "@User{id:Int=1,name:String=\"kraity\"}")));
        assertEquals(DOC, algoOf(value(v, "<User><id>1</id><name>kraity</name></User>")));
        assertEquals(KAT, algoOf(value(v, "[123,kraity,456]")));
        assertEquals(KAT, algoOf(value(v, "[123,456,\"789\"]")));
        assertEquals(KAT, algoOf(value(v, "[123,{id:Int=1},456]")));
        assertEquals(KAT, algoOf(value(v, "[123,\"kraity\",456]")));
        assertEquals(KAT, algoOf(value(v, "[123,{id=1,name=kraity},456]")));
        assertEquals(JSON, algoOf(value(v, "{\"id\":1,\"name\":\"kraity\"}")));
        assertEquals(JSON, algoOf(value(v, "[123,{\"id\":1,\"name\":\"kraity\"},456]")));
    }
}
