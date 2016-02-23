package org.reactivecouchbase.common.test;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.reactivecouchbase.common.Duration;
import org.reactivecouchbase.common.MeasuredRate;

import java.math.BigDecimal;

public class MeasuredRateTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    @Test
    public void testRates() throws Exception {
        MeasuredRate.Rate rate = new MeasuredRate.Rate(BigDecimal.valueOf(10L), Duration.of("10 milli"));
        System.out.println(rate);
        System.out.println(rate.perMillisecond());
        System.out.println(rate.perSecond());
        System.out.println(rate.perMinute());
        System.out.println(rate.perHour());
        System.out.println(rate.perDay());
        System.out.println(rate.perMonth());
        System.out.println(rate.perYear());
    }

} 
