package com.gaboratorium.stash.resources.requestLogs.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface RequestLogDao {

    @SqlQuery("insert into request_logs values (:requestLogId, :requestType, :requestUrl, :isSuccessful) returning *")
    @Mapper(RequestLogMapper.class)
    public RequestLog insert(
        @Bind("requestLogId") String requestLogId,
        @Bind("requestType") String requestType,
        @Bind("requestUrl") String requestUrl,
        @Bind("isSuccessful") Boolean isSuccessful
    );
}
