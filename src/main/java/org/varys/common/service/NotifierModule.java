package org.varys.common.service;

import java.util.concurrent.Callable;

public interface NotifierModule extends Callable<Void> {

    @Override
    default Void call() {
        try {
            this.startModule();
        } catch (Throwable t) {
            Log.error(t, "An unhandled error occurred while running a module");
        }

        return null;
    }

    void startModule();
}
