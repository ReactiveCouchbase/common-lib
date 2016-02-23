package org.reactivecouchbase.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class IdGenerators {

    private IdGenerators() {
    }

    private static final List<String> CHARACTERS = Arrays
            .asList("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray())
            .stream().map(Object::toString).collect(Collectors.toList());

    private static final List<String> EXTENDED_CHARACTERS =  Arrays
            .asList("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789*$£%)([]!=+-_:/;.><&".toCharArray())
            .stream().map(Object::toString).collect(Collectors.toList());

    private static final List<String> INIT_STRING = new ArrayList<String>() {{
        for (int b = 0; b <= 15; b++) {
            add(Integer.toHexString(b));
        }
    }};

    private static final Random RANDOM = new Random();

    public static String generateUUID() {
        StringBuilder builder = new StringBuilder();
        for (int c = 0; c <= 36; c++) {
            if (c == 9 || c == 14 || c == 19 || c == 24) {
                builder.append("-");
            } else {
                if (c == 15) {
                    builder.append("4");
                } else {
                    if (c == 20) {
                        Double rand = RANDOM.nextDouble() * 4.0;
                        builder.append(INIT_STRING.get(rand.intValue() | 8));
                    } else {
                        Double rand = RANDOM.nextDouble() * 15.0;
                        builder.append(INIT_STRING.get(rand.intValue() | 0));
                    }
                }
            }
        }
        return builder.toString();
    }

    public static String generateToken(List<String> characters, int size) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            Integer rand = RANDOM.nextInt(characters.size());
            builder.append(characters.get(rand));
        }
        return builder.toString();
    }

    public static String generateToken(int size) {
        return generateToken(CHARACTERS, size);
    }

    public static String generateToken() {
        return generateToken(64);
    }

    public static String generateExtendedToken(int size) {
        return generateToken(EXTENDED_CHARACTERS, size);
    }

    public static String generateExtendedToken() {
        return generateToken(EXTENDED_CHARACTERS, 64);
    }

    private static final AtomicLong generatorId = new AtomicLong(1L);
    private static final Long minus = 1288834974657L;
    private static final AtomicLong counter = new AtomicLong(-1L);
    private static final AtomicLong lastTimestamp = new AtomicLong(-1L);

    public static void setGeneratorId(Long value) {
        generatorId.set(value);
    }

    public static synchronized Long generateUniqueId() {
        if (generatorId.get() > 1024L) {
            throw new RuntimeException("Generator id can't be larger than 1024");
        }
        Long timestamp = System.currentTimeMillis();
        if (timestamp < lastTimestamp.get()) {
            throw new RuntimeException("Clock is running backward. Sorry :-(");
        }
        lastTimestamp.set(timestamp);
        counter.compareAndSet(4095, -1L);
        return ((timestamp - minus) << 22L) | (generatorId.get() << 10L) | counter.incrementAndGet();
    }

    public static String uuid() {
        return generateUUID();
    }

    public static String token(List<String> characters, int size) {
        return generateToken(characters, size);
    }

    public static synchronized Long uniqueId() {
        return generateUniqueId();
    }

    public static String token(int size) {
        return generateToken(size);
    }

    public static String token() {
        return generateToken();
    }

    public static String longToken(int size) {
        return generateExtendedToken(size);
    }

    public static String longToken() {
        return generateExtendedToken();
    }

}
