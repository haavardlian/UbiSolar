package com.sintef_energy.ubisolar.mappers;

import com.sintef_energy.ubisolar.structs.FacebookUser;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Lars Erik on 03.05.14.
 */
public class FacebookUserMapper implements ResultSetMapper<FacebookUser> {

    @Override
    public FacebookUser map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new FacebookUser(r.getLong("user_id"), r.getString("name"));
    }
}
