package io.mcarle.sciurus.cache.redis;

import io.lettuce.core.codec.RedisCodec;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class RedisJsonCodec implements RedisCodec<String, RedisData> {

    @Override
    public String decodeKey(final ByteBuffer bytes) {
        return StandardCharsets.UTF_8.decode(bytes).toString();
    }

    @Override
    public RedisData decodeValue(final ByteBuffer bytes) {
        return RedisData.fromByteBuffer(bytes);
    }

    @Override
    public ByteBuffer encodeKey(final String key) {
        return StandardCharsets.UTF_8.encode(key);
    }

    @Override
    public ByteBuffer encodeValue(final RedisData value) {
        return RedisData.toByteBuffer(value);
    }
}
