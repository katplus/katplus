package plus.kat.spring.http;

import org.junit.jupiter.api.Test;

import plus.kat.Job;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;

import java.io.*;
import java.util.HashMap;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class MutableHttpMessageConverterTest {

    @Test
    public void test() throws IOException {
        HashMap<Job, String> in = new HashMap<>();
        HashMap<Job, String> out = new HashMap<>();

        in.put(Job.KAT, "{i:id(1)s:name(kraity)}");
        out.put(Job.KAT, "plus.kat.spring.http.MutableHttpMessageConverterTest$User{s:name(kraity)i:id(1)}");

        in.put(Job.DOC, "<User><id>1</id><name>kraity</name></User>");
        out.put(Job.DOC, "<plus.kat.spring.http.MutableHttpMessageConverterTest$User><name>kraity</name><id>1</id></plus.kat.spring.http.MutableHttpMessageConverterTest$User>");

        in.put(Job.JSON, "{\"id\":1,\"name\":\"kraity\"}");
        out.put(Job.JSON, "{\"name\":\"kraity\",\"id\":1}");

        HashMap<Job, MediaType[]> mediaTypes = new HashMap<>();
        mediaTypes.put(
            Job.KAT, new MediaType[]{
                MediaTypes.TEXT_KAT,
                MediaTypes.APPLICATION_KAT
            }
        );
        mediaTypes.put(
            Job.DOC, new MediaType[]{
                MediaType.TEXT_XML,
                MediaType.APPLICATION_XML
            }
        );
        mediaTypes.put(
            Job.JSON, new MediaType[]{
                MediaType.APPLICATION_JSON
            }
        );

        for (Job job : Job.values()) {
            MutableHttpMessageConverter converter =
                new MutableHttpMessageConverter(job);

            for (MediaType mediaType : mediaTypes.get(job)) {
                assertTrue(converter.canRead(
                    User.class, User.class, mediaType
                ));
                assertTrue(converter.canWrite(
                    User.class, User.class, mediaType
                ));
            }

            User user = (User) converter.read(
                User.class, User.class, new HttpInputMessage() {
                    @Override
                    public InputStream getBody() {
                        return new ByteArrayInputStream(
                            in.get(job).getBytes(UTF_8)
                        );
                    }

                    @Override
                    public HttpHeaders getHeaders() {
                        return new HttpHeaders();
                    }
                }
            );
            assertEquals(1, user.id);
            assertEquals("kraity", user.name);

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            converter.write(
                user, User.class, MediaType.APPLICATION_JSON, new HttpOutputMessage() {
                    @Override
                    public OutputStream getBody() {
                        return output;
                    }

                    @Override
                    public HttpHeaders getHeaders() {
                        return new HttpHeaders();
                    }
                }
            );
            assertEquals(out.get(job), output.toString("UTF-8"));
        }
    }

    static class User {
        private int id;
        private String name;

        public void setId(
            int id
        ) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setName(
            String name
        ) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
