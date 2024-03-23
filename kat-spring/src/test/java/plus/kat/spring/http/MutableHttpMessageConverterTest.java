package plus.kat.spring.http;

import org.junit.jupiter.api.Test;

import plus.kat.Algo;

import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpInputMessage;

import java.io.*;
import java.nio.charset.*;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static java.nio.charset.StandardCharsets.*;
import static plus.kat.spring.http.MutableHttpMessageConverter.*;

/**
 * @author kraity
 */
public class MutableHttpMessageConverterTest {

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

    @Test
    public void test() throws IOException {
        HashMap<Algo, String> in = new HashMap<>();
        HashMap<Algo, String> out = new HashMap<>();

        in.put(Algo.KAT, "{id=1,name=陆之岇}");
        out.put(Algo.KAT, "{name=\"陆之岇\",id=1}");

        in.put(Algo.DOC, "<User><id>1</id><name>陆之岇</name></User>");
        out.put(Algo.DOC, "<plus.kat.spring.http.MutableHttpMessageConverterTest$User><name>陆之岇</name><id>1</id></plus.kat.spring.http.MutableHttpMessageConverterTest$User>");

        in.put(Algo.JSON, "{\"id\":1,\"name\":\"陆之岇\"}");
        out.put(Algo.JSON, "{\"name\":\"陆之岇\",\"id\":1}");

        Charset[] charsets = {
            UTF_8, UTF_16, UTF_16LE, UTF_16BE
        };
        HashMap<Algo, MediaType[]> mediaTypes = new HashMap<>();

        mediaTypes.put(
            Algo.KAT, new MediaType[]{
                TEXT_KAT,
                APPLICATION_KAT
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

            User user = null;
            for (MediaType mediaType : mediaTypes.get(algo)) {
                assertTrue(converter.canRead(
                    User.class, User.class, mediaType
                ));
                assertTrue(converter.canWrite(
                    User.class, User.class, mediaType
                ));

                for (Charset charset : charsets) {
                    user = (User) converter.read(
                        User.class, User.class, new HttpInputMessage() {
                            @Override
                            public InputStream getBody() {
                                return new ByteArrayInputStream(
                                    in.get(algo).getBytes(charset)
                                );
                            }

                            @Override
                            public HttpHeaders getHeaders() {
                                HttpHeaders headers =
                                    new HttpHeaders();
                                headers.setContentType(
                                    new MediaType(
                                        mediaType, charset
                                    )
                                );
                                return headers;
                            }
                        }
                    );
                    assertEquals(1, user.id, charset::name);
                    assertEquals("陆之岇", user.name, charset::name);
                }
            }

            assertNotNull(user);
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
}
