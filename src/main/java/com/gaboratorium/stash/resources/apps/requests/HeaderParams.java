package com.gaboratorium.stash.resources.apps.requests;

import lombok.Data;

@Data
public class HeaderParams {
    final public static String TOKEN = "X-Auth-Token";
    final public static String APP_ID = "X-Auth-App-Id";
    final public static String APP_SECRET = "X-Auth-App-Secret";
}
