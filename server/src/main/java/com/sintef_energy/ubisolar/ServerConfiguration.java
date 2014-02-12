package com.sintef_energy.ubisolar;

/**
 * Created by thb on 12.02.14.
 */
import com.yammer.dropwizard.config.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class ServerConfiguration extends Configuration {

    @NotEmpty
    @JsonProperty
    private String defaultName = "Stranger";

    public String getDefaultName() {
        return defaultName;
    }
}
