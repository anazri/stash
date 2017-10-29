package com.gaboratorium.stash.modules.userAuthenticator.userAuthenticationRequired;

import lombok.Data;

@Data
public class UserAuthenticationHeaders {

    final public static String USER_TOKEN = "X-Auth-User-Token";
    final public static String USER_ID = "X-Auth-User-Id";
}
