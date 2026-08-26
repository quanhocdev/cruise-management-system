package com.project.activityvisit.config;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

        @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
        private String bootstrapServers;

        @Bean
        public ConsumerFactory<String, Object> consumerFactory() {

                Map<String, Object> props = new HashMap<>();

                props.put(
                                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                                bootstrapServers);

                props.put(
                                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                                StringDeserializer.class);

                // ĐÃ BỎ: props.put(JsonDeserializer.TRUSTED_PACKAGES, ...)
                // ĐÃ BỎ: props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, ...)
                // -> Cấu hình JsonDeserializer chỉ qua setter bên dưới,
                // không trộn với props để tránh xung đột.

                // ===================================================
                // ObjectMapper hỗ trợ LocalDateTime / LocalDate...
                // ===================================================
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                JsonDeserializer<Object> jsonDeserializer = new JsonDeserializer<>(objectMapper);
                jsonDeserializer.addTrustedPackages("com.project.common.event");
                jsonDeserializer.setUseTypeMapperForKey(false);
                jsonDeserializer.setUseTypeHeaders(true); // thay cho USE_TYPE_INFO_HEADERS

                ErrorHandlingDeserializer<Object> errorHandlingDeserializer = new ErrorHandlingDeserializer<>(
                                jsonDeserializer);

                return new DefaultKafkaConsumerFactory<>(
                                props,
                                new StringDeserializer(),
                                errorHandlingDeserializer);
        }

        @Bean
        public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {

                ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();

                factory.setConsumerFactory(consumerFactory());

                return factory;
        }
}