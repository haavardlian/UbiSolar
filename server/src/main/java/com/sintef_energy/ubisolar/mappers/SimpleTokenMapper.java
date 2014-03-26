package com.sintef_energy.ubisolar.mappers;

import com.sintef_energy.ubisolar.structs.SimpleToken;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Håvard on 26.03.14.
 */
public class SimpleTokenMapper implements ResultSetMapper<SimpleToken> {
    public SimpleToken map(int index, ResultSet r, StatementContext statementContext) throws SQLException {
        return new SimpleToken(r.getString(("access_token")));
    }
}
