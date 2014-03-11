package com.sintef_energy.ubisolar.mappers;

import com.sintef_energy.ubisolar.structs.Tip;
import com.sintef_energy.ubisolar.structs.TipRating;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Håvard on 05.03.14.
 */
public class TipRatingMapper implements ResultSetMapper<TipRating> {
    public TipRating map(int index, ResultSet r, StatementContext statementContext) throws SQLException {
        return new TipRating(r.getInt("id"), r.getInt("tip_id"), r.getShort("rating"), r.getInt("user_id"));
    }
}
