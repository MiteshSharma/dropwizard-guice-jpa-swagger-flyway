package com.myth.context;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ServerContext {
    private static Executor mysqlReadWriteExecutor = null;

    public static void init(ServerContextConfig contextConfig) {
        mysqlReadWriteExecutor = Executors.newFixedThreadPool(contextConfig.getMysqlReadWriteThreadCount());
    }

    public static Executor getMysqlReadWriteExecutor() {
        return mysqlReadWriteExecutor;
    }
}
