package com.e_comerce.config;

import java.time.Duration;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
public class RedisAndRabbitConfig {

    public static final String EMAIL_QUEUE = "support-email-queue";
    public static final String IMAGE_QUEUE = "s3-image-queue";
    public static final String IMAGE_DEL_QUEUE ="s3-image-del-queue";

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // Spring to use Redis instead of In-memory
        RedisCacheConfiguration config= RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(9)).serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(GenericJacksonJsonRedisSerializer.builder().build())); // default ttl is 10 mins and currently stored as Json
        return RedisCacheManager.builder(connectionFactory).cacheDefaults(config).build();
    }
    // 1-A. Initialize Email Queue
    @Bean
    public Queue emailQueue(){
        // atrue = durable queue (survives server restarts)
        return new Queue(EMAIL_QUEUE, true);
    }
    // 1-B. Initialize Image Queue
    @Bean
    public Queue imageQueue(){
        return new Queue(IMAGE_QUEUE,true);
    }
    // 1-c. Initialize Image Deletion Queue
    @Bean
    public Queue imageDeletionQueue(){
        return new Queue(IMAGE_DEL_QUEUE,true);
    }

    // 2. The JSON Message Converter
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
    // 3. The RabbitTemplate using the JSON Converter
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
