package com.jcwhatever.nucleus.utils;

import org.junit.Assert;
import org.junit.Test;

public class EnumUtilsTest {

    public enum TestEnum {
        CONSTANT,
        DEFAULT
    }

    @Test
    public void testGetEnum() throws Exception {

        Assert.assertEquals(TestEnum.CONSTANT, EnumUtils.getEnum("CONSTANT", TestEnum.class));
        Assert.assertEquals(null, EnumUtils.getEnum("constant1", TestEnum.class));
        Assert.assertEquals(null, EnumUtils.getEnum("invalid", TestEnum.class));
    }

    @Test
    public void testGetEnum1() throws Exception {

        Assert.assertEquals(TestEnum.CONSTANT, EnumUtils.getEnum("CONSTANT", TestEnum.class, TestEnum.DEFAULT));
        Assert.assertEquals(TestEnum.DEFAULT, EnumUtils.getEnum("constant", TestEnum.class, TestEnum.DEFAULT));
        Assert.assertEquals(TestEnum.DEFAULT, EnumUtils.getEnum("invalid", TestEnum.class, TestEnum.DEFAULT));
    }

    @Test
    public void testSearchEnum() throws Exception {
        Assert.assertEquals(TestEnum.CONSTANT, EnumUtils.searchEnum("CONSTANT", TestEnum.class));
        Assert.assertEquals(TestEnum.CONSTANT, EnumUtils.searchEnum("constant", TestEnum.class));
        Assert.assertEquals(null, EnumUtils.searchEnum("invalid", TestEnum.class));
    }

    @Test
    public void testSearchEnum1() throws Exception {
        Assert.assertEquals(TestEnum.CONSTANT, EnumUtils.searchEnum("CONSTANT", TestEnum.class, TestEnum.DEFAULT));
        Assert.assertEquals(TestEnum.CONSTANT, EnumUtils.searchEnum("constant", TestEnum.class, TestEnum.DEFAULT));
        Assert.assertEquals(TestEnum.DEFAULT, EnumUtils.searchEnum("invalid", TestEnum.class, TestEnum.DEFAULT));
    }

    @Test
    public void testGetGenericEnum() throws Exception {

        Class<? extends Enum<?>> clazz = TestEnum.class;

        Assert.assertEquals(TestEnum.CONSTANT, EnumUtils.getGenericEnum("CONSTANT", clazz, null));
        Assert.assertEquals(null, EnumUtils.getGenericEnum("constant", clazz, null));
        Assert.assertEquals(null, EnumUtils.getGenericEnum("invalid", clazz, null));

        Assert.assertEquals(TestEnum.CONSTANT, EnumUtils.getGenericEnum("CONSTANT", clazz, TestEnum.DEFAULT));
        Assert.assertEquals(TestEnum.DEFAULT, EnumUtils.getGenericEnum("constant", clazz, TestEnum.DEFAULT));
        Assert.assertEquals(TestEnum.DEFAULT, EnumUtils.getGenericEnum("invalid", clazz, TestEnum.DEFAULT));
    }

    @Test
    public void testSearchGenericEnum() throws Exception {
        Class<? extends Enum<?>> clazz = TestEnum.class;

        Assert.assertEquals(TestEnum.CONSTANT, EnumUtils.searchGenericEnum("CONSTANT", clazz, null));
        Assert.assertEquals(TestEnum.CONSTANT, EnumUtils.searchGenericEnum("constant", clazz, null));
        Assert.assertEquals(null, EnumUtils.searchGenericEnum("invalid", clazz, null));

        Assert.assertEquals(TestEnum.CONSTANT, EnumUtils.searchGenericEnum("CONSTANT", clazz, TestEnum.DEFAULT));
        Assert.assertEquals(TestEnum.CONSTANT, EnumUtils.searchGenericEnum("constant", clazz, TestEnum.DEFAULT));
        Assert.assertEquals(TestEnum.DEFAULT, EnumUtils.searchGenericEnum("invalid", clazz, TestEnum.DEFAULT));
    }

    @Test
    public void testGetRawEnum() throws Exception {

        Class clazz = TestEnum.class;

        Assert.assertEquals(TestEnum.CONSTANT, EnumUtils.getRawEnum("CONSTANT", clazz, null));
        Assert.assertEquals(null, EnumUtils.getRawEnum("constant", clazz, null));
        Assert.assertEquals(null, EnumUtils.getRawEnum("invalid", clazz, null));

        Assert.assertEquals(TestEnum.CONSTANT, EnumUtils.getRawEnum("CONSTANT", clazz, TestEnum.DEFAULT));
        Assert.assertEquals(TestEnum.DEFAULT, EnumUtils.getRawEnum("constant", clazz, TestEnum.DEFAULT));
        Assert.assertEquals(TestEnum.DEFAULT, EnumUtils.getRawEnum("invalid", clazz, TestEnum.DEFAULT));
    }

    @Test
    public void testSearchRawEnum() throws Exception {

        Class clazz = TestEnum.class;

        Assert.assertEquals(TestEnum.CONSTANT, EnumUtils.searchRawEnum("CONSTANT", clazz, null));
        Assert.assertEquals(TestEnum.CONSTANT, EnumUtils.searchRawEnum("constant", clazz, null));
        Assert.assertEquals(null, EnumUtils.searchRawEnum("invalid", clazz, null));

        Assert.assertEquals(TestEnum.CONSTANT, EnumUtils.searchRawEnum("CONSTANT", clazz, TestEnum.DEFAULT));
        Assert.assertEquals(TestEnum.CONSTANT, EnumUtils.searchRawEnum("constant", clazz, TestEnum.DEFAULT));
        Assert.assertEquals(TestEnum.DEFAULT, EnumUtils.searchRawEnum("invalid", clazz, TestEnum.DEFAULT));
    }
}