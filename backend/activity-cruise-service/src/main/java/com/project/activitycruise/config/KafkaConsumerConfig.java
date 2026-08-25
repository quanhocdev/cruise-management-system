package com.project.activitycruise.config;

import com.project.common.event.TourApprovedEvent;

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

        props.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers);

        props.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "activity-cruise-group-v1");

        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);

        return props;
    }

    // =========================================================
    // ConsumerFactory cho TourApprovedEvent
    // =========================================================

    @Bean
    public ConsumerFactory<String, TourApprovedEvent> activityConsumerFactory() {

        JsonDeserializer<TourApprovedEvent> jsonDeserializer = new JsonDeserializer<>(TourApprovedEvent.class);

        jsonDeserializer.addTrustedPackages("*");
        jsonDeserializer.setUseTypeHeaders(false);
        jsonDeserializer.setRemoveTypeHeaders(true);

        ErrorHandlingDeserializer<TourApprovedEvent> errorHandlingDeserializer = new ErrorHandlingDeserializer<>(
                jsonDeserializer);

        Map<String, Object> props = baseConsumerProps();

        props.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                errorHandlingDeserializer);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                errorHandlingDeserializer);
    }

    // =========================================================
    // Activity Cruise Listener
    // =========================================================

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TourApprovedEvent> activityKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, TourApprovedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(
                activityConsumerFactory());

        return factory;
    }
}