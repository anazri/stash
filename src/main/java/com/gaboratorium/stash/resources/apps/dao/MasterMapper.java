package com.gaboratorium.stash.resources.apps.dao;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MasterMapper implements ResultSetMapper<Master> {
    @Override
    public Master map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new Master(
            r.getString("id"),
            r.getString("app_id"),
            r.getString("master_email"),
            r.getString("master_password_hash")
        );
    }
}
