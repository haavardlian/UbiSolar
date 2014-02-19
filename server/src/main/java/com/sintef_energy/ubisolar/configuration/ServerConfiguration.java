package com.sintef_energy.ubisolar.configuration;

/**
 * Created by thb on 12.02.14.
 */
import com.yammer.dropwizard.config.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.db.DatabaseConfiguration;
import org.hibernate.validator.constraints.NotEmpty;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class ServerConfiguration extends Configuration {
    @Valid
    @NotNull
    @JsonProperty
    private DatabaseConfiguration database = new DatabaseConfiguration();

    public ServerConfiguration() {
        database.setUrl("jdbc:mysql://mysql.stud.ntnu.no/haavarhl_it2901");
        database.setUser("haavarhl_it2901");
        database.setPassword("julebrus");
        database.setDriverClass("com.mysql.jdbc.Driver");
    }

    public DatabaseConfiguration getDatabaseConfiguration() {
        return database;
    }

}
