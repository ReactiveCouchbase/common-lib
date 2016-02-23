package org.reactivecouchbase.common;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class MeasuredRate {

    public static class Rate {
        public final BigDecimal value;
        public final Duration per;

        public Rate(BigDecimal value, Duration per) {
            Invariant.checkNotNull(value);
            Invariant.checkNotNull(per);
            this.value = value.setScale(3, BigDecimal.ROUND_HALF_EVEN);
            this.per = per;
        }

        public Rate to(Duration duration) {
            long nanoSource = per.toNanos();
            long nanoTarget = duration.toNanos();
            BigDecimal newValue = value.multiply(BigDecimal.valueOf(nanoTarget)).divide(BigDecimal.valueOf(nanoSource), 3, BigDecimal.ROUND_HALF_EVEN);
            return new Rate(newValue, duration);
        }

        public Rate perMillisecond() {
            return to(Duration.of(1L, TimeUnit.MILLISECONDS));
        }

        public Rate perSecond() {
            return to(Duration.of(1L, TimeUnit.SECONDS));
        }

        public Rate perMinute() {
            return to(Duration.of(1L, TimeUnit.MINUTES));
        }

        public Rate perHour() {
            return to(Duration.of(1L, TimeUnit.HOURS));
        }

        public Rate perDay() {
            return to(Duration.of(1L, TimeUnit.DAYS));
        }

        public Rate perMonth() {
            return to(Duration.of(30L, TimeUnit.DAYS));
        }

        public Rate perYear() {
            return to(Duration.of(365L, TimeUnit.DAYS));
        }

        @Override
        public String toString() {
            return "Rate { " +
                    "value = " + value +
                    ", per = " + per.toHumanReadable() +
                    " }";
        }
    }

    private final AtomicLong lastBucket = new AtomicLong(0);
    private final AtomicLong currentBucket = new AtomicLong(0);
    private final long sampleInterval;
    private volatile long threshold;

    public MeasuredRate(long sampleInterval) {
        this.sampleInterval = sampleInterval;
        this.threshold = System.currentTimeMillis() + sampleInterval;
    }

    public static MeasuredRate of(Duration duration) {
        return new MeasuredRate(duration.toMillis());
    }

    public static MeasuredRate of(Long duration) {
        return new MeasuredRate(duration);
    }

    /**
     * Returns the count in the last sample interval
     */
    public long getCount() {
        checkAndResetWindow();
        return lastBucket.get();
    }

    public Rate rate() {
        return new Rate(BigDecimal.valueOf(getCount()), Duration.of(sampleInterval, TimeUnit.MILLISECONDS));
    }

    /**
     * Returns the count in the current sample interval which will be incomplete.
     */
    public long getCurrentCount() {
        checkAndResetWindow();
        return currentBucket.get();
    }

    public void increment() {
        checkAndResetWindow();
        currentBucket.incrementAndGet();
    }

    public void mark() {
        increment();
    }

    public void increment(long of) {
        checkAndResetWindow();
        currentBucket.addAndGet(of);
    }

    public void mark(long of) {
        increment(of);
    }

    private void checkAndResetWindow() {
        long now = System.currentTimeMillis();
        if (threshold < now) {
            lastBucket.set(currentBucket.get());
            currentBucket.set(0);
            threshold = now + sampleInterval;
        }
    }

    public void reset() {
        lastBucket.set(0);
        currentBucket.set(0);
    }

    @Override
    public String toString() {
        return "count:" + getCount() + "currentCount:" + getCurrentCount();
    }
}