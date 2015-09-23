package com.jcwhatever.nucleus.utils.text;

import com.jcwhatever.nucleus.utils.text.components.IChatMessage;
import com.jcwhatever.nucleus.utils.text.format.TextFormatterSettings;
import com.jcwhatever.nucleus.utils.text.format.TextFormatterSettings.FormatPolicy;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TextFormatterTest {

    /**
     * General test.
     */
    @Test
    public void testFormat() throws Exception {

        assertEquals("zero, one, two", format("{0}, {1}, {2}", "zero", "one", "two"));

        assertEquals("§czero, one, two", format("{RED}{0}, {1}, {2}", "zero", "one", "two"));

        assertEquals("§czero, §9one, two", format("{RED}{0}, {BLUE}{1}, {2}", "zero", "one", "two"));

        assertEquals("§czero, §9one§c, two", format("{RED}{0}, {1}, {2}", "zero", "§9one", "two"));

        assertEquals("§czero, §9one§c, two", format("{RED}{0}, {1}, {2}", "zero", "{BLUE}one", "two"));

        assertEquals("§c§lzero, §9one§c§l, two", format("{RED}{BOLD}{0}, {1}, {2}", "zero", "§9one", "two"));

        assertEquals("§zero", format("\\u00A7zero"));

        assertEquals("\\u00A7zero", format("\\\\u00A7zero"));

        assertEquals("\\§zero", format("\\\\\\u00A7zero"));

        assertEquals("zero\n one", format("zero\\n one"));

        assertEquals("zero\\n one", format("zero\\\\n one"));

        assertEquals("ZeroZeroOneZero", format("{0}{0}{1}{0}", "Zero", "One"));

        assertEquals("\"", format("{0}", "\""));

        assertEquals("§c§czero, §9§9one§9, two", format("{RED}{RED}{0}, {BLUE}{1}, {2}", "zero", "{BLUE}one", "two"));

        assertEquals("§c§czero, §9§9one§9, two", format("{RED}{RED}{0}, §9{1}, {2}", "zero", "§9one", "two"));

        assertEquals("§c§czero, §9, ", format("{RED}{RED}{0}, §9{1}, {2}", "zero", "", ""));

        assertEquals("§czero, §a", format("{RED}{0}, {GREEN}{1}", "zero", ""));
    }

    @Test
    public void testFormat1() throws Exception {

        TextFormatterSettings settings = new TextFormatterSettings().setEscaped('"');

        assertEquals("\\\"", format(settings, "\""));

        assertEquals("\\\"", format(settings, "{0}", "\""));
    }

    /**
     * Test Line Return Settings
     */
    @Test
    public void testFormat2() throws Exception {

        TextFormatterSettings settings = new TextFormatterSettings().setLineReturnPolicy(FormatPolicy.IGNORE);

        assertEquals("\\r", format(settings, "\\r"));

        assertEquals("\\n", format(settings, "\\n"));

        assertEquals("\\r", format(settings, "{0}", "\\r"));

        assertEquals("\\n", format(settings, "{0}", "\\n"));

        settings = new TextFormatterSettings().setLineReturnPolicy(FormatPolicy.REMOVE);

        assertEquals("", format(settings, "\\r"));

        assertEquals("", format(settings, "\\n"));

        assertEquals("", format(settings, "{0}", "\\r"));

        assertEquals("", format(settings, "{0}", "\\n"));
    }

    /**
     * Test Color Settings
     */
    @Test
    public void testFormat3() throws Exception {

        TextFormatterSettings settings = new TextFormatterSettings().setColorPolicy(FormatPolicy.IGNORE);

        assertEquals("{RED}redRed", format(settings, "{RED}red{0}", "Red"));

        assertEquals("{RED}red{RED}Red", format(settings, "{RED}red{0}", "{RED}Red"));

        settings = new TextFormatterSettings().setColorPolicy(FormatPolicy.REMOVE);

        assertEquals("redRed", format(settings, "{RED}red{0}", "Red"));

        assertEquals("redRed", format(settings, "{RED}red{0}", "{RED}Red"));
    }

    /**
     * Test Unicode Settings
     */
    @Test
    public void testFormat4() throws Exception {

        TextFormatterSettings settings = new TextFormatterSettings().setUnicodePolicy(FormatPolicy.IGNORE);

        assertEquals("\\u00A7unicodeTest", format(settings, "\\u00A7unicode{0}", "Test"));

        assertEquals("\\u00A7unicode\\u00A7Test", format(settings, "\\u00A7unicode{0}", "\\u00A7Test"));

        settings = new TextFormatterSettings().setUnicodePolicy(FormatPolicy.REMOVE);

        assertEquals("unicodeTest", format(settings, "\\u00A7unicode{0}", "Test"));

        assertEquals("unicodeTest", format(settings, "\\u00A7unicode{0}", "\\u00A7Test"));
    }

    /**
     * Test Argument Formatting Settings
     */
    @Test
    public void testFormat5() throws Exception {

        TextFormatterSettings settings = new TextFormatterSettings().setIsArgsFormatted(false);

        assertEquals("zero, one, two", format(settings, "{0}, {1}, {2}", "zero", "one", "two"));

        assertEquals("§czero, one, two", format(settings, "{RED}{0}, {1}, {2}", "zero", "one", "two"));

        assertEquals("§czero, §9one§c, two", format(settings, "{RED}{0}, {1}, {2}", "zero", "§9one", "two"));

        assertEquals("§czero, {BLUE}one, two", format(settings, "{RED}{0}, {1}, {2}", "zero", "{BLUE}one", "two"));

        assertEquals("§unicode\\u00A7Test", format(settings, "\\u00A7unicode{0}", "\\u00A7Test"));

        assertEquals("\n \\n", format(settings, "\\n {0}", "\\n"));


        settings.setEscaped('\"');

        assertEquals("\\\"\"", format(settings, "\"{0}", "\""));
    }

    /**
     * Test chat message arg
     */
    @Test
    public void testFormat6() throws Exception {

        IChatMessage message1 = TextUtils.format("{RED}red");

        assertEquals("§cred", format("{0}", message1));
        assertEquals("§cred§9 blue", format("{0}{BLUE} blue", message1));
        assertEquals("§9§cred§9 blue", format("{BLUE}{0} blue", message1));

    }

    /**
     * Test reset
     */
    @Test
    public void testFormat7() throws Exception {

        assertEquals("§ctest§r reset", format("{RED}test{RESET} reset"));

        assertEquals("§ctest§r reset", format("{RED}test§r reset"));

        assertEquals("§ctest§r reset", format("§ctest§r reset"));

        assertEquals("§ctest§r reset", format("§ctest{RESET} reset"));
    }

    private String format(TextFormatterSettings settings, String template, Object... args) {
        return TextUtils.format(settings, template, args).toString();
    }

    private String format(String template, Object... args) {
        return TextUtils.format(template, args).toString();
    }
}