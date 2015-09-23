package com.jcwhatever.nucleus.utils.text;

import com.jcwhatever.nucleus.utils.text.TextFormat.TextFormatMap;
import com.jcwhatever.nucleus.utils.text.TextFormat.TextFormats;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class TextFormatTest {

    @Test
    public void testRemove() throws Exception {

        String source = "§l§1Test §2String§3";

        assertEquals("Test String", TextFormat.remove(source));
    }

    @Test
    public void testSeparate() throws Exception {

        String source = "§l§1Test §2String§3";

        TextFormatMap map = TextFormat.separate(source);

        assertEquals("Test String", map.getText());

        assertEquals(3, map.size());

        assertEquals("§l§1", map.get(0));

        assertEquals("§2", map.get(5));

        assertEquals("§3", map.get(11));
    }

    @Test
    public void testGetFormatAt() throws Exception {
        String source = "§l§1Test §2String§3";

        TextFormats formats = TextFormat.getFormatAt(11, source);

        assertEquals("§2", formats.toString());

        assertEquals(1, formats.getFormats().size());



        formats = TextFormat.getFormatAt(0, source);

        assertEquals("", formats.toString());

        assertEquals(0, formats.getFormats().size());



        formats = TextFormat.getFormatAt(4, source);

        assertEquals("§1", formats.toString());

        assertEquals(1, formats.getFormats().size());
    }

    @Test
    public void testGetEndFormat() throws Exception {

        String source = "§l§1Test §2String";

        TextFormats formats = TextFormat.getEndFormat(source);

        assertEquals("§2", formats.toString());

        assertEquals(1, formats.getFormats().size());
    }


    @Test
    public void testTrim() throws Exception {

        String source = "§1Test String§1";
        assertEquals("§1Test String", TextFormat.trim(source));

        source = "§1Test String§1§c§1";
        assertEquals("§1Test String", TextFormat.trim(source));

        source = "§1§1§1§1Test §1§1§1String§1§c§1";
        assertEquals("§1Test String", TextFormat.trim(source));

        source = "§1§c§1§1Test §1§c§1String§1§c§1";
        assertEquals("§1Test String", TextFormat.trim(source));

        source = "§1§c§1§1";
        assertEquals("", TextFormat.trim(source));
    }
}