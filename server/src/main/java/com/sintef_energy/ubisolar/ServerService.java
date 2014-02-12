package com.sintef_energy.ubisolar;

/**
 * Created by thb on 12.02.14.
 */
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

public class ServerService extends Service<ServerConfiguration> {
    public static void main(String[] args) throws Exception {
        new ServerService().run(args);
    }

    @Override
    public void initialize(Bootstrap<ServerConfiguration> bootstrap) {
        bootstrap.setName("hello-world");
    }

    @Override
    public void run(ServerConfiguration configuration,Environment environment) {
        final String defaultName = configuration.getDefaultName();
        environment.addResource(new GeneralResource(defaultName));
    }
}