package com.sintef_energy.ubisolar;

/**
 * Created by thb on 12.02.14.
 */
import com.yammer.dropwizard.config.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.db.DatabaseConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class ServerConfiguration extends Configuration {

    @Valid
    @NotNull
    @JsonProperty("database")
    private DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();

    public DatabaseConfiguration getDatabaseConfiguration() {
        return databaseConfiguration;
    }

    public void setDatabaseConfiguration(DatabaseConfiguration databaseConfiguration) {
        this.databaseConfiguration = databaseConfiguration;
    }


}
