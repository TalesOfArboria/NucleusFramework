package com.jcwhatever.nucleus.utils.text;

import com.jcwhatever.nucleus.utils.text.TextFormatterSettings.FormatPolicy;

import org.junit.Assert;
import org.junit.Test;

public class TextFormatterTest {

    /**
     * General test.
     */
    @Test
    public void testFormat() throws Exception {

        Assert.assertEquals("zero, one, two", TextUtils.format("{0}, {1}, {2}", "zero", "one", "two"));

        Assert.assertEquals("§czero, one, two", TextUtils.format("{RED}{0}, {1}, {2}", "zero", "one", "two"));

        Assert.assertEquals("§czero, §9one, two", TextUtils.format("{RED}{0}, {BLUE}{1}, {2}", "zero", "one", "two"));

        Assert.assertEquals("§czero, §9one§c, two", TextUtils.format("{RED}{0}, {1}, {2}", "zero", "§9one", "two"));

        Assert.assertEquals("§czero, §9one§c, two", TextUtils.format("{RED}{0}, {1}, {2}", "zero", "{BLUE}one", "two"));

        Assert.assertEquals("§c§lzero, §9one§c§l, two", TextUtils.format("{RED}{BOLD}{0}, {1}, {2}", "zero", "§9one", "two"));

        Assert.assertEquals("§zero", TextUtils.format("\\u00A7zero"));

        Assert.assertEquals("\\u00A7zero", TextUtils.format("\\\\u00A7zero"));

        Assert.assertEquals("\\§zero", TextUtils.format("\\\\\\u00A7zero"));

        Assert.assertEquals("zero\n one", TextUtils.format("zero\\n one"));

        Assert.assertEquals("zero\\n one", TextUtils.format("zero\\\\n one"));

        Assert.assertEquals("ZeroZeroOneZero", TextUtils.format("{0}{0}{1}{0}", "Zero", "One"));

        Assert.assertEquals("\"", TextUtils.format("{0}", "\""));
    }

    @Test
    public void testFormat1() throws Exception {

        TextFormatterSettings settings = new TextFormatterSettings().setEscaped('"');

        Assert.assertEquals("\\\"", TextUtils.format(settings, "\""));

        Assert.assertEquals("\\\"", TextUtils.format(settings, "{0}", "\""));
    }

    /**
     * Test Line Return Settings
     */
    @Test
    public void testFormat2() throws Exception {

        TextFormatterSettings settings = new TextFormatterSettings().setLineReturnPolicy(FormatPolicy.IGNORE);

        Assert.assertEquals("\\r", TextUtils.format(settings, "\\r"));

        Assert.assertEquals("\\n", TextUtils.format(settings, "\\n"));

        Assert.assertEquals("\\r", TextUtils.format(settings, "{0}", "\\r"));

        Assert.assertEquals("\\n", TextUtils.format(settings, "{0}", "\\n"));

        settings = new TextFormatterSettings().setLineReturnPolicy(FormatPolicy.REMOVE);

        Assert.assertEquals("", TextUtils.format(settings, "\\r"));

        Assert.assertEquals("", TextUtils.format(settings, "\\n"));

        Assert.assertEquals("", TextUtils.format(settings, "{0}", "\\r"));

        Assert.assertEquals("", TextUtils.format(settings, "{0}", "\\n"));
    }

    /**
     * Test Color Settings
     */
    @Test
    public void testFormat3() throws Exception {

        TextFormatterSettings settings = new TextFormatterSettings().setColorPolicy(FormatPolicy.IGNORE);

        Assert.assertEquals("{RED}redRed", TextUtils.format(settings, "{RED}red{0}", "Red"));

        Assert.assertEquals("{RED}red{RED}Red", TextUtils.format(settings, "{RED}red{0}", "{RED}Red"));

        settings = new TextFormatterSettings().setColorPolicy(FormatPolicy.REMOVE);

        Assert.assertEquals("redRed", TextUtils.format(settings, "{RED}red{0}", "Red"));

        Assert.assertEquals("redRed", TextUtils.format(settings, "{RED}red{0}", "{RED}Red"));
    }

    /**
     * Test Unicode Settings
     */
    @Test
    public void testFormat4() throws Exception {

        TextFormatterSettings settings = new TextFormatterSettings().setUnicodePolicy(FormatPolicy.IGNORE);

        Assert.assertEquals("\\u00A7unicodeTest", TextUtils.format(settings, "\\u00A7unicode{0}", "Test"));

        Assert.assertEquals("\\u00A7unicode\\u00A7Test", TextUtils.format(settings, "\\u00A7unicode{0}", "\\u00A7Test"));

        settings = new TextFormatterSettings().setUnicodePolicy(FormatPolicy.REMOVE);

        Assert.assertEquals("unicodeTest", TextUtils.format(settings, "\\u00A7unicode{0}", "Test"));

        Assert.assertEquals("unicodeTest", TextUtils.format(settings, "\\u00A7unicode{0}", "\\u00A7Test"));
    }

    /**
     * Test Argument Formatting Settings
     */
    @Test
    public void testFormat5() throws Exception {

        TextFormatterSettings settings = new TextFormatterSettings().setIsArgsFormatted(false);

        Assert.assertEquals("zero, one, two", TextUtils.format(settings, "{0}, {1}, {2}", "zero", "one", "two"));

        Assert.assertEquals("§czero, one, two", TextUtils.format(settings, "{RED}{0}, {1}, {2}", "zero", "one", "two"));

        Assert.assertEquals("§czero, §9one§c, two", TextUtils.format(settings, "{RED}{0}, {1}, {2}", "zero", "§9one", "two"));

        Assert.assertEquals("§czero, {BLUE}one, two", TextUtils.format(settings, "{RED}{0}, {1}, {2}", "zero", "{BLUE}one", "two"));

        Assert.assertEquals("§unicode\\u00A7Test", TextUtils.format(settings, "\\u00A7unicode{0}", "\\u00A7Test"));

        Assert.assertEquals("\n \\n", TextUtils.format(settings, "\\n {0}", "\\n"));


        settings.setEscaped('\"');

        Assert.assertEquals("\\\"\"", TextUtils.format(settings, "\"{0}", "\""));
    }
}