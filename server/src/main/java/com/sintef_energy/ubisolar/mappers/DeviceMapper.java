package com.sintef_energy.ubisolar.mappers;

import com.sintef_energy.ubisolar.structs.Device;
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
        return new Device(r.getLong("id"), r.getLong("user_id"), r.getString("name"), r.getString("description"));
    }
}
