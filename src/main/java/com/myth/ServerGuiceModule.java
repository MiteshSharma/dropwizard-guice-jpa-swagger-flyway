package com.myth;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import io.dropwizard.db.DataSourceFactory;
import com.google.inject.persist.jpa.JpaPersistModule;
import io.dropwizard.setup.Environment;

import javax.inject.Named;
import java.util.Properties;

public class ServerGuiceModule extends AbstractModule {
    private MobileServerConfiguration configuration;
    private Environment environment;

    public ServerGuiceModule(final MobileServerConfiguration configuration, final Environment environment) {
        this.configuration = configuration;
        this.environment = environment;
    }

    @Override
    protected void configure() {
        install(jpaModule(configuration.getDataSourceFactory()));
    }

    private Module jpaModule(DataSourceFactory dataSourceFactory ) {
        final Properties properties = new Properties();
        properties.put("javax.persistence.jdbc.driver", dataSourceFactory.getDriverClass());
        properties.put("javax.persistence.jdbc.url", dataSourceFactory.getUrl());
        properties.put("javax.persistence.jdbc.user", dataSourceFactory.getUser());
        properties.put("javax.persistence.jdbc.password", dataSourceFactory.getPassword());

        final JpaPersistModule jpaModule = new JpaPersistModule("DefaultUnit");
        jpaModule.properties(properties);

        return jpaModule;
    }
}