package com.gaboratorium.stash.resources.users.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.hibernate.validator.constraints.NotEmpty;
import java.sql.Timestamp;

@JsonDeserialize
public class RegisterUserRequestBody {
    @JsonProperty @NotEmpty public String userId;
    @JsonProperty @NotEmpty public String appId;
    @JsonProperty @NotEmpty public String userEmail;
    @JsonProperty @NotEmpty public String userPasswordHash;
    @JsonProperty public String userEmailSecondary;
    @JsonProperty public String userFirstName;
    @JsonProperty public String userLastName;
    @JsonProperty public String userGender;
    @JsonProperty public String userRole;
    @JsonProperty public String userAddress;
    @JsonProperty public String userCity;
    @JsonProperty public String userZip;
    @JsonProperty public String userCountry;
    @JsonProperty public Timestamp userBirthday;
}
