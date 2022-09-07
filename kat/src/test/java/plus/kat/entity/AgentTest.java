package plus.kat.entity;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class AgentTest {

    @Test
    public void test_combine() {
        User user = new User(
            1, "kraity"
        );

        UserDTO userDTO = new UserDTO();
        assertTrue(userDTO.combine(user));

        assertEquals(user.id, userDTO.id);
        assertEquals(user.name, userDTO.name);
    }

    @Test
    public void test_update() {
        HashMap<String, Object>
            data = new HashMap<>();

        data.put("id", 1);
        data.put("name", "kraity");

        UserDTO userDTO = new UserDTO();
        assertTrue(userDTO.update(data));

        assertEquals(1, userDTO.id);
        assertEquals("kraity", userDTO.name);
    }

    @Test
    public void test_migrate() {
        UserDTO userDTO = new UserDTO();
        User user = new User(
            1, "kraity"
        );

        assertTrue(user.migrate(userDTO));
        assertEquals(user.id, userDTO.id);
        assertEquals(user.name, userDTO.name);
    }

    static class User implements Agent {
        private int id;
        private String name;

        public User(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    static class UserDTO implements Agent {
        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
