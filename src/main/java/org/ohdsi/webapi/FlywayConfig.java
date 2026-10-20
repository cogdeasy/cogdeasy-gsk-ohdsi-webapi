package org.ohdsi.webapi;

import com.odysseusinc.arachne.commons.config.flyway.ApplicationContextAwareSpringJdbcMigrationResolver;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Flyway 4 is configured here rather than by Spring Boot: Boot 2.7's Flyway auto-configuration
 * requires Flyway 8, so it is excluded in {@link WebApi}.
 */
@Configuration
@ConditionalOnProperty(prefix = "flyway", name = "enabled", matchIfMissing = true)
public class FlywayConfig {
 
    @Bean
    @ConfigurationProperties(prefix="flyway.datasource")
    public DataSource secondaryDataSource() {
        // Boot 1.5 picked tomcat-jdbc first; Boot 2 would pick HikariCP, which is on the classpath for AuthDataSource.
        return DataSourceBuilder.create().type(org.apache.tomcat.jdbc.pool.DataSource.class).build();
    }

    @Bean(initMethod = "migrate", name = "flyway")
    @ConfigurationProperties(prefix="flyway")
    public Flyway flyway() {
      Flyway flyway = new Flyway();
      flyway.setDataSource(secondaryDataSource());
      return flyway;
    }

    @Bean
    public FlywayMigrationInitializer flywayInitializer(ApplicationContext context, Flyway flyway) {

        ApplicationContextAwareSpringJdbcMigrationResolver contextAwareResolver = new ApplicationContextAwareSpringJdbcMigrationResolver(context);
        flyway.setResolvers(contextAwareResolver);

        return new FlywayMigrationInitializer(flyway);
    }

    public static class FlywayMigrationInitializer implements InitializingBean {

        private final Flyway flyway;

        public FlywayMigrationInitializer(Flyway flyway) {
            this.flyway = flyway;
        }

        @Override
        public void afterPropertiesSet() {
            this.flyway.migrate();
        }
    }

}
