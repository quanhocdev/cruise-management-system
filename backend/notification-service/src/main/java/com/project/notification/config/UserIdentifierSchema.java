package com.project.notification.config;

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
}
