/*
 * Licensed to UbiCollab.org under one or more contributor
 * license agreements.  See the NOTICE file distributed
 * with this work for additional information regarding
 * copyright ownership. UbiCollab.org licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.sintef_energy.ubisolar;

/**
 * Created by thb on 12.02.14.
 *
 * The main class for the server
 */

import com.sintef_energy.ubisolar.resources.*;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.db.DatabaseConfiguration;
import com.yammer.dropwizard.jdbi.DBIFactory;
import com.yammer.dropwizard.migrations.MigrationsBundle;
import org.skife.jdbi.v2.DBI;

public class ServerService extends Service<ServerConfiguration> {

    public static void main(String[] args) throws Exception {
        new ServerService().run(args);
    }



    @Override
    public void initialize(Bootstrap<ServerConfiguration> bootstrap) {
        bootstrap.setName("sintef_energy_server");
        bootstrap.addBundle(new AssetsBundle());
        bootstrap.addBundle(new MigrationsBundle<ServerConfiguration>() {
            @Override
            public DatabaseConfiguration getDatabaseConfiguration(ServerConfiguration configuration) {
                return configuration.getDatabaseConfiguration();
            }
        });
    }

    @Override
    public void run(ServerConfiguration configuration,Environment environment) throws ClassNotFoundException {
        final DBIFactory factory = new DBIFactory();
        DBI jdbi = factory.build(environment, configuration.getDatabaseConfiguration(), "mysql");
        final ServerDAO dao = jdbi.onDemand(ServerDAO.class);
        //environment.addResource(new DeviceResource(dao));
        //environment.addResource(new DevicesResource(dao));
        //environment.addResource(new TotalUsageResource(dao));
        //environment.addResource(new DeviceUsageResource(dao));
        environment.addResource(new SyncResource(dao));
        environment.addResource(new DataGeneratorResource(dao));
        environment.addResource(new FriendsResource(dao));
        environment.addResource(new TipsResource(dao));
        environment.addResource(new TimeResource());
    }
}