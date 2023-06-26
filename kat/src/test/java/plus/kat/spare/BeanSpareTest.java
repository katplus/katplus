package plus.kat.spare;

import org.junit.jupiter.api.Test;

import plus.kat.*;
import plus.kat.actor.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class BeanSpareTest {

    static class Post {
        public int id;
        public String title;
    }

    static class PostVO extends Post {
        public int created;
        public int modified;
    }

    @Test
    public void test_child_and_parent1() throws IOException {
        Spare<PostVO> spare =
            Spare.of(PostVO.class);

        assertInstanceOf(
            BeanSpare.class, spare
        );

        PostVO vo = new PostVO();
        try (Chan chan = spare.write(vo)) {
            assertEquals("{id=0,title=null,created=0,modified=0}", chan.toString());
        }
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
    public void test_child_and_parent2() throws IOException {
        Spare<UserVO0> spare =
            Spare.of(UserVO0.class);

        assertInstanceOf(
            BeanSpare.class, spare
        );

        UserVO0 vo = new UserVO0();
        try (Chan chan = spare.write(vo)) {
            assertEquals("{id=0,name=null,nickname=null,token=null,created=0,modified=0}", chan.toString());
        }
    }

    static class UserVO extends User {
        public int role;

        @Magic(index = -7)
        public String token;

        public String nickname;
    }

    @Test
    public void test_child_and_parent3() throws IOException {
        Spare<UserVO> spare =
            Spare.of(UserVO.class);

        assertInstanceOf(
            BeanSpare.class, spare
        );

        UserVO vo = new UserVO();
        try (Chan chan = spare.write(vo)) {
            assertEquals("{id=0,name=null,role=0,nickname=null,token=null,created=0,modified=0}", chan.toString());
        }
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
    public void test_child_and_parent4() throws IOException {
        Spare<UserDTO> spare =
            Spare.of(UserDTO.class);

        assertInstanceOf(
            BeanSpare.class, spare
        );

        UserDTO dto = new UserDTO();
        try (Chan chan = spare.write(dto)) {
            assertEquals("{uuid=0,iv=0,tag=0,salt=null,token=null,id=0,name=null,role=0,nickname=null,password=null,access=false,blocked=false,created=0,modified=0,destroy=false}", chan.toString());
        }
    }
}
