package com.myth.context;

import java.util.HashMap;
import java.util.Map;

public class RequestContext {
    private static ThreadLocal<RequestContext> localThreadContext = new ThreadLocal<RequestContext>();
    private Map<String, Object> args;

    public RequestContext() {
        this.args = new HashMap<>();
    }

    public static RequestContext current() {
        RequestContext context = localThreadContext.get();
        if (context == null) {
            context = new RequestContext();
            localThreadContext.set(context);
        }
        return context;
    }

    public static void reset() {
        localThreadContext.remove();
    }

    public void setArg(String key, Object value) {
        this.args.put(key, value);
    }

    public Object get(String key) {
        return this.args.get(key);
    }

    public Map<String, Object> getArgs() {
        return this.args;
    }

    public void setArgs(Map<String, Object> arguments) {
        this.args.putAll(arguments);
    }
}
