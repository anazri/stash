package com.gaboratorium.stash.resources.apps;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AppMapper implements ResultSetMapper<App> {
    public App map(int index, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        return new App(
            resultSet.getString("id"),
            resultSet.getString("app_name"),
            resultSet.getString("app_description"),
            resultSet.getString("app_secret"),
            resultSet.getString("master_email")
        );
    }
}
