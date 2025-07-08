package com.example.betickettrain.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;
    @Value("${spring.data.redis.password}")
    private String password;
    @Value("${spring.data.redis.username}")
    private String username;
    // Cấu hình RedisConnectionFactory
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisHost);
        config.setPort(redisPort);
        config.setUsername(username);
        config.setPassword(RedisPassword.of(password));

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .useSsl()
                .disablePeerVerification()// <-- BẮT BUỘC Railway Redis
                .build();

        return new LettuceConnectionFactory(config, clientConfig);
    }

    // Cấu hình ObjectMapper cho việc serialize/deserialize các đối tượng Java 8 Date/Time types
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerSubtypes(PageImpl.class); // Đăng ký PageImpl để hỗ trợ phân trang
        objectMapper.registerModule(new JavaTimeModule()); // Đăng ký module JavaTimeModule để hỗ trợ LocalDateTime
        return objectMapper;
    }

    // Cấu hình RedisSerializer sử dụng ObjectMapper đã cấu hình
    @Bean
    public GenericJackson2JsonRedisSerializer redisSerializer(ObjectMapper objectMapper) {
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }

    // Cấu hình RedisTemplate cho việc tương tác với Redis
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory, GenericJackson2JsonRedisSerializer redisSerializer) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer()); // Serializer cho key
        template.setValueSerializer(redisSerializer);
        // Serializer cho value
        template.setHashKeySerializer(new StringRedisSerializer()); // Serializer cho hash key
        template.setHashValueSerializer(redisSerializer); // Serializer cho hash value
        template.afterPropertiesSet();
        return template;
    }

    // Cấu hình RedisCacheManager để quản lý cache
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory, GenericJackson2JsonRedisSerializer redisSerializer) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(60)) // Thời gian sống của cache là 60 phút
                .disableCachingNullValues() // Không cache giá trị null
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())) // Serializer cho key
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer)); // Serializer cho value

        return RedisCacheManager.builder(connectionFactory).cacheDefaults(config).transactionAware().build();
    }
}
