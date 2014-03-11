package com.sintef_energy.ubisolar.mappers;

import com.sintef_energy.ubisolar.structs.TotalUsage;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by haavard on 2/19/14.
 */
public class TotalUsageMapper implements ResultSetMapper<TotalUsage>
{
    public TotalUsage map(int index, ResultSet r, StatementContext statementContext) throws SQLException {
        return new TotalUsage(r.getLong("id"), r.getLong("user_id"), r.getTimestamp("timestamp"), r.getDouble("power_usage"));
    }
}
