package io.mcarle.sciurus.cache.redis;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RedisJsonCodecTest {

    public static Serializable[] data() {
        HashMap<String, Serializable> secondMap = new HashMap<>();
        secondMap.put("777", new TestSerializableClass(true));
        HashMap<String, Serializable> myMap = new HashMap<>();
        myMap.put("123", new TestSerializableClass("String123"));
        myMap.put("333", new TestSerializableClass(new int[]{3, 3, 3}));
        myMap.put("555", new TestSerializableClass[]{new TestSerializableClass(new int[]{5, 5, 5})});
        myMap.put("666", secondMap);
        return new Serializable[]{
                null,
                "String",
                'c',
                true,
                false,
                Short.MAX_VALUE,
                Short.MIN_VALUE,
                Integer.MAX_VALUE,
                Integer.MIN_VALUE,
                Long.MAX_VALUE,
                Long.MIN_VALUE,
                Float.MAX_VALUE,
                Float.MIN_VALUE,
                Double.MAX_VALUE,
                Double.MIN_VALUE,
                TestEnum.VALUE1,
                TestEnum.VALUE2,
                new TestSerializableClass("String"),
                new TestSerializableClass('c'),
                new TestSerializableClass(true),
                new TestSerializableClass(false),
                new TestSerializableClass(Short.MAX_VALUE),
                new TestSerializableClass(Short.MIN_VALUE),
                new TestSerializableClass(Integer.MAX_VALUE),
                new TestSerializableClass(Integer.MIN_VALUE),
                new TestSerializableClass(Long.MAX_VALUE),
                new TestSerializableClass(Long.MIN_VALUE),
                new TestSerializableClass(Float.MAX_VALUE),
                new TestSerializableClass(Float.MIN_VALUE),
                new TestSerializableClass(Double.MAX_VALUE),
                new TestSerializableClass(Double.MIN_VALUE),
                new TestSerializableClass(TestEnum.VALUE1),
                new TestSerializableClass(TestEnum.VALUE2),
                new TestSerializableClass(new TestSerializableClass("String")),
                new TestSerializableClass(new TestSerializableClass(myMap)),
                myMap,
                new ArrayList<>(Arrays.asList(myMap, new TestSerializableClass(myMap)))
        };
    }

    @Test
    public void decodeKeyTest() {
        String key = "Hello World!";
        assertThat(
                new RedisJsonCodec().decodeKey(ByteBuffer.wrap(key.getBytes(StandardCharsets.UTF_8))),
                is(key)
        );
    }

    @Test
    public void encodeKeyTest() {
        String key = "Hello World!";
        assertThat(
                new RedisJsonCodec().encodeKey(key),
                is(ByteBuffer.wrap(key.getBytes(StandardCharsets.UTF_8)))
        );
    }

    @ParameterizedTest
    @MethodSource("data")
    public void encodeAndDecodeValueTest(final Serializable serializable) {
        RedisData value = new RedisData(serializable);
        assertThat(
                new Gson().toJson(
                        new RedisJsonCodec().decodeValue(
                                new RedisJsonCodec().encodeValue(value)
                        )
                ),
                is(new Gson().toJson(value))
        );
    }

    public enum TestEnum {
        VALUE1,
        VALUE2
    }

    public static class TestSerializableClass implements Serializable {

        private final Serializable someData;
        private final double number = Math.random();

        public TestSerializableClass(final Serializable someData) {
            this.someData = someData;
        }

    }
}