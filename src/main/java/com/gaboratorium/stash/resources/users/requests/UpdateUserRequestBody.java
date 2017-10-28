package com.gaboratorium.stash.resources.users.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import java.sql.Timestamp;

@JsonDeserialize
public class UpdateUserRequestBody {
    @JsonProperty @Getter public String userId;
    @JsonProperty @Getter public String userEmail;
    @JsonProperty @Getter public String userPasswordHash;
    @JsonProperty @Getter public String userEmailSecondary;
    @JsonProperty @Getter public String userFirstName;
    @JsonProperty @Getter public String userLastName;
    @JsonProperty @Getter public String userGender;
    @JsonProperty @Getter public String userRole;
    @JsonProperty @Getter public String userAddress;
    @JsonProperty @Getter public String userCity;
    @JsonProperty @Getter public String userZip;
    @JsonProperty @Getter public String userCountry;
    @JsonProperty @Getter public Timestamp userBirthday;
}
