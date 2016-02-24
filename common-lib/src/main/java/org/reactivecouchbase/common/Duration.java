package org.reactivecouchbase.common;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Representation for a duration with support of human readable output
 * <p/>
 * * Duration.of("12 s")
 * * Duration.parse("12 h")
 * * Duration.of(12, TimeUnit.SECONDS)
 */
public class Duration {

    public final Long value;
    public final TimeUnit unit;

    public Duration(Long value, TimeUnit unit) {
        Invariant.checkNotNull(value);
        Invariant.checkNotNull(unit);
        this.value = value;
        this.unit = unit;
    }

    public Duration(Integer value, TimeUnit unit) {
        Invariant.checkNotNull(value);
        Invariant.checkNotNull(unit);
        this.value = value.longValue();
        this.unit = unit;
    }

    public Duration(String expression) {
        Invariant.checkNotNull(expression);
        Duration d = parse(expression);
        this.value = d.value;
        this.unit = d.unit;
    }

    public static Duration of(Long value, TimeUnit unit) {
        return new Duration(value, unit);
    }

    public static Duration of(String expression) {
        return Duration.parse(expression);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Duration)) {
            return false;
        }

        Duration duration = (Duration) o;

        if (unit != duration.unit) {
            return false;
        }
        if (!value.equals(duration.value)) {
            return false;
        }

        return true;
    }

    @Override
    public final int hashCode() {
        int result = value.hashCode();
        result = 31 * result + unit.hashCode();
        return result;
    }

    public final String toHumanReadable() {
        return toHumanReadable(false);
    }

    public final String toHumanReadable(boolean small) {
        if (toMillis() == 0L) {
            if (toMicros() == 0L) {
                return value + " nanos";
            } else {
                return value + " microsec";
            }
        }
        if (toMillis() < 1000L) {
            return value + " millis";
        }
        Long seconds = toSeconds();
        Double numyears = Math.floor(seconds / 31536000);
        Double numdays = Math.floor((seconds % 31536000) / 86400);
        Double numhours = Math.floor(((seconds % 31536000) % 86400) / 3600);
        Double numminutes = Math.floor((((seconds % 31536000) % 86400) % 3600) / 60);
        Long numseconds = (((seconds % 31536000) % 86400) % 3600) % 60;
        Long nummillis = toMillis() - (seconds * 1000);
        Long nummicros = toMicros() - (nummillis * 1000) - (seconds * 1000000);
        Long numnanos = toNanos() - (nummicros * 1000) - (nummillis * 1000000) - (seconds * 1000000000);
        StringBuilder builder = new StringBuilder();
        if (numyears > 0) {
            builder.append(numyears.intValue()).append(small ? "y" : " year");
            if (numyears > 1 && !small) {
                builder.append("s ");
            } else {
                builder.append(" ");
            }
        }
        if (numdays > 0) {
            builder.append(numdays.intValue()).append(small ? "d" : " day");
            if (numdays > 1 && !small) {
                builder.append("s ");
            } else {
                builder.append(" ");
            }
        }
        if (numhours > 0) {
            builder.append(numhours.intValue()).append(small ? "h" : " hour");
            if (numhours > 1 && !small) {
                builder.append("s ");
            } else {
                builder.append(" ");
            }
        }
        if (numminutes > 0) {
            builder.append(numminutes.intValue()).append(small ? "m" : " minute");
            if (numminutes > 1 && !small) {
                builder.append("s ");
            } else {
                builder.append(" ");
            }
        }
        if (numseconds > 0) {
            builder.append(numseconds).append(small ? "s" : " second");
            if (numseconds > 1 && !small) {
                builder.append("s ");
            } else {
                builder.append(" ");
            }
        }
        if (nummillis > 0) {
            builder.append(nummillis).append(small ? " milli" : " millisecond");
            if (nummillis > 1 && !small) {
                builder.append("s ");
            } else {
                builder.append(" ");
            }
        }
        if (nummicros > 0) {
            builder.append(nummicros).append(small ? " micro" : " microsecond");
            if (nummicros > 1 && !small) {
                builder.append("s ");
            } else {
                builder.append(" ");
            }
        }
        if (numnanos > 0) {
            builder.append(numnanos).append(small ? " nano" : " nanosecond");
            if (numnanos > 1 && !small) {
                builder.append("s ");
            } else {
                builder.append(" ");
            }
        }
        return builder.toString().trim();
    }

    @Override
    public final String toString() {
        return "Duration{" +
                "value=" + value +
                ", unit=" + unit +
                '}';
    }

    public static final Map<String, TimeUnit> UNITS = Collections.unmodifiableMap(new HashMap<String, TimeUnit>() {{
        put("milli", TimeUnit.MILLISECONDS);
        put("millis", TimeUnit.MILLISECONDS);
        put("millisecond", TimeUnit.MILLISECONDS);
        put("milliseconds", TimeUnit.MILLISECONDS);
        put("sec", TimeUnit.SECONDS);
        put("min", TimeUnit.MINUTES);
        put("minute", TimeUnit.MINUTES);
        put("minutes", TimeUnit.MINUTES);
        put("hours", TimeUnit.HOURS);
        put("hour", TimeUnit.HOURS);
        put("day", TimeUnit.DAYS);
        put("days", TimeUnit.DAYS);
        put("micro", TimeUnit.MICROSECONDS);
        put("micros", TimeUnit.MICROSECONDS);
        put("microsecond", TimeUnit.MICROSECONDS);
        put("microseconds", TimeUnit.MICROSECONDS);
        put("nano", TimeUnit.NANOSECONDS);
        put("nanos", TimeUnit.NANOSECONDS);
        put("nanosecond", TimeUnit.NANOSECONDS);
        put("nanoseconds", TimeUnit.NANOSECONDS);
        put("seconds", TimeUnit.SECONDS);
        put("second", TimeUnit.SECONDS);
        put("ns", TimeUnit.NANOSECONDS);
        put("ms", TimeUnit.MILLISECONDS);
        put("s", TimeUnit.SECONDS);
        put("m", TimeUnit.MINUTES);
        put("h", TimeUnit.HOURS);
        put("d", TimeUnit.DAYS);
    }});

    public static Duration parse(String expression) {
        if (expression.contains(" ")) {
            List<String> parts = Arrays.asList(expression.trim().toLowerCase().split(" "));
            Invariant.invariant(parts.size() == 2, "Duration expression should have two parts");
            Long value = Long.valueOf(parts.get(0));
            String unitOfTime = parts.get(1);
            if (UNITS.containsKey(unitOfTime)) {
                return new Duration(value, UNITS.get(unitOfTime.toLowerCase()));
            } else {
                throw new RuntimeException("Can't parse expression " + expression + ". The format is 'value unit'.");
            }
        } else {
            String unitExpression = expression.replaceAll("([0-9]+)", "").trim().toLowerCase();
            String valueExpression = expression.toLowerCase().replace(unitExpression, "").trim();
            if (UNITS.containsKey(unitExpression)) {
                Long value = Long.valueOf(valueExpression);
                return new Duration(value, UNITS.get(unitExpression));
            }
            throw new RuntimeException("Can't parse expression " + expression + ". The format is 'value unit'.");
        }
    }

    public final long toNanos() {
        return unit.toNanos(value);
    }

    public final long toMicros() {
        return unit.toMicros(value);
    }

    public final long toMillis() {
        return unit.toMillis(value);
    }

    public final long toSeconds() {
        return unit.toSeconds(value);
    }

    public final long toMinutes() {
        return unit.toMinutes(value);
    }

    public final long toHours() {
        return unit.toHours(value);
    }

    public final long toDays() {
        return unit.toDays(value);
    }

    public final Duration plus(Duration d) {
        return new Duration(this.toNanos() + d.toNanos(), TimeUnit.NANOSECONDS);
    }

    public final Duration minus(Duration d) {
        long value = this.toNanos() - d.toNanos();
        if (value < 0L) {
            value = 0L;
        }
        return new Duration(value, TimeUnit.NANOSECONDS);
    }

    public final java.time.Duration toJdkDuration() {
        return java.time.Duration.ofNanos(toNanos());
    }

    public static Measurable measure() {
        return new Measurable(TimeUnit.MILLISECONDS, System.currentTimeMillis());
    }

    public static Measurable measureNanos() {
        return new Measurable(TimeUnit.NANOSECONDS, System.nanoTime());
    }

    public static class Measurable {
        final TimeUnit unit;
        final Long start;

        Measurable(TimeUnit unit, Long start) {
            this.unit = unit;
            this.start = start;
        }

        public final Duration stop() {
            if (unit.equals(TimeUnit.MILLISECONDS)) {
                return new Duration(System.currentTimeMillis() - start, unit);
            }
            if (unit.equals(TimeUnit.NANOSECONDS)) {
                return new Duration(System.nanoTime() - start, unit);
            }
            throw new RuntimeException("Unsupported unit " + unit.name());
        }
    }
}
