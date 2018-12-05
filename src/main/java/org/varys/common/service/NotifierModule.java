/*
 * This file is part of Varys.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Varys.  If not, see <https://www.gnu.org/licenses/>.
 */

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

        Log.info("Successfuly started {} module", this.getName());

    return null;
    }

    String getName();

    long getPeriodSeconds();

    void iterate();
}
