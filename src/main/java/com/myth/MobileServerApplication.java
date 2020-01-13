package com.myth;

import com.bendb.dropwizard.redis.JedisBundle;
import com.bendb.dropwizard.redis.JedisFactory;
import com.github.toastshaman.dropwizard.auth.jwt.JwtAuthFilter;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.myth.auth.UserAuthenticator;
import com.myth.auth.UserAuthorizer;
import com.myth.context.ServerContext;
import com.myth.db.PersistInitialiser;
import com.myth.filters.RequestIdFilter;
import com.myth.health.ServerHealthCheck;
import com.myth.models.User;
import com.myth.resources.IndexResource;
import com.myth.resources.UserResource;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.HmacKey;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.EnumSet;

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

        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );

        bootstrap.addBundle(new SwaggerBundle<MobileServerConfiguration>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(MobileServerConfiguration configuration) {
                return configuration.getSwaggerBundleConfiguration();
            }
        });

        bootstrap.addBundle(new JedisBundle<MobileServerConfiguration>() {
            public JedisFactory getJedisFactory(
                    MobileServerConfiguration configuration) {
                return configuration.getRedis();
            }
        });
    }

    @Override
    public void run(final MobileServerConfiguration configuration,
                    final Environment environment) {
        this.enableCors(environment);
        environment.healthChecks().register("App Health Check", new ServerHealthCheck());

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

        this.setupAuth(configuration, environment, injector);

        environment.jersey().register(RequestIdFilter.class);
    }

    private void enableCors(final Environment environment) {
        // Enabling CORS requests
        final FilterRegistration.Dynamic filterRegistration = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
        filterRegistration.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,OPTIONS");
        filterRegistration.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        filterRegistration.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");

        filterRegistration.setInitParameter("allowedHeaders", "Content-Type, Authorization, X-Requested-With, Content-Length, Accept, Origin");
        filterRegistration.setInitParameter("allowCredentials", "true");

        filterRegistration.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
    }

    private void setupAuth(final MobileServerConfiguration configuration,
                           final Environment environment, final Injector injector) {
        final byte[] key = configuration.getJwtTokenSecret();

        final JwtConsumer consumer = new JwtConsumerBuilder().setAllowedClockSkewInSeconds(30)
                .setRequireExpirationTime()
                .setRequireSubject()
                .setVerificationKey(new HmacKey(key))
                .setRelaxVerificationKeyValidation()
                .build();

        UserAuthenticator userAuthenticator = injector.getInstance(UserAuthenticator.class);
        UserAuthorizer userAuthorizer = injector.getInstance(UserAuthorizer.class);
        environment.jersey().register(new AuthDynamicFeature(new JwtAuthFilter.Builder<User>().setJwtConsumer(consumer)
                .setRealm("realm")
                .setPrefix("Bearer")
                .setAuthenticator(userAuthenticator)
                .setAuthorizer(userAuthorizer)
                .buildAuthFilter()));

        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(Principal.class));
        environment.jersey().register(RolesAllowedDynamicFeature.class);
    }

    private void initResources(final Environment environment, final Injector injector) {
        environment.jersey().register(injector.getInstance(UserResource.class));
        environment.jersey().register(injector.getInstance(IndexResource.class));
    }
}
