package com.myth.context;

import com.myth.context.mdc.RequestExecutors;

import java.util.concurrent.Executor;

public class ServerContext {
    private static Executor mysqlReadWriteExecutor = null;

    public static void init(ServerContextConfig contextConfig) {
        mysqlReadWriteExecutor = RequestExecutors.newFixedThreadPool(contextConfig.getMysqlReadWriteThreadCount());
    }

    public static Executor getMysqlReadWriteExecutor() {
        return mysqlReadWriteExecutor;
    }
}
