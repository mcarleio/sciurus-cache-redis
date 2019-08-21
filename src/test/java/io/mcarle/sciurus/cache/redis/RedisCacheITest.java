package io.mcarle.sciurus.cache.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.mcarle.sciurus.Sciurus;
import io.mcarle.sciurus.annotation.Cache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RedisCacheITest {

    @Mock
    private RedisClient redisClient;
    @Mock
    private StatefulRedisConnection<String, RedisData> redisConnection;
    @Mock
    private RedisCommands<String, RedisData> syncCommands;

    @BeforeEach
    public void setUp() {
        when(redisClient.connect(any(RedisJsonCodec.class))).thenReturn(redisConnection);
        when(redisConnection.sync()).thenReturn(syncCommands);

        Sciurus.registerCache(Sciurus.CACHE_GLOBAL, new RedisCache(redisClient));
    }

    @Cache(time = 1000)
    public String testMethod() {
        return "" + Math.random();
    }

    @Test
    public void checkNoInteractionsOnStoppedCache() {
        // given
        Sciurus.stopCache();

        // when
        testMethod();

        // then
        verifyNoMoreInteractions(syncCommands);
    }

    @Test
    public void checkInteractionsOnStartedCache() {
        // given
        Sciurus.startCache();

        // when
        testMethod();

        // then
        verify(syncCommands, times(1)).get(any());
        verify(syncCommands, times(1)).set(any(), any(), any());
    }

}
