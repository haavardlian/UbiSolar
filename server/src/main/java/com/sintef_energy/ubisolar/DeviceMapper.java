package com.sintef_energy.ubisolar;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by haavard on 2/19/14.
 */
public class DeviceMapper implements ResultSetMapper<Device> {
    @Override
    public Device map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new Device(r.getInt("user_id"), r.getString("name"), r.getString("description"), r.getInt("device_id"));
    }
}
