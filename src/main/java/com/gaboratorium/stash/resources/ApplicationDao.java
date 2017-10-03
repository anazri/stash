package com.gaboratorium.stash.resources;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;

public interface ApplicationDao {

    @SqlQuery("select * from payment.transactions where id = :transactionId")
    String findById(
        @Bind("transactionId") int transactionId
    );
}
