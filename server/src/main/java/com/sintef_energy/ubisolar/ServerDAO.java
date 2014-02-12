package com.sintef_energy.ubisolar;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

/**
 * Created by haavard on 2/12/14.
 */
public interface ServerDAO {
    @SqlUpdate("create table server (id int primary key, name varchar(100))")
    void createTable();

    @SqlUpdate("insert into server (id, name) values (:id, :name)")
    void insert(@Bind("id") int id, @Bind("name") String name);

    @SqlQuery("select name from server where id = :id")
    String findNameById(@Bind("id") int id);
}
