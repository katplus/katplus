package plus.kat.spring.http;

import org.junit.jupiter.api.Test;

import plus.kat.Algo;

import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpInputMessage;

import java.io.*;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author kraity
 */
public class MutableHttpMessageConverterTest {

    @Test
    public void test() throws IOException {
        HashMap<Algo, String> in = new HashMap<>();
        HashMap<Algo, String> out = new HashMap<>();

        in.put(Algo.KAT, "{i:id(1)s:name(kraity)}");
        out.put(Algo.KAT, "plus.kat.spring.http.MutableHttpMessageConverterTest$User{s:name(kraity)i:id(1)}");

        in.put(Algo.DOC, "<User><id>1</id><name>kraity</name></User>");
        out.put(Algo.DOC, "<plus.kat.spring.http.MutableHttpMessageConverterTest$User><name>kraity</name><id>1</id></plus.kat.spring.http.MutableHttpMessageConverterTest$User>");

        in.put(Algo.JSON, "{\"id\":1,\"name\":\"kraity\"}");
        out.put(Algo.JSON, "{\"name\":\"kraity\",\"id\":1}");

        HashMap<Algo, MediaType[]> mediaTypes = new HashMap<>();
        mediaTypes.put(
            Algo.KAT, new MediaType[]{
                MediaTypes.TEXT_KAT,
                MediaTypes.APPLICATION_KAT
            }
        );
        mediaTypes.put(
            Algo.DOC, new MediaType[]{
                MediaType.TEXT_XML,
                MediaType.APPLICATION_XML
            }
        );
        mediaTypes.put(
            Algo.JSON, new MediaType[]{
                MediaType.APPLICATION_JSON
            }
        );

        for (Algo algo : new Algo[]{Algo.KAT, Algo.DOC, Algo.JSON}) {
            MutableHttpMessageConverter converter =
                new MutableHttpMessageConverter(algo);

            for (MediaType mediaType : mediaTypes.get(algo)) {
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
                            in.get(algo).getBytes(UTF_8)
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
            assertEquals(out.get(algo), output.toString("UTF-8"));
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
