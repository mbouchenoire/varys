package org.varys.common.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public interface NotifierModule extends Callable<Void> {

    @Override
    default Void call() {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            final LocalDateTime start = LocalDateTime.now();

            Log.info("Starting notification iteration for the {} module...", this.getName());

            try {
                this.iterate();
            } catch (Exception e) {
                Log.error(e, "An unhandled error occurred while running the {} module", this.getName());
            }

            final Duration between = Duration.between(start, LocalDateTime.now());

            Log.info("{} module notification iteration duration: {} second(s)",
                    this.getName(), between.getSeconds());
        }, 0, this.getPeriodSeconds(), TimeUnit.SECONDS);

        Log.info("Successfuly started Jenkins module");

    return null;
    }

    String getName();

    long getPeriodSeconds();

    void iterate();
}
