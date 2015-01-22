package com.jcwhatever.nucleus.utils;

import com.jcwhatever.nucleus.utils.DateUtils.TimeRound;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class DateUtilsTest {

    @Test
    public void testGetDeltaMilliseconds() throws Exception {

        long now = System.currentTimeMillis();

        long later = now + 5;

        Assert.assertEquals(5, DateUtils.getDeltaMilliseconds(new Date(now), new Date(later)));

        Assert.assertEquals(-5, DateUtils.getDeltaMilliseconds(new Date(later), new Date(now)));
    }

    @Test
    public void testGetDeltaTicks() throws Exception {

        long now = System.currentTimeMillis();

        long later = now + (5 * 50);

        Assert.assertEquals(5, DateUtils.getDeltaTicks(new Date(now), new Date(later)));

        Assert.assertEquals(-5, DateUtils.getDeltaTicks(new Date(later), new Date(now)));
    }

    @Test
    public void testGetDeltaSeconds() throws Exception {
        long now = System.currentTimeMillis();

        long later = now + (5 * 50 * 20);

        Assert.assertEquals(5.0D, DateUtils.getDeltaSeconds(new Date(now), new Date(later)), 0.0D);

        Assert.assertEquals(-5.0D, DateUtils.getDeltaSeconds(new Date(later), new Date(now)), 0.0D);
    }

    @Test
    public void testGetDeltaSeconds1() throws Exception {
        long now = System.currentTimeMillis();

        long later = now + (5 * 50 * 20) + 10;

        Assert.assertEquals(6L, DateUtils.getDeltaSeconds(new Date(now), new Date(later), TimeRound.ROUND_UP));

        Assert.assertEquals(-5L, DateUtils.getDeltaSeconds(new Date(later), new Date(now), TimeRound.ROUND_UP));

        Assert.assertEquals(5L, DateUtils.getDeltaSeconds(new Date(now), new Date(later), TimeRound.ROUND_DOWN));

        Assert.assertEquals(-6L, DateUtils.getDeltaSeconds(new Date(later), new Date(now), TimeRound.ROUND_DOWN));
    }

    @Test
    public void testGetDeltaMinutes() throws Exception {

        long now = System.currentTimeMillis();

        long later = now + (5 * 50 * 20 * 60);

        Assert.assertEquals(5.0D, DateUtils.getDeltaMinutes(new Date(now), new Date(later)), 0.0D);

        Assert.assertEquals(-5.0D, DateUtils.getDeltaMinutes(new Date(later), new Date(now)), 0.0D);
    }

    @Test
    public void testGetDeltaMinutes1() throws Exception {

        long now = System.currentTimeMillis();

        long later = now + (5 * 50 * 20 * 60) + 50;

        Assert.assertEquals(6L, DateUtils.getDeltaMinutes(new Date(now), new Date(later), TimeRound.ROUND_UP));

        Assert.assertEquals(-5L, DateUtils.getDeltaMinutes(new Date(later), new Date(now), TimeRound.ROUND_UP));

        Assert.assertEquals(5L, DateUtils.getDeltaMinutes(new Date(now), new Date(later), TimeRound.ROUND_DOWN));

        Assert.assertEquals(-6L, DateUtils.getDeltaMinutes(new Date(later), new Date(now), TimeRound.ROUND_DOWN));
    }

    @Test
    public void testGetDeltaHours() throws Exception {
        long now = System.currentTimeMillis();

        long later = now + (5 * 50 * 20 * 60 * 60);

        Assert.assertEquals(5.0D, DateUtils.getDeltaHours(new Date(now), new Date(later)), 0.0D);

        Assert.assertEquals(-5.0D, DateUtils.getDeltaHours(new Date(later), new Date(now)), 0.0D);
    }

    @Test
    public void testGetDeltaHours1() throws Exception {

        long now = System.currentTimeMillis();

        long later = now + (5 * 50 * 20 * 60 * 60) + 50;

        Assert.assertEquals(6L, DateUtils.getDeltaHours(new Date(now), new Date(later), TimeRound.ROUND_UP));

        Assert.assertEquals(-5L, DateUtils.getDeltaHours(new Date(later), new Date(now), TimeRound.ROUND_UP));

        Assert.assertEquals(5L, DateUtils.getDeltaHours(new Date(now), new Date(later), TimeRound.ROUND_DOWN));

        Assert.assertEquals(-6L, DateUtils.getDeltaHours(new Date(later), new Date(now), TimeRound.ROUND_DOWN));
    }

    @Test
    public void testGetDeltaDays() throws Exception {

        long now = System.currentTimeMillis();

        long later = now + (5 * 50 * 20 * 60 * 60 * 24);

        Assert.assertEquals(5.0D, DateUtils.getDeltaDays(new Date(now), new Date(later)), 0.0D);

        Assert.assertEquals(-5.0D, DateUtils.getDeltaDays(new Date(later), new Date(now)), 0.0D);
    }

    @Test
    public void testGetDeltaDays1() throws Exception {

        long now = System.currentTimeMillis();

        long later = now + (5 * 50 * 20 * 60 * 60 * 24) + 50;

        Assert.assertEquals(6L, DateUtils.getDeltaDays(new Date(now), new Date(later), TimeRound.ROUND_UP));

        Assert.assertEquals(-5L, DateUtils.getDeltaDays(new Date(later), new Date(now), TimeRound.ROUND_UP));

        Assert.assertEquals(5L, DateUtils.getDeltaDays(new Date(now), new Date(later), TimeRound.ROUND_DOWN));

        Assert.assertEquals(-6L, DateUtils.getDeltaDays(new Date(later), new Date(now), TimeRound.ROUND_DOWN));
    }

    @Test
    public void testAddMilliseconds() throws Exception {

        long now = System.currentTimeMillis();

        long later = now + 5;

        Date result = DateUtils.addMilliseconds(new Date(now), 5);

        Assert.assertEquals(result.getTime(), later);
    }

    @Test
    public void testAddTicks() throws Exception {

        long now = System.currentTimeMillis();

        long later = now + (5 * 50);

        Date result = DateUtils.addTicks(new Date(now), 5);

        Assert.assertEquals(result.getTime(), later);
    }

    @Test
    public void testAddSeconds() throws Exception {

        long now = System.currentTimeMillis();

        long later = now + (5 * 50 * 20);

        Date result = DateUtils.addSeconds(new Date(now), 5);

        Assert.assertEquals(result.getTime(), later);
    }

    @Test
    public void testAddMinutes() throws Exception {

        long now = System.currentTimeMillis();

        long later = now + (5 * 50 * 20 * 60);

        Date result = DateUtils.addMinutes(new Date(now), 5);

        Assert.assertEquals(result.getTime(), later);
    }

    @Test
    public void testAddHours() throws Exception {

        long now = System.currentTimeMillis();

        long later = now + (5 * 50 * 20 * 60 * 60);

        Date result = DateUtils.addHours(new Date(now), 5);

        Assert.assertEquals(result.getTime(), later);
    }

    @Test
    public void testAddDays() throws Exception {

        long now = System.currentTimeMillis();

        long later = now + (5 * 50 * 20 * 60 * 60 * 24);

        Date result = DateUtils.addDays(new Date(now), 5);

        Assert.assertEquals(result.getTime(), later);
    }
}