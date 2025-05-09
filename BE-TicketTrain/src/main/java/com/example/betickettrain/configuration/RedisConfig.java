//package com.example.betickettrain.configuration;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.module.SimpleDeserializers;
//import com.fasterxml.jackson.databind.module.SimpleModule;
//import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
//import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.cache.RedisCacheConfiguration;
//import org.springframework.data.redis.cache.RedisCacheManager;
//import org.springframework.data.redis.connection.RedisConnection;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//import org.springframework.data.redis.core.HashOperations;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
//import org.springframework.data.redis.serializer.RedisSerializationContext;
//import org.springframework.data.redis.serializer.RedisSerializer;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//
//
//import java.time.Duration;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
//@Configuration
//@EnableCaching
//public class RedisConfig {
//   @Value("${spring.data.redis.host}")
//  private String redis_host;
//   @Value("${spring.data.redis.port}")
//  private int redis_port;
//
//  @Bean
//    LettuceConnectionFactory connectionFactory(){
//      RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(redis_host,
//              redis_port);
//
//      return new LettuceConnectionFactory(configuration);
//  }
//    //sử dụng redis+spring cache
//    @Bean
//    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
//        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
//                .entryTtl(Duration.ofMinutes(10)) // Thời gian sống của cache
//                .disableCachingNullValues()
//                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
//                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json()))
//
//                ;
//
//        return RedisCacheManager.builder(redisConnectionFactory)
//                .cacheDefaults(cacheConfig)
//
//                .build();
//    }
//    @Bean
//    RedisTemplate<String, Object> redisTemplate() {
//
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(connectionFactory());
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setHashKeySerializer(new StringRedisSerializer());
//
//        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
//        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
//        return template;
//    }
//
//    @Bean
//    ObjectMapper redisobjectMapper(){
//      ObjectMapper objectMapper = new ObjectMapper();
//        SimpleModule module = new SimpleModule();
//        module.addSerializer(LocalDateTime.class,new LocalDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME));
//        module.addDeserializer(LocalDateTime.class,new LocalDateTimeDeserializer(DateTimeFormatter.ISO_DATE_TIME));
//        module.setDeserializers(new SimpleDeserializers());
//        objectMapper.registerModule(module);
//        return objectMapper;
//    }
//    //khai báo cụ thể <> cho hashoperation vì nó là 1 interface
//@Bean
//    HashOperations<String ,String, Object> hashOperations(RedisTemplate<String,Object> redisTemplate){
//
//      return redisTemplate.opsForHash();
//}
//
//}
