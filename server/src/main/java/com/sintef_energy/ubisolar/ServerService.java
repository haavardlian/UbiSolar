package com.sintef_energy.ubisolar;

/**
 * Created by thb on 12.02.14.
 */
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.jdbi.DBIFactory;
import com.yammer.dropwizard.jdbi.bundles.DBIExceptionsBundle;
import org.skife.jdbi.v2.DBI;

public class ServerService extends Service<ServerConfiguration> {
    public static void main(String[] args) throws Exception {
        new ServerService().run(args);
    }

    @Override
    public void initialize(Bootstrap<ServerConfiguration> bootstrap) {
        bootstrap.setName("sintef_energy_server");
    }

    @Override
    public void run(ServerConfiguration configuration,Environment environment) throws ClassNotFoundException {
        final DBIFactory factory = new DBIFactory();
        DBI jdbi = factory.build(environment, configuration.getDatabaseConfiguration(), "mysql");
        final ServerDAO dao = jdbi.onDemand(ServerDAO.class);
        environment.addResource(new GeneralResource(dao));
    }
}