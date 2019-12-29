package com.myth;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.myth.context.ServerContext;
import com.myth.db.PersistInitialiser;
import com.myth.resources.UserResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import ru.vyarus.dropwizard.guice.GuiceBundle;

public class MobileServerApplication extends Application<MobileServerConfiguration> {

    public static void main(final String[] args) throws Exception {
        new MobileServerApplication().run(args);
    }

    @Override
    public String getName() {
        return "MobileServer";
    }

    @Override
    public void initialize(final Bootstrap<MobileServerConfiguration> bootstrap) {
        bootstrap.addBundle(new SwaggerBundle<MobileServerConfiguration>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(MobileServerConfiguration configuration) {
                return configuration.getSwaggerBundleConfiguration();
            }
        });
    }

    @Override
    public void run(final MobileServerConfiguration configuration,
                    final Environment environment) {
        FluentConfiguration flywayConfiguration = Flyway.configure();
        flywayConfiguration.dataSource(configuration.getDataSourceFactory().getUrl(),
                                       configuration.getDataSourceFactory().getUser(),
                                       configuration.getDataSourceFactory().getPassword());
        Flyway flyway = new Flyway(flywayConfiguration);
        flyway.migrate();

        final Injector injector = Guice.createInjector(new ServerGuiceModule(configuration, environment));
        injector.getInstance(PersistInitialiser.class);

        this.initResources(environment, injector);

        ServerContext.init(configuration.getServerContextConfig());
    }

    private void initResources(final Environment environment, final Injector injector) {
        environment.jersey().register(injector.getInstance(UserResource.class));
    }
}
