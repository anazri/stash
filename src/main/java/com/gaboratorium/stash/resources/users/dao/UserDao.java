package com.gaboratorium.stash.resources.users.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import java.sql.Timestamp;

public interface UserDao {
    @SqlQuery("select * from users where id = :userId;")
    @Mapper(UserMapper.class)
    User findById(
        @Bind("userId") String userId
    );

    @SqlQuery("select * from users where user_email = :userEmail;")
    @Mapper(UserMapper.class)
    User findByUserEmail(
        @Bind("userEmail") String userEmail
    );

    @SqlQuery("insert into users values (" +
        ":userId," +
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
}
