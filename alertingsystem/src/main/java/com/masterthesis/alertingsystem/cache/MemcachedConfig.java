package com.masterthesis.alertingsystem.cache;

import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.transcoders.SerializingTranscoder;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.InetSocketAddress;

@Configuration
public class MemcachedConfig {

    @Bean
    public MemcachedClient memcachedClient() throws IOException {
        ConnectionFactoryBuilder builder = new ConnectionFactoryBuilder();
        builder.setTranscoder(new SerializingTranscoder());
        builder.setProtocol(ConnectionFactoryBuilder.Protocol.BINARY);
        
        return new MemcachedClient(
            builder.build(),
            java.util.Collections.singletonList(new InetSocketAddress("localhost", 11211))
        );
    }

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("default");
    }

}
