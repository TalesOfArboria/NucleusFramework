package com.jcwhatever.nucleus.utils;

import org.junit.Test;

public class PreConTest {

    @Test(expected=IllegalStateException.class)
    public void testIsValid() throws Exception {
        PreCon.isValid(false);
    }

    @Test(expected=IllegalStateException.class)
    public void testIsValid1() throws Exception {
        PreCon.isValid(false, "Error message.");
    }

    @Test
    public void testIsValid2() throws Exception {
        PreCon.isValid(true);
    }

    @Test
    public void testIsValid3() throws Exception {
        PreCon.isValid(true, "Error message.");
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testSupported() throws Exception {
        PreCon.supported(false);
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testSupported1() throws Exception {
        PreCon.supported(false, "Error message.");
    }

    @Test
    public void testSupported2() throws Exception {
        PreCon.supported(true);
    }

    @Test
    public void testSupported3() throws Exception {
        PreCon.supported(true, "Error message.");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNotNull() throws Exception {
        PreCon.notNull(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNotNull1() throws Exception {
        PreCon.notNull(null, "paramName");
    }

    @Test
    public void testNotNull2() throws Exception {
        PreCon.notNull(new Object());
    }

    @Test
    public void testNotNull3() throws Exception {
        PreCon.notNull(new Object(), "paramName");
    }


    @Test(expected=IllegalArgumentException.class)
    public void testNotNullOrEmpty() throws Exception {
        PreCon.notNullOrEmpty(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNotNullOrEmpty1() throws Exception {
        PreCon.notNullOrEmpty("");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNotNullOrEmpty2() throws Exception {
        PreCon.notNullOrEmpty(null, "paramName");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNotNullOrEmpty3() throws Exception {
        PreCon.notNullOrEmpty("", "paramName");
    }

    @Test
    public void testNotNullOrEmpty4() throws Exception {
        PreCon.notNullOrEmpty("valid");
    }

    @Test
    public void testNotNullOrEmpty5() throws Exception {
        PreCon.notNullOrEmpty("valid", "paramName");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValidNodeName() throws Exception {
        PreCon.validNodeName(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValidNodeName1() throws Exception {
        PreCon.validNodeName("");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValidNodeName2() throws Exception {
        PreCon.validNodeName("invalid.name");
    }

    @Test
    public void testValidNodeName3() throws Exception {
        PreCon.validNodeName("valid-name");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValidNodePath() throws Exception {
        PreCon.validNodePath(null);
    }

    @Test
    public void testValidNodePath1() throws Exception {
        PreCon.validNodePath("");
    }

    @Test
    public void testValidNodePath2() throws Exception {
        PreCon.validNodePath("valid.path");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGreaterThanZero() throws Exception {
        PreCon.greaterThanZero(0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGreaterThanZero1() throws Exception {
        PreCon.greaterThanZero(0, "paramName");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGreaterThanZero2() throws Exception {
        PreCon.greaterThanZero(0.0D);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGreaterThanZero3() throws Exception {
        PreCon.greaterThanZero(0.0D, "paramName");
    }

    @Test
    public void testGreaterThanZero4() throws Exception {
        PreCon.greaterThanZero(1);
    }

    @Test
    public void testGreaterThanZero5() throws Exception {
        PreCon.greaterThanZero(1, "paramName");
    }

    @Test
    public void testGreaterThanZero6() throws Exception {
        PreCon.greaterThanZero(1.0D);
    }

    @Test
    public void testGreaterThanZero7() throws Exception {
        PreCon.greaterThanZero(1.0D, "paramName");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testPositiveNumber() throws Exception {
        PreCon.positiveNumber(-1);
    }
    @Test(expected=IllegalArgumentException.class)
    public void testPositiveNumber1() throws Exception {
        PreCon.positiveNumber(-1, "paramName");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testPositiveNumber2() throws Exception {
        PreCon.positiveNumber(-1.0D, "paramName");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testPositiveNumber3() throws Exception {
        PreCon.positiveNumber(-1.0D, "paramName");
    }

    @Test
    public void testPositiveNumber4() throws Exception {
        PreCon.positiveNumber(0);
    }
    @Test
    public void testPositiveNumber5() throws Exception {
        PreCon.positiveNumber(0, "paramName");
    }

    @Test
    public void testPositiveNumber6() throws Exception {
        PreCon.positiveNumber(0.0D, "paramName");
    }

    @Test
    public void testPositiveNumber7() throws Exception {
        PreCon.positiveNumber(0.0D, "paramName");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLessThan() throws Exception {
        PreCon.lessThan(10, 10);

    }
    @Test(expected=IllegalArgumentException.class)
    public void testLessThan1() throws Exception {
        PreCon.lessThan(10, 10, "paramName");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLessThan2() throws Exception {
        PreCon.lessThan(10.0D, 10.0D);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLessThan3() throws Exception {
        PreCon.lessThan(10.0D, 10.0D, "paramName");
    }

    @Test
    public void testLessThan4() throws Exception {
        PreCon.lessThan(9, 10);

    }
    @Test
    public void testLessThan5() throws Exception {
        PreCon.lessThan(9, 10, "paramName");
    }

    @Test
    public void testLessThan6() throws Exception {
        PreCon.lessThan(9.0D, 10.0D);
    }

    @Test
    public void testLessThan7() throws Exception {
        PreCon.lessThan(9.0D, 10.0D, "paramName");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLessThanEqual() throws Exception {
        PreCon.lessThanEqual(11, 10);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLessThanEqual1() throws Exception {
        PreCon.lessThanEqual(11, 10, "paramName");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLessThanEqual2() throws Exception {
        PreCon.lessThanEqual(11.0D, 10.0D, "paramName");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLessThanEqual3() throws Exception {
        PreCon.lessThanEqual(11.0D, 10.0D, "paramName");
    }

    @Test
    public void testLessThanEqual4() throws Exception {
        PreCon.lessThanEqual(10, 10);
    }

    @Test
    public void testLessThanEqual5() throws Exception {
        PreCon.lessThanEqual(10, 10, "paramName");
    }

    @Test
    public void testLessThanEqual6() throws Exception {
        PreCon.lessThanEqual(10.0D, 10.0D, "paramName");
    }

    @Test
    public void testLessThanEqual7() throws Exception {
        PreCon.lessThanEqual(10.0D, 10.0D, "paramName");
    }
}