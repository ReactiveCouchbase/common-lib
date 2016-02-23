package org.reactivecouchbase.common;

import org.reactivecouchbase.functional.Option;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class Ranges {

    public static Iterable<Integer> simpleRange(final int from, final int to) {
        return () -> new Iterator<Integer>() {
            private int internalState = from;

            @Override
            public boolean hasNext() {
                return internalState != to;
            }

            @Override
            public Integer next() {
                internalState++;
                return internalState;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static class FiniteRangeGenerator<T> implements Iterable<T> {

        private final Iterator<T> generator;
        private final T from;
        private final T to;
        private final Function<T, T> nextitem;

        private FiniteRangeGenerator(final T from, final T to, final Function<T, T> nextItem) {
            Invariant.checkNotNull(from);
            Invariant.checkNotNull(to);
            Invariant.checkNotNull(nextItem);
            this.from = from;
            this.to = to;
            this.nextitem = nextItem;
            this.generator = Generator.from(from, input -> {
                if (to.equals(input)) {
                    return Option.none();
                }
                return Option.apply(nextItem.apply(input));
            }).iterator();
        }

        @Override
        public Iterator<T> iterator() {
            return generator;
        }

        public Iterable<T> cycle(final int rounds) {
            final AtomicInteger counter = new AtomicInteger(1);
            final Holder<FiniteRangeGenerator<T>> holder = Holder.of(this);
            return () -> {
                if (!holder.get().iterator().hasNext() && counter.get() != rounds) {
                    counter.incrementAndGet();
                    holder.set(new FiniteRangeGenerator<>(from, to, nextitem));
                }
                return holder.get().iterator();
            };
        }

        public Iterable<T> infiniteCycle() {
            final Holder<FiniteRangeGenerator<T>> holder = Holder.of(this);
            return () -> {
                if (!holder.get().iterator().hasNext()) {
                    holder.set(new FiniteRangeGenerator<>(from, to, nextitem));
                }
                return holder.get().iterator();
            };
        }
    }

    public static <T> FiniteRangeGenerator<T> range(T from, T to, Function<T, T> next) {
        return new FiniteRangeGenerator<>(from, to, next);
    }

    public static <T extends Number> FiniteRangeGenerator<T> numericRange(T from, T to, Function<T, T> next) {
        return new FiniteRangeGenerator<T>(from, to, next);
    }

    public static FiniteRangeGenerator<Long> longRange(long from, long to) {
        if (to < from) {
            return longRange(from, to, -1L);
        } else {
            return longRange(from, to, 1L);
        }
    }

    public static FiniteRangeGenerator<Long> longRange(long from, long to, final long increment) {
        return new FiniteRangeGenerator<>(from, to, input -> input + increment);
    }

    public static FiniteRangeGenerator<Integer> intRange(int from, int to) {
        if (to < from) {
            return intRange(from, to, -1);
        } else {
            return intRange(from, to, 1);
        }
    }

    public static FiniteRangeGenerator<Integer> intRange(int from, int to, final int increment) {
        return new FiniteRangeGenerator<>(from, to, input -> input + increment);
    }

    public static FiniteRangeGenerator<Double> doubleRange(double from, double to) {
        if (to < from) {
            return doubleRange(from, to, -1.0);
        } else {
            return doubleRange(from, to, 1.0);
        }
    }

    public static FiniteRangeGenerator<Double> doubleRange(double from, double to, final double increment) {
        return new FiniteRangeGenerator<>(from, to, input -> input + increment);
    }

    public static FiniteRangeGenerator<Float> floatRange(float from, float to) {
        if (to < from) {
            return floatRange(from, to, -1.0f);
        } else {
            return floatRange(from, to, 1.0f);
        }
    }

    public static FiniteRangeGenerator<Float> floatRange(float from, float to, final float increment) {
        return new FiniteRangeGenerator<>(from, to, input -> input + increment);
    }

    public static FiniteRangeGenerator<BigInteger> bigIntRange(BigInteger from, BigInteger to) {
        if (to.compareTo(from) < 0) {
            return bigIntRange(from, to, BigInteger.valueOf(-1));
        } else {
            return bigIntRange(from, to, BigInteger.ONE);
        }
    }

    public static FiniteRangeGenerator<BigInteger> bigIntRange(BigInteger from, BigInteger to, final BigInteger increment) {
        return new FiniteRangeGenerator<>(from, to, input -> input.add(increment));
    }

    public static FiniteRangeGenerator<BigDecimal> bigDecRange(BigDecimal from, BigDecimal to) {
        if (to.compareTo(from) < 0) {
            return bigDecRange(from, to, BigDecimal.valueOf(-1.0));
        } else {
            return bigDecRange(from, to, BigDecimal.ONE);
        }
    }

    public static FiniteRangeGenerator<BigDecimal> bigDecRange(BigDecimal from, BigDecimal to, final BigDecimal increment) {
        return new FiniteRangeGenerator<>(from, to, input -> input.add(increment));
    }
}
