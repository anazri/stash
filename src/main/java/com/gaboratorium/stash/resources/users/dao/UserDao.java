package com.gaboratorium.stash.resources.users.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import java.sql.Timestamp;

public interface UserDao {

    // Get by ID
    @SqlQuery("select * from users where id = :userId AND app_id = :appId;")
    @Mapper(UserMapper.class)
    User findById(
        @Bind("userId") String userId,
        @Bind("appId") String appId
    );

    // Get by e-mail
    @SqlQuery("select * from users where user_email = :userEmail AND app_id = :appId;")
    @Mapper(UserMapper.class)
    User findByUserEmail(
        @Bind("userEmail") String userEmail,
        @Bind("appId") String appId
    );

    // Get by credentials
    @SqlQuery("select * from users where " +
        "id = :userId AND " +
        "user_password_hash = :userPasswordHash AND " +
        "app_id = :appId;")
    @Mapper(UserMapper.class)
    User findByUserCredentials(
        @Bind("userId") String userId,
        @Bind("userPasswordHash") String userPasswordHash,
        @Bind("appId") String appId
    );

    // Insert
    @SqlQuery("insert into users values (" +
        ":userId," +
        " :appId," +
        " :userEmail," +
        " :userPasswordHash," +
        " :userEmailSecondary," +
        " :userFirstName," +
        " :userLastName," +
        " :userGender," +
        " :userRole," +
        " :userAddress," +
        " :userCity," +
        " :userZip," +
        " :userCountry," +
        " :userBirthday)" +
        " returning *;")
    @Mapper(UserMapper.class)
    User insert(
        @Bind("userId") String userId,
        @Bind("appId") String appId,
        @Bind("userEmail") String userEmail,
        @Bind("userPasswordHash") String userPasswordHash,
        @Bind("userEmailSecondary") String userEmailSecondary,
        @Bind("userFirstName") String userFirstName,
        @Bind("userLastName") String userLastName,
        @Bind("userGender") String userGender,
        @Bind("userRole") String userRole,
        @Bind("userAddress") String userAddress,
        @Bind("userCity") String userCity,
        @Bind("userZip") String userZip,
        @Bind("userCountry") String userCountry,
        @Bind("userBirthday") Timestamp userBirthday
    );

    // Update
    @SqlQuery("update users set " +
        "id = :userNewId, " +
        "user_email = :userEmail, " +
        "user_password_hash = :userPasswordHash, " +
        "user_email_secondary = :userEmailSecondary, " +
        "user_first_name = :userFirstName, " +
        "user_last_name = :userLastName, " +
        "user_gender = :userGender, " +
        "user_role = :userRole, " +
        "user_address = :userAddress, " +
        "user_city = :userCity, " +
        "user_zip = :userZip, " +
        "user_country = :userCountry, " +
        "user_birthday = :userBirthday " +
        "where id = :userId " +
        "returning *;")
    @Mapper(UserMapper.class)
    User update(
        @Bind("userId") String userId,
        @Bind("userNewId") String userNewId,
        @Bind("appId") String appId,
        @Bind("userEmail") String userEmail,
        @Bind("userPasswordHash") String userPasswordHash,
        @Bind("userEmailSecondary") String userEmailSecondary,
        @Bind("userFirstName") String userFirstName,
        @Bind("userLastName") String userLastName,
        @Bind("userGender") String userGender,
        @Bind("userRole") String userRole,
        @Bind("userAddress") String userAddress,
        @Bind("userCity") String userCity,
        @Bind("userZip") String userZip,
        @Bind("userCountry") String userCountry,
        @Bind("userBirthday") Timestamp userBirthday
    );

    @SqlUpdate("delete from users where id = :userId and app_id = :appId;")
    void delete(
        @Bind("userId") String userId,
        @Bind("appId") String appId
    );
}
