package com.myth.filters;

import com.myth.context.RequestContext;
import io.dropwizard.util.Strings;
import org.slf4j.MDC;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.UUID;

@Provider
@Priority(Priorities.USER)
public class RequestIdFilter implements ContainerRequestFilter, ContainerResponseFilter {
    private static final String REQUEST_ID = "X-Request-Id";

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        String requestId = containerRequestContext.getHeaderString(REQUEST_ID);
        if (Strings.isNullOrEmpty(requestId)) {
            requestId = UUID.randomUUID().toString();
        }
        containerRequestContext.getHeaders().putSingle(REQUEST_ID, requestId);
        MDC.put("requestId", requestId);
        RequestContext.current().setArg("requestId", requestId);
    }

    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) throws IOException {
        final String requestId = containerRequestContext.getHeaderString(REQUEST_ID);
        containerResponseContext.getHeaders().putSingle(REQUEST_ID, requestId);
        MDC.remove("requestId");
    }
}
