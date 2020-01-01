package com.myth;

import com.myth.context.ServerContextConfig;
import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.db.DataSourceFactory;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.hibernate.validator.constraints.*;

import javax.validation.Valid;
import javax.validation.constraints.*;
import javax.validation.constraints.NotEmpty;
import java.io.UnsupportedEncodingException;

public class MobileServerConfiguration extends Configuration {

    @Valid
    @NotNull
    @JsonProperty("database")
    private DataSourceFactory database = new DataSourceFactory();

    @JsonProperty("swagger")
    private SwaggerBundleConfiguration swaggerBundleConfiguration;

    @JsonProperty("serverContextConfig")
    private ServerContextConfig serverContextConfig;

    @NotEmpty
    @JsonProperty("jwtTokenSecret")
    private String jwtTokenSecret = "JWTTokenMessage";

    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

    public SwaggerBundleConfiguration getSwaggerBundleConfiguration() {
        return swaggerBundleConfiguration;
    }

    public ServerContextConfig getServerContextConfig() {
        return serverContextConfig;
    }

    public byte[] getJwtTokenSecret() {
        byte[] tokenSecret;
        try {
            tokenSecret =  jwtTokenSecret.getBytes("UTF-8");
        }catch(UnsupportedEncodingException e) {
            tokenSecret = null;
        };
        return tokenSecret;
    }
}
