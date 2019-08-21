package io.mcarle.sciurus.cache.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.mcarle.sciurus.ExecutionIdentifier;
import io.mcarle.sciurus.cache.CustomCache;

import java.io.Serializable;
import java.time.Duration;

public class RedisCache implements CustomCache {

    private RedisClient redisClient;
    private StatefulRedisConnection<String, RedisData> connection;
    private RedisCommands<String, RedisData> syncCommands;

    public RedisCache(RedisClient redisClient) {
        this.redisClient = redisClient;
        this.connection = redisClient.connect(new RedisJsonCodec());
        this.syncCommands = connection.sync();
    }

    @Override
    public Object get(ExecutionIdentifier executionIdentifier) {
        RedisData redisData = syncCommands.get(getKey(executionIdentifier));
        if (redisData == null) {
            return CustomCache.EMPTY;
        } else {
            return redisData.getCachedValue();
        }
    }

    @Override
    public void put(ExecutionIdentifier executionIdentifier, Serializable result, Duration duration) {
        syncCommands.set(getKey(executionIdentifier), new RedisData(result), SetArgs.Builder.px(duration.toMillis()));
    }

    @Override
    public void postDeregister() {
        connection.close();
        redisClient.shutdown();
    }

    private String getKey(ExecutionIdentifier executionIdentifier) {
        return executionIdentifier.toString() + executionIdentifier.hashCode();
    }
}
