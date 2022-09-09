package plus.kat.spare;

import org.junit.jupiter.api.Test;
import plus.kat.Json;
import plus.kat.anno.Expose;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class OrderTest {

    static class Post {
        public int id;
        public String title;
    }

    static class PostVO extends Post {
        public int created;
        public int modified;
    }

    @Test
    public void test() {
        PostVO vo = new PostVO();
        assertEquals("{\"id\":0,\"title\":null,\"created\":0,\"modified\":0}", Json.encode(vo));
    }

    static class Entity {
        @Expose(index = -8)
        public int created;

        @Expose(index = -9)
        public int modified;
    }

    static class User extends Entity {
        public int id;
        public String name;
    }

    static class UserVO0 extends User {
        @Expose(index = -7)
        public String token;
        public String nickname;
    }

    @Test
    public void test0() {
        UserVO0 vo = new UserVO0();
        assertEquals("{\"id\":0,\"name\":null,\"nickname\":null,\"token\":null,\"created\":0,\"modified\":0}", Json.encode(vo));
    }

    static class UserVO extends User {
        public int role;

        @Expose(index = -7)
        public String token;

        public String nickname;
    }

    @Test
    public void test1() {
        UserVO vo = new UserVO();
        assertEquals("{\"id\":0,\"name\":null,\"role\":0,\"nickname\":null,\"token\":null,\"created\":0,\"modified\":0}", Json.encode(vo));
    }

    static class UserDO extends User {
        @Expose(index = 0)
        public int uuid;

        @Expose(index = -7)
        public String password;
    }

    static class UserDTO extends UserDO {
        public int role;

        @Expose(index = 0)
        public int iv;

        @Expose(index = 3)
        public String salt;

        public String nickname;

        @Expose(index = 2)
        public int tag;

        @Expose(index = -7)
        public boolean access;

        @Expose(index = 4)
        public String token;

        @Expose(index = -10)
        public boolean destroy;

        @Expose(index = -7)
        public boolean blocked;
    }

    @Test
    public void test2() {
        UserDTO dto = new UserDTO();
        assertEquals("{\"uuid\":0,\"iv\":0,\"tag\":0,\"salt\":null,\"token\":null,\"id\":0,\"name\":null,\"role\":0,\"nickname\":null,\"password\":null,\"access\":false,\"blocked\":false,\"created\":0,\"modified\":0,\"destroy\":false}", Json.encode(dto));
    }
}
