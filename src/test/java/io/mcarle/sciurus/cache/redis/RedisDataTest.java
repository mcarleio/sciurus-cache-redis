package io.mcarle.sciurus.cache.redis;

import org.junit.jupiter.api.Test;

import java.io.NotSerializableException;
import java.io.Serializable;
import java.nio.ByteBuffer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RedisDataTest {

    @Test
    void toByteBuffer() {
        RuntimeException re = assertThrows(
                RuntimeException.class,
                () -> RedisData.toByteBuffer(new RedisData(new NotReallySerializable(new NotSerializable())))
        );
        assertThat(re.getCause(), instanceOf(NotSerializableException.class));
    }

    @Test
    void fromByteBuffer() {
        assertThrows(
                RuntimeException.class,
                () -> RedisData.fromByteBuffer(ByteBuffer.wrap(new byte[]{1, 2, 3}))
        );
    }

    static class NotReallySerializable implements Serializable {

        private final NotSerializable notSerializable;

        NotReallySerializable(final NotSerializable notSerializable) {
            this.notSerializable = notSerializable;
        }
    }

    private static class NotSerializable {

    }
}