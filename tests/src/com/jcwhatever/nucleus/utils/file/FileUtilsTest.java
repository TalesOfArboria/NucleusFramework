package com.jcwhatever.nucleus.utils.file;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class FileUtilsTest {

    @Test
    public void testGetFileExtension() throws Exception {

        String fileName = "test.js";
        File file = new File("test.js");

        Assert.assertEquals("js", FileUtils.getFileExtension(file));

        Assert.assertEquals("js", FileUtils.getFileExtension(fileName));
    }

    @Test
    public void testGetFileExtension1() throws Exception {

        String fileName = "test";
        File file = new File("test");

        Assert.assertEquals("", FileUtils.getFileExtension(file));

        Assert.assertEquals("", FileUtils.getFileExtension(fileName));
    }

    @Test
    public void testGetNameWithoutExtension() throws Exception {
        String fileName = "test.js";
        File file = new File("test.js");

        Assert.assertEquals("test", FileUtils.getNameWithoutExtension(file));

        Assert.assertEquals("test", FileUtils.getNameWithoutExtension(fileName));
    }

    @Test
    public void testGetNameWithoutExtension1() throws Exception {
        String fileName = ".js";
        File file = new File(".js");

        Assert.assertEquals("", FileUtils.getNameWithoutExtension(file));

        Assert.assertEquals("", FileUtils.getNameWithoutExtension(fileName));
    }

    @Test
    public void testGetRelative() throws Exception {

        File base = new File("folder1/base");

        File file = new File("folder1/base/test.js");

        Assert.assertEquals("test.js", FileUtils.getRelative(base, file));
    }

    @Test
    public void testGetRelative2() throws Exception {

        File base = new File("folder1/base");

        File file = new File("folder1/base");

        Assert.assertEquals("", FileUtils.getRelative(base, file));
    }

}