package org.mule.modules.caas.cli.config;

import java.util.concurrent.TimeUnit;

public class Timeout {

    private long duration;

    private TimeUnit unit;

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public void setUnit(TimeUnit unit) {
        this.unit = unit;
    }
}
