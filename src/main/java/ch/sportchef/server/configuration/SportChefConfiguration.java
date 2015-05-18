package ch.sportchef.server.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class SportChefConfiguration extends Configuration {

    @Valid
    @NotNull
    @JsonProperty("database")
    private SportChefDataSourceFactory database = new SportChefDataSourceFactory();

    @Valid
    @NotNull
    @JsonProperty("healthCheck")
    private HealthCheckConfiguration healthCheck = new HealthCheckConfiguration();

    @Valid
    @NotEmpty
    @JsonProperty("tokenSecret")
    private String tokenSecret = UUID.randomUUID().toString();

    public SportChefDataSourceFactory getDataSourceFactory() {
        return database;
    }

    public HealthCheckConfiguration getHealthCheckConfiguration() {
        return healthCheck;
    }

    public byte[] getTokenSecret() throws UnsupportedEncodingException {
        return tokenSecret.getBytes(StandardCharsets.UTF_8);
    }
}
