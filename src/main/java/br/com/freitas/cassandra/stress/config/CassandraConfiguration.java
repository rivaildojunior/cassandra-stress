package br.com.freitas.cassandra.stress.config;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.QueryLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.SchemaAction;

@Configuration
public class CassandraConfiguration extends AbstractCassandraConfiguration {
    @Value("${spring.data.cassandra.contact-points}")
    private String contactPoints;

    @Value("${spring.data.cassandra.keyspace-name}")
    private String keySpace;

    @Value("${spring.data.cassandra.schema-action}")
    private String schemaAction;

    @Override
    protected String getContactPoints() {
        return this.contactPoints;
    }

    @Override
    protected String getKeyspaceName() {
        return this.keySpace;
    }

    @Override
    public SchemaAction getSchemaAction() {
        return SchemaAction.valueOf(this.schemaAction);
    }

    @Override
    public String[] getEntityBasePackages() {
        return new String[]{"br.com.freitas.pocitau.demo.domain.model"};
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