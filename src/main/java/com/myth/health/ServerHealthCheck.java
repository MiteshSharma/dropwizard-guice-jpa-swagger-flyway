package com.myth.health;

import com.codahale.metrics.health.HealthCheck;

public class ServerHealthCheck extends HealthCheck {
    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }
}
