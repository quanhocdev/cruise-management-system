package com.project.tour.config;

import com.project.common.event.ProductTourConfiguredEvent;
import com.project.common.event.ServiceTourConfiguredEvent;

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
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);

        return props;
    }

    // =========================================================
    // PRODUCT TOUR CONFIGURED
    // =========================================================

    @Bean
    public ConsumerFactory<String, ProductTourConfiguredEvent> productTourConfiguredConsumerFactory() {

        JsonDeserializer<ProductTourConfiguredEvent> jsonDeserializer = new JsonDeserializer<>(
                ProductTourConfiguredEvent.class);

        jsonDeserializer.addTrustedPackages("*");
        jsonDeserializer.setUseTypeHeaders(false);
        jsonDeserializer.setRemoveTypeHeaders(true);

        ErrorHandlingDeserializer<ProductTourConfiguredEvent> errorHandlingDeserializer = new ErrorHandlingDeserializer<>(
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

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProductTourConfiguredEvent> productTourConfiguredKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, ProductTourConfiguredEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(
                productTourConfiguredConsumerFactory());

        return factory;
    }

    // =========================================================
    // SERVICE TOUR CONFIGURED
    // =========================================================

    @Bean
    public ConsumerFactory<String, ServiceTourConfiguredEvent> serviceTourConfiguredConsumerFactory() {

        JsonDeserializer<ServiceTourConfiguredEvent> jsonDeserializer = new JsonDeserializer<>(
                ServiceTourConfiguredEvent.class);

        jsonDeserializer.addTrustedPackages("*");
        jsonDeserializer.setUseTypeHeaders(false);
        jsonDeserializer.setRemoveTypeHeaders(true);

        ErrorHandlingDeserializer<ServiceTourConfiguredEvent> errorHandlingDeserializer = new ErrorHandlingDeserializer<>(
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

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ServiceTourConfiguredEvent> serviceTourConfiguredKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, ServiceTourConfiguredEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(
                serviceTourConfiguredConsumerFactory());

        return factory;
    }
}