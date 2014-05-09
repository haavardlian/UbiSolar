package com.sintef_energy.ubisolar.configuration;

/**
 * Created by thb on 12.02.14.
 */
import com.yammer.dropwizard.config.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import com.sintef_energy.ubisolar.configuration.DatabaseConfiguration.*;

public class ServerConfiguration extends Configuration {
    @Valid
    @NotNull
    @JsonProperty
    private DatabaseConfiguration database;

    public ServerConfiguration() {
        database = new DatabaseConfiguration("jdbc:mysql://188.226.188.11/haavarhl_it2901", "haavarhl_it2901", "julebrus", DatabaseType.MYSQL);
    }

    public DatabaseConfiguration getDatabaseConfiguration() {
        return database;
    }

}
