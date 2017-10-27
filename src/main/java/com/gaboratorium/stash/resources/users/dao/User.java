package com.gaboratorium.stash.resources.users.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.sql.Timestamp;

@AllArgsConstructor
public class User {

    @Getter final private String userId;
    @Getter final private String userEmail;
    @Getter final private String userPasswordHash;
    @Getter final private String userEmailSecondary;
    @Getter final private String userFirstName;
    @Getter final private String userLastName;
    @Getter final private String userGender;
    @Getter final private String userRole;
    @Getter final private String userAddress;
    @Getter final private String userCity;
    @Getter final private String userZip;
    @Getter final private String userCountry;
    @Getter final private Timestamp userBirthday;
    @Getter final private Timestamp userRegisteredAt;
}
