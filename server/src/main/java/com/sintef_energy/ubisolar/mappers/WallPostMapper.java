package com.sintef_energy.ubisolar.mappers;

import com.sintef_energy.ubisolar.structs.Device;
import com.sintef_energy.ubisolar.structs.WallPost;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by haavard on 2/19/14.
 */
public class WallPostMapper implements ResultSetMapper<WallPost> {
    @Override
    public WallPost map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new WallPost(r.getInt("id"), r.getLong("user_id"), r.getString("message"), r.getLong("timestamp"));
    }
}