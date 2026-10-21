package com.project.booking.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

@Configuration
public class BookingCommandSchema {
    @Bean
    public DataSourceInitializer bookingCommandSchemaInitializer(DataSource dataSource,
            @Value("${booking.commands.initialize-schema:true}") boolean enabled) {
        var initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setEnabled(enabled);
        initializer.setDatabasePopulator(new ResourceDatabasePopulator(
                new ClassPathResource("db/migration/V20261015_01__request_dedup.sql"),
                new ClassPathResource("db/migration/V20261016_02__onboard_account.sql"),
                new ClassPathResource("db/migration/V20261017_03__usage_capacity_lock.sql"),
                new ClassPathResource("db/migration/V20261017_04__spending_limit_audit.sql"),
                new ClassPathResource("db/migration/V20261018_05__legacy_ledger_metadata.sql")));
        return initializer;
    }

    @Bean
    @DependsOn("entityManagerFactory")
    public DataSourceInitializer registrationSchemaInitializer(DataSource dataSource,
            @Value("${booking.commands.initialize-schema:true}") boolean enabled) {
        var initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setEnabled(enabled);
        initializer.setDatabasePopulator(new ResourceDatabasePopulator(
                new ClassPathResource("db/migration/V20261019_06__activity_registration.sql"),
                new ClassPathResource("db/migration/V20261019_07__checkout_snapshot.sql"),
                new ClassPathResource("db/migration/V20261021_08__registration_notice_links.sql")));
        return initializer;
    }
}
