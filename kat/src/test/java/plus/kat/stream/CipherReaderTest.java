package plus.kat.stream;

import org.junit.jupiter.api.Test;

import plus.kat.Chan;
import plus.kat.Event;
import plus.kat.Spare;
import plus.kat.anno.Embed;
import plus.kat.anno.Expose;
import plus.kat.chain.Value;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.*;

public class CipherReaderTest {

    @Test
    public void test_cipher_input_stream_aes() throws Exception {
        SecretKeySpec ks = new SecretKeySpec(
            "0123456789ABCDEf".getBytes(US_ASCII), "AES"
        );
        IvParameterSpec ps = new IvParameterSpec(
            "0123456789abcdef".getBytes(US_ASCII)
        );

        Cipher cipher = Cipher.getInstance(
            "AES/CBC/PKCS5Padding"
        );
        cipher.init(
            Cipher.ENCRYPT_MODE, ks, ps
        );

        String text = "User{i:id(1)s:name(kraity)}";
        Value value = new Value(text);

        ByteArrayInputStream input =
            new ByteArrayInputStream(
                value.doFinal(cipher)
            );

        cipher.init(
            Cipher.DECRYPT_MODE, ks, ps
        );

        Spare<User> spare = Spare
            .lookup(User.class);

        User user = spare.read(
            new Event<>(input, cipher)
        );

        assertNotNull(user);
        assertEquals(1, user.id);
        assertEquals("kraity", user.name);

        try (Chan chan = spare.write(user)) {
            assertEquals(text, chan.toString());
        }
    }

    @Test
    public void test_cipher_input_stream_rsa() throws Exception {
        byte[] pub = Base64.mime().decode(
            ("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDbUq4z/sg094rjoVBlofrwW+dj\n" +
                "ZrP4ntJsOmRwAIO1+elDOJtZdvzJz4DCCYTHFxgAwO/Aq0s7lfF7j7X2ZtD//IjM\n" +
                "1JaknujmHRzAq2D6h0Q0lixu4zod/LOqfIxhmpk3Dw3DJhAsqN4L1wBtlLwDu7S0\n" +
                "QKFYlRogt03ZCtWQVwIDAQAB").getBytes(US_ASCII)
        );

        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
            .generatePublic(new X509EncodedKeySpec(pub));

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);

        String text = "User{i:id(1)s:name(kraity)}";
        Value value = new Value(text);
        int length = value.length();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        for (int i = 0; i < length; i += 117) {
            out.write(
                value.doFinal(cipher, i,
                    Math.min(117, length - i)
                )
            );
        }

        ByteArrayInputStream input =
            new ByteArrayInputStream(
                out.toByteArray()
            );

        byte[] pri = Base64.mime().decode(
            ("MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANtSrjP+yDT3iuOh\n" +
                "UGWh+vBb52Nms/ie0mw6ZHAAg7X56UM4m1l2/MnPgMIJhMcXGADA78CrSzuV8XuP\n" +
                "tfZm0P/8iMzUlqSe6OYdHMCrYPqHRDSWLG7jOh38s6p8jGGamTcPDcMmECyo3gvX\n" +
                "AG2UvAO7tLRAoViVGiC3TdkK1ZBXAgMBAAECgYBfWZa6rC3GOUh8pgkZ5k3+aTYz\n" +
                "lNbxY7r/qnM37kFUwA2VV+rR/lTwN/I2aYT8OSIKUdbp4I6YnRubKGNneFOr6G1e\n" +
                "j4ZXg7PkGhWdDtT6M39/J1KvLPcqOcEwNMt32q1Q2WS1ehZZLMhzk0YMHZvkb5YR\n" +
                "SzoyUibSiBEi6lxpIQJBAPtuDc1S3cyTP9fnhlzqx9uxPvCTtzsx5YcOwhcdL5Y4\n" +
                "b7pj10UjXk7MOv+Q1/8UTnL/BvIeURwjL44N6FYOW+0CQQDfTzpCIFAFlxkLb+Ov\n" +
                "8srV5WHDxQJFUfDJf0wKL5NuUYIocZPolPCJ2uV5OQhznrRtGCT5lyRB28PsOg4l\n" +
                "PXzTAkEAk7cRpsqDgpgUDx13xAkvh/O3PZIbOUzUQ6e5AelktsXLZl8X7webdYHp\n" +
                "O5J2Q+dDO652/zIuhvBpFPU4xa5D9QJAMS9/Mn9xgLm2L3m2mdONb611aixjgqc4\n" +
                "tPkP45J1E7BqTcQuguUDUinfr2KrhOPo87qEsmDjAIqKTea/pN41NwJBAIJWd/da\n" +
                "gaPX+kBmZ5IS7mPnsuoGxLSSAg4ThVjN7sVWBJO9PMIIZQ56DZ4TJFZTG0NdEOfE\n" +
                "f8+nzhNJI1knBkk=").getBytes(US_ASCII)
        );

        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA")
            .generatePrivate(new PKCS8EncodedKeySpec(pri));
        cipher.init(Cipher.DECRYPT_MODE, priKey);

        Spare<User> spare = Spare
            .lookup(User.class);

        User user = spare.read(
            new Event<>(input, cipher)
        );

        assertNotNull(user);
        assertEquals(1, user.id);
        assertEquals("kraity", user.name);
        try (Chan chan = spare.write(user)) {
            assertEquals(text, chan.toString());
        }
    }

    @Embed("User")
    static class User {
        @Expose("id")
        private int id;

        @Expose("name")
        private String name;
    }
}
