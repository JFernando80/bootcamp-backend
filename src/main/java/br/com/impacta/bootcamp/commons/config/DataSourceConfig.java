package br.com.impacta.bootcamp.commons.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Value("${dbhost}")
    private String dbHost;

    @Value("${dbuser}")
    private String dbuser;

    @Value("${dbpass}")
    private String dbpass;

    @Bean(name = "ireport")
    public DataSource postgresDataSource()
    {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url(dbHost);
        dataSourceBuilder.username(dbuser);
        dataSourceBuilder.password(dbpass);
        return dataSourceBuilder.build();
    }
}
