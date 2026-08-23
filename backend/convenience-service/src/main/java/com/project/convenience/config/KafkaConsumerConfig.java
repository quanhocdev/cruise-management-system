package com.project.convenience.config;

import com.project.common.event.ProductTourAssignedEvent;
import com.project.common.event.ServiceTourAssignedEvent;
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

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    private Map<String, Object> baseConsumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "convenience-group-v2");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }

    // =========================================================================
    // 1. Consumer Config cho PRODUCT Event
    // =========================================================================
    @Bean
    public ConsumerFactory<String, ProductTourAssignedEvent> productConsumerFactory() {
        JsonDeserializer<ProductTourAssignedEvent> jsonDeserializer = new JsonDeserializer<>(
                ProductTourAssignedEvent.class);
        jsonDeserializer.addTrustedPackages("*");
        jsonDeserializer.setUseTypeHeaders(false);
        jsonDeserializer.setRemoveTypeHeaders(true);

        ErrorHandlingDeserializer<ProductTourAssignedEvent> errorHandlingDeserializer = new ErrorHandlingDeserializer<>(
                jsonDeserializer);

        Map<String, Object> props = baseConsumerProps();
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, errorHandlingDeserializer);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), errorHandlingDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProductTourAssignedEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ProductTourAssignedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(productConsumerFactory());
        return factory;
    }

    // =========================================================================
    // 2. Consumer Config cho SERVICE Event (Bổ sung mới)
    // =========================================================================
    @Bean
    public ConsumerFactory<String, ServiceTourAssignedEvent> serviceConsumerFactory() {
        JsonDeserializer<ServiceTourAssignedEvent> jsonDeserializer = new JsonDeserializer<>(
                ServiceTourAssignedEvent.class);
        jsonDeserializer.addTrustedPackages("*");
        jsonDeserializer.setUseTypeHeaders(false);
        jsonDeserializer.setRemoveTypeHeaders(true);

        ErrorHandlingDeserializer<ServiceTourAssignedEvent> errorHandlingDeserializer = new ErrorHandlingDeserializer<>(
                jsonDeserializer);

        Map<String, Object> props = baseConsumerProps();
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, errorHandlingDeserializer);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), errorHandlingDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ServiceTourAssignedEvent> serviceKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ServiceTourAssignedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(serviceConsumerFactory());
        return factory;
    }
}