package com.sintef_energy.ubisolar;

/**
 * Created by thb on 12.02.14.
 */
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

public class HelloService extends Service<HelloConfiguration> {
    public static void main(String[] args) throws Exception {
        new HelloService().run(args);
    }

    @Override
    public void initialize(Bootstrap<HelloConfiguration> bootstrap) {
        bootstrap.setName("hello-world");
    }

    @Override
    public void run(HelloConfiguration configuration,Environment environment) {
        final String template = configuration.getTemplate();
        final String defaultName = configuration.getDefaultName();
        environment.addResource(new Resource(template, defaultName));
        environment.addHealthCheck(new TemplateHealthCheck(template));
    }
}