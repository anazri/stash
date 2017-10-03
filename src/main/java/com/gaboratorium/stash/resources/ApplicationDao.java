package com.gaboratorium.stash.resources;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;

public interface ApplicationDao {

    @SqlQuery("select application_name from applications where id = :applicationId")
    String findById(
        @Bind("applicationId") String applicationId
    );
}
