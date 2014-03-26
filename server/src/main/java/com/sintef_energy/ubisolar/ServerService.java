package com.sintef_energy.ubisolar;

/**
 * Created by thb on 12.02.14.
 */
import com.sintef_energy.ubisolar.auth.Auth;
import com.sintef_energy.ubisolar.auth.User;
import com.sintef_energy.ubisolar.configuration.ServerConfiguration;
import com.sintef_energy.ubisolar.resources.*;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.auth.oauth.OAuthProvider;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.jdbi.DBIFactory;
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
        environment.addResource(new DeviceResource(dao));
        environment.addResource(new DevicesResource(dao));
        environment.addResource(new TotalUsageResource(dao));
        environment.addResource(new DeviceUsageResource(dao));
        environment.addResource(new TipsResource(dao));
        environment.addProvider(new OAuthProvider<User>(new Auth(), "SECRET"));
    }
}