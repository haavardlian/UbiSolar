package com.sintef_energy.ubisolar;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

/**
 * Created by haavard on 2/12/14.
 */
public interface ServerDAO {
    @SqlUpdate("CREATE TABLE server (id int primary key, name varchar(100))")
    void createTable();

    @SqlUpdate("INSERT INTO server (id, name) VALUES (:id, :name)")
    void insert(@Bind("id") int id, @Bind("name") String name);

    @SqlQuery("SELECT name FROM server WHERE id = :id")
    String findNameById(@Bind("id") int id);
}
