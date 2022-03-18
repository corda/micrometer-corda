/*
 * Copyright 2022 VMware, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micrometer.signalfx;

import io.micrometer.core.instrument.AbstractTimer;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.TimeWindowMax;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import io.micrometer.core.instrument.step.StepTuple2;
import io.micrometer.core.instrument.util.TimeUtils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

/**
 * This class is mostly the same as {@link io.micrometer.core.instrument.step.StepTimer}, with one notable difference:
 * the {@link DistributionStatisticConfig} is modified before being passed to the super class constructor -
 * that forces the histogram generated by this meter to be cumulative.
 *
 * @author Bogdan Drutu
 * @author Mateusz Rzeszutek
 */
final class SignalfxTimer extends AbstractTimer {

    private final LongAdder count = new LongAdder();
    private final LongAdder total = new LongAdder();
    private final StepTuple2<Long, Long> countTotal;
    private final TimeWindowMax max;

    SignalfxTimer(Id id, Clock clock, DistributionStatisticConfig distributionStatisticConfig, PauseDetector pauseDetector, TimeUnit baseTimeUnit, long stepMillis) {
        super(id, clock, CumulativeHistogramConfigUtil.updateConfig(distributionStatisticConfig), pauseDetector, baseTimeUnit, false);
        countTotal = new StepTuple2<>(clock, stepMillis, 0L, 0L, count::sumThenReset, total::sumThenReset);
        max = new TimeWindowMax(clock, distributionStatisticConfig);
    }

    @Override
    protected void recordNonNegative(long amount, TimeUnit unit) {
        final long nanoAmount = (long) TimeUtils.convert(amount, unit, TimeUnit.NANOSECONDS);
        count.increment();
        total.add(nanoAmount);
        max.record(amount, unit);
    }

    @Override
    public long count() {
        return countTotal.poll1();
    }

    @Override
    public double totalTime(TimeUnit unit) {
        return TimeUtils.nanosToUnit(countTotal.poll2(), unit);
    }

    @Override
    public double max(TimeUnit unit) {
        return max.poll(unit);
    }
}
