package br.com.freitas.dse.stress.config;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.QueryLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.SchemaAction;

@Configuration
public class DseConfiguration extends AbstractCassandraConfiguration {
    @Value("${spring.data.cassandra.contact-points}")
    private String CONTACT_POINTS;

    @Value("${spring.data.cassandra.keyspace-name}")
    private String KEYSPACE;

    @Value("${spring.data.cassandra.schema-action}")
    private String SCHEMA_ACTION;

    @Override
    protected String getContactPoints() {
        return this.CONTACT_POINTS;
    }

    @Override
    protected String getKeyspaceName() {
        return this.KEYSPACE;
    }

    @Override
    public SchemaAction getSchemaAction() {
        return SchemaAction.valueOf(this.SCHEMA_ACTION);
    }

    @Override
    public String[] getEntityBasePackages() {
        return new String[]{"br.com.freitas.cassandra.stress.domain.model"};
    }

    @Override
    protected boolean getMetricsEnabled() {
        return false;
    }

    @Bean
    public QueryLogger queryLogger(Cluster cluster) {
        QueryLogger queryLogger = QueryLogger.builder().build();

        cluster.register(queryLogger);

        return queryLogger;
    }
}