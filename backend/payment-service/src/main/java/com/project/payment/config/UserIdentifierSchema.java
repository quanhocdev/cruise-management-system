package com.project.payment.config;

import java.util.ArrayList;
import java.util.Arrays;
import javax.sql.DataSource;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import java.sql.SQLException;
import java.nio.charset.StandardCharsets;

/** Validates legacy account ownership before Hibernate can change user ID columns. */
@Configuration
public class UserIdentifierSchema {
    @Bean
    public DataSourceInitializer userIdentifierSchemaInitializer(DataSource dataSource) {
        var initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(connection -> {
            boolean autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try (var input = new ClassPathResource("db/upgrade/user_identifiers.sql").getInputStream();
                 var statement = connection.createStatement()) {
                statement.execute(new String(input.readAllBytes(), StandardCharsets.UTF_8));
                upgradePaymentRequestHashes(connection);
                connection.commit();
            } catch (Exception e) {
                connection.rollback();
                throw new SQLException("Cannot migrate user identifiers; legacy ownership must be mapped first", e);
            } finally {
                connection.setAutoCommit(autoCommit);
            }
        });
        return initializer;
    }

    @Bean
    static BeanFactoryPostProcessor userIdentifierSchemaDependency() {
        return factory -> {
            if (factory.containsBeanDefinition("entityManagerFactory")) {
                var definition = factory.getBeanDefinition("entityManagerFactory");
                var dependencies = new ArrayList<String>();
                if (definition.getDependsOn() != null) dependencies.addAll(Arrays.asList(definition.getDependsOn()));
                dependencies.add("userIdentifierSchemaInitializer");
                definition.setDependsOn(dependencies.toArray(String[]::new));
            }
        };
    }

    private static void upgradePaymentRequestHashes(java.sql.Connection connection) throws Exception {
        try (var statement = connection.createStatement()) {
            try (var table = statement.executeQuery("select to_regclass('payment.bill_payment_requests')")) {
                table.next();
                if (table.getString(1) == null) return;
            }
            statement.execute("alter table payment.bill_payment_requests add column if not exists legacy_payload_hash varchar(64)");
            var json = new com.fasterxml.jackson.databind.ObjectMapper().findAndRegisterModules();
            try (var rows = statement.executeQuery("select client_request_id,payload_hash,response_body from payment.bill_payment_requests where legacy_response_body is not null and legacy_payload_hash is null");
                 var update = connection.prepareStatement("update payment.bill_payment_requests set legacy_payload_hash=?,payload_hash=? where client_request_id=?")) {
                while (rows.next()) {
                    var body = json.readTree(rows.getString("response_body"));
                    var payload = new java.util.TreeMap<String,Object>();
                    payload.put("amount", new java.math.BigDecimal(body.get("amount").asText()).stripTrailingZeros());
                    payload.put("billId", body.get("billId").longValue());
                    payload.put("payerId", java.util.UUID.fromString(body.get("payerId").asText()));
                    String hash = java.util.HexFormat.of().formatHex(java.security.MessageDigest.getInstance("SHA-256")
                        .digest(json.writeValueAsString(payload).getBytes(StandardCharsets.UTF_8)));
                    update.setString(1,rows.getString("payload_hash"));
                    update.setString(2,hash);
                    update.setObject(3,rows.getObject("client_request_id"));
                    update.executeUpdate();
                }
            }
        }
    }
}
