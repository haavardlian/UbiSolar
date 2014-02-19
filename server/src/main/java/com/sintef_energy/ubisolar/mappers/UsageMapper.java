package com.sintef_energy.ubisolar.mappers;

import com.sintef_energy.ubisolar.DeviceUsage;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by haavard on 2/19/14.
 */
public class UsageMapper implements ResultSetMapper<DeviceUsage>
{
    public DeviceUsage map(int index, ResultSet r, StatementContext ctx) throws SQLException
    {
        return new DeviceUsage(r.getInt("device_id"), r.getDate("datetime"), r.getDouble("power_usage"));
    }
}
