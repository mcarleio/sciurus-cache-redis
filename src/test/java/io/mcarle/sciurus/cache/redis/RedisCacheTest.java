package io.mcarle.sciurus.cache.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.protocol.CommandArgs;
import io.mcarle.sciurus.ExecutionIdentifier;
import io.mcarle.sciurus.cache.CustomCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class RedisCacheTest {

    @Mock
    private RedisClient redisClient;
    @Mock
    private StatefulRedisConnection<String, RedisData> redisConnection;
    @Mock
    private RedisCommands<String, RedisData> syncCommands;

    private RedisCache testee;
    private ExecutionIdentifier sampleExecution = new ExecutionIdentifier("methodSignature", new Object[]{"param1", "param2"});

    @BeforeEach
    public void setUp() {
        when(redisClient.connect(any(RedisJsonCodec.class))).thenReturn(redisConnection);
        when(redisConnection.sync()).thenReturn(syncCommands);

        testee = new RedisCache(redisClient);
    }

    @Test
    public void getCachedObject() {
        // given
        when(syncCommands.get(getExecutionIdentifierString(sampleExecution))).thenReturn(new RedisData("Test"));

        // when
        Object cachedValue = testee.get(sampleExecution);

        // then
        assertThat(cachedValue, is("Test"));
    }

    @Test
    public void getCachedNull() {
        // given
        when(syncCommands.get(getExecutionIdentifierString(sampleExecution))).thenReturn(new RedisData(null));

        // when
        Object cachedValue = testee.get(sampleExecution);

        // then
        assertThat(cachedValue, is(nullValue()));
    }

    @Test
    public void getNoCachedValue() {
        // given
        when(syncCommands.get(getExecutionIdentifierString(sampleExecution))).thenReturn(null);

        // when
        Object cachedValue = testee.get(sampleExecution);

        // then
        assertThat(cachedValue, is(CustomCache.EMPTY));
    }

    @Test
    public void putCachedObject() {
        CommandArgs commandArgs = mock(CommandArgs.class);
        when(commandArgs.add(anyString())).thenReturn(commandArgs);

        // when
        testee.put(sampleExecution, "Test", Duration.ofSeconds(5));

        // then
        ArgumentCaptor<RedisData> redisDataArgumentCaptor = ArgumentCaptor.forClass(RedisData.class);
        ArgumentCaptor<SetArgs> setArgsArgumentCaptor = ArgumentCaptor.forClass(SetArgs.class);
        verify(syncCommands, times(1)).set(
                eq(getExecutionIdentifierString(sampleExecution)),
                redisDataArgumentCaptor.capture(),
                setArgsArgumentCaptor.capture()
        );
        assertThat(redisDataArgumentCaptor.getValue().getCachedValue(), is("Test"));

        setArgsArgumentCaptor.getValue().build(commandArgs);
        verify(commandArgs).add(eq("PX"));
        verify(commandArgs).add(eq(Duration.ofSeconds(5).toMillis()));
    }

    @Test
    public void putCachedNull() {
        CommandArgs commandArgs = mock(CommandArgs.class);
        when(commandArgs.add(anyString())).thenReturn(commandArgs);

        // when
        testee.put(sampleExecution, null, Duration.ofSeconds(5));

        // then
        ArgumentCaptor<RedisData> redisDataArgumentCaptor = ArgumentCaptor.forClass(RedisData.class);
        ArgumentCaptor<SetArgs> setArgsArgumentCaptor = ArgumentCaptor.forClass(SetArgs.class);
        verify(syncCommands, times(1)).set(
                eq(getExecutionIdentifierString(sampleExecution)),
                redisDataArgumentCaptor.capture(),
                setArgsArgumentCaptor.capture()
        );
        assertThat(redisDataArgumentCaptor.getValue().getCachedValue(), is(nullValue()));

        setArgsArgumentCaptor.getValue().build(commandArgs);
        verify(commandArgs).add(eq("PX"));
        verify(commandArgs).add(eq(Duration.ofSeconds(5).toMillis()));
    }

    @Test
    public void shouldCloseConnectionOnPostDeregister() {
        // when
        testee.postDeregister();

        // then
        verify(redisConnection, times(1)).close();
        verify(redisClient, times(1)).shutdown();
        verifyNoMoreInteractions(redisClient, redisConnection, syncCommands);
    }

    private String getExecutionIdentifierString(final ExecutionIdentifier executionIdentifier) {
        return executionIdentifier.toString() + executionIdentifier.hashCode();
    }
}
