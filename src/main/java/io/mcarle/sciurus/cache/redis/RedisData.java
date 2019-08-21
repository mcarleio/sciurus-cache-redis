package io.mcarle.sciurus.cache.redis;

import java.io.*;
import java.nio.ByteBuffer;

class RedisData implements Serializable {

    private final Serializable value;

    RedisData(final Serializable value) {
        this.value = value;
    }

    static ByteBuffer toByteBuffer(final RedisData redisData) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(redisData);
            oos.close();
            return ByteBuffer.wrap(baos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static RedisData fromByteBuffer(final ByteBuffer byteBuffer) {
        try {
            byte[] data = new byte[byteBuffer.remaining()];
            byteBuffer.get(data);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            Object o = ois.readObject();
            ois.close();
            return (RedisData) o;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    Serializable getCachedValue() {
        return value;
    }
}
