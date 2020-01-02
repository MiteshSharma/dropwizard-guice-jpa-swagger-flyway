package com.myth.context.mdc;

import org.slf4j.MDC;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MDCThreadPoolExecutor implements ExecutorService {

    private ExecutorService executorService;

    public MDCThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                 TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        this.executorService = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    @Override
    public void shutdown() {
        this.executorService.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return this.executorService.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return this.executorService.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return this.executorService.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return this.executorService.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return this.executorService.submit(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return this.executorService.submit(task, result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return this.executorService.submit(task);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return this.executorService.invokeAll(tasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return this.executorService.invokeAll(tasks, timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return this.executorService.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.executorService.invokeAny(tasks, timeout, unit);
    }

    @Override
    public void execute(Runnable command) {
        this.executorService.execute(this.wrap(command));
    }

    public Runnable wrap(final Runnable runnable) {
        final Map<String, String> mdcContextMap = MDC.getCopyOfContextMap();
        return () -> {
            Map<String, String> previousMdcContextMap = MDC.getCopyOfContextMap();
            if (mdcContextMap == null) {
                MDC.clear();
            } else {
                MDC.setContextMap(mdcContextMap);
            }
            try {
                runnable.run();
            } finally {
                if (previousMdcContextMap == null) {
                    MDC.clear();
                } else {
                    MDC.setContextMap(previousMdcContextMap);
                }
            }
        };
    }
}
