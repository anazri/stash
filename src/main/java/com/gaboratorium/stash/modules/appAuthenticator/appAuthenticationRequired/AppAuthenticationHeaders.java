package com.gaboratorium.stash.modules.appAuthenticator.appAuthenticationRequired;

import lombok.Data;

@Data
public class AppAuthenticationHeaders {

    final public static String APP_TOKEN = "X-Auth-App-Token";
    final public static String APP_ID = "X-Auth-App-Id";
    final public static String APP_SECRET = "X-Auth-App-Secret";
}
