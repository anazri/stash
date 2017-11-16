package com.gaboratorium.stash.resources.requestLogs.dao;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class RequestLogMapper implements ResultSetMapper<RequestLog> {
    @Override
    public RequestLog map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new RequestLog(
            r.getString("id"),
            r.getString("request_type"),
            r.getString("request_url"),
            r.getBoolean("is_successful")

        );
    }
}
