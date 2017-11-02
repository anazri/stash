package com.gaboratorium.stash.resources.files.dao;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FileMapper implements ResultSetMapper<File> {
    @Override
    public File map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new File(
            r.getString("id"),
            r.getString("file_url"),
            r.getString("file_owner_id")
        );
    }
}
