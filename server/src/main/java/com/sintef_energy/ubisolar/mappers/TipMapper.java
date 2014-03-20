package com.sintef_energy.ubisolar.mappers;

import com.sintef_energy.ubisolar.structs.Tip;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Håvard on 05.03.14.
 */
public class TipMapper implements ResultSetMapper<Tip> {
    public Tip map(int index, ResultSet r, StatementContext statementContext) throws SQLException {
        return new Tip(r.getInt(("id")), r.getString(("name")), r.getString("description"), r.getInt("n_ratings"), r.getInt("average_rating"));
    }
}
