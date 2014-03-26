package com.sintef_energy.ubisolar.configuration;

/**
 * Created by Håvard on 12.03.14.
 */
public class DatabaseConfiguration extends com.yammer.dropwizard.db.DatabaseConfiguration {

    public static enum DatabaseType {MYSQL, POSTGRES};

    public DatabaseConfiguration() {
        this.setUrl("jdbc:mysql://mysql.stud.ntnu.no/haavarhl_it2901");
        this.setUser("haavarhl_it2901");
        this.setPassword("julebrus");
        this.setDriverClass("com.mysql.jdbc.Driver");
    }

    public DatabaseConfiguration(String url, String user, String password, DatabaseType databaseType) {
        this.setUrl(url);
        this.setUser(user);
        this.setPassword(password);
        switch(databaseType) {
            case MYSQL:
                this.setDriverClass("com.mysql.jdbc.Driver");
                break;
            case POSTGRES:
                this.setDriverClass("org.postgresql.Driver");
                break;
            default:
                this.setDriverClass("com.mysql.jdbc.Driver");
                break;
        }
    }
}
