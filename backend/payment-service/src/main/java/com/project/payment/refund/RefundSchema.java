package com.project.payment.refund;

import javax.sql.DataSource;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.*;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class RefundSchema {
    @Bean
    @DependsOn("entityManagerFactory")
    DataSourceInitializer paymentRefundSchemaInitializer(DataSource dataSource) {
        var initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(new ResourceDatabasePopulator(
                new ClassPathResource("db/migration/V20261019_01__payment_refunds.sql"),
                new ClassPathResource("db/migration/V20261019_02__bill_payment_requests.sql")));
        return initializer;
    }
}
