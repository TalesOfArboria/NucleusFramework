package com.jcwhatever.nucleus.utils.text;

import com.jcwhatever.nucleus.utils.text.TextFormat.TextFormatMap;
import com.jcwhatever.nucleus.utils.text.TextFormat.TextFormats;

import org.junit.Assert;
import org.junit.Test;


public class TextFormatTest {

    @Test
    public void testRemove() throws Exception {

        String source = "§l§1Test §2String§3";

        Assert.assertEquals("Test String", TextFormat.remove(source));
    }

    @Test
    public void testSeparate() throws Exception {

        String source = "§l§1Test §2String§3";

        TextFormatMap map = TextFormat.separate(source);

        Assert.assertEquals("Test String", map.getText());

        Assert.assertEquals(3, map.size());

        Assert.assertEquals("§l§1", map.get(0));

        Assert.assertEquals("§2", map.get(5));

        Assert.assertEquals("§3", map.get(11));
    }

    @Test
    public void testGetFormatAt() throws Exception {
        String source = "§l§1Test §2String§3";

        TextFormats formats = TextFormat.getFormatAt(11, source);

        Assert.assertEquals("§2", formats.toString());

        Assert.assertEquals(1, formats.getFormats().size());



        formats = TextFormat.getFormatAt(0, source);

        Assert.assertEquals("", formats.toString());

        Assert.assertEquals(0, formats.getFormats().size());



        formats = TextFormat.getFormatAt(4, source);

        Assert.assertEquals("§1", formats.toString());

        Assert.assertEquals(1, formats.getFormats().size());
    }

    @Test
    public void testGetEndFormat() throws Exception {

        String source = "§l§1Test §2String";

        TextFormats formats = TextFormat.getEndFormat(source);

        Assert.assertEquals("§2", formats.toString());

        Assert.assertEquals(1, formats.getFormats().size());
    }

}