package com.gaboratorium.stash.resources.users.dao;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements ResultSetMapper<User>{
    @Override
    public User map(int index, ResultSet resultSet, StatementContext ctx) throws SQLException {
        return new User(
            resultSet.getString("id"),
            resultSet.getString("app_id"),
            resultSet.getString("user_email"),
            resultSet.getString("user_password_hash"),
            resultSet.getString("user_email_secondary"),
            resultSet.getString("user_first_name"),
            resultSet.getString("user_last_name"),
            resultSet.getString("user_gender"),
            resultSet.getString("user_role"),
            resultSet.getString("user_address"),
            resultSet.getString("user_city"),
            resultSet.getString("user_zip"),
            resultSet.getString("user_country"),
            resultSet.getTimestamp("user_birthday"),
            resultSet.getTimestamp("created_at")
        );
    }
}
