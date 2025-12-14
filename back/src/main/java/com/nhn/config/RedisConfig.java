package com.nhn.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableCaching
@Slf4j
public class RedisConfig {
    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Value("${spring.redis.user}")
    private String user;

    @Value("${spring.redis.password}")
    private String password;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        final RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(this.redisHost,
                                                                                     this.redisPort);
        config.setUsername(this.user);
        config.setPassword(this.password);
        return new LettuceConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<Object, Object> redisTemplate(final RedisConnectionFactory redisConnectionFactory) {
        final RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        return template;
    }
}
