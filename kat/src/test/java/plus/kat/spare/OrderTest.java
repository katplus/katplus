package plus.kat.spare;

import org.junit.jupiter.api.Test;

import plus.kat.*;
import plus.kat.actor.*;

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
        assertEquals("{id=0,title=null,created=0,modified=0}", Kat.encode(vo));
    }

    static class Entity {
        @Magic(index = -8)
        public int created;

        @Magic(index = -9)
        public int modified;
    }

    static class User extends Entity {
        public int id;
        public String name;
    }

    static class UserVO0 extends User {
        @Magic(index = -7)
        public String token;
        public String nickname;
    }

    @Test
    public void test0() {
        UserVO0 vo = new UserVO0();
        assertEquals("{id=0,name=null,nickname=null,token=null,created=0,modified=0}", Kat.encode(vo));
    }

    static class UserVO extends User {
        public int role;

        @Magic(index = -7)
        public String token;

        public String nickname;
    }

    @Test
    public void test1() {
        UserVO vo = new UserVO();
        assertEquals("{id=0,name=null,role=0,nickname=null,token=null,created=0,modified=0}", Kat.encode(vo));
    }

    static class UserDO extends User {
        @Magic(index = 0)
        public int uuid;

        @Magic(index = -7)
        public String password;
    }

    static class UserDTO extends UserDO {
        public int role;

        @Magic(index = 0)
        public int iv;

        @Magic(index = 3)
        public String salt;

        public String nickname;

        @Magic(index = 2)
        public int tag;

        @Magic(index = -7)
        public boolean access;

        @Magic(index = 4)
        public String token;

        @Magic(index = -10)
        public boolean destroy;

        @Magic(index = -7)
        public boolean blocked;
    }

    @Test
    public void test2() {
        UserDTO dto = new UserDTO();
        assertEquals("{uuid=0,iv=0,tag=0,salt=null,token=null,id=0,name=null,role=0,nickname=null,password=null,access=false,blocked=false,created=0,modified=0,destroy=false}", Kat.encode(dto));
    }
}
