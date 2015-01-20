package com.jcwhatever.nucleus.utils;

import static org.junit.Assert.assertEquals;

import com.jcwhatever.dummy.DummyPlugin;
import com.jcwhatever.nucleus.NucleusInit;

import org.junit.Test;

import java.util.regex.Matcher;

public class NmsUtilsTest {

    @Test
    public void testPatternVersion() {

        String testVersion = "net.minecraft.server.v1_8_R1.";


        Matcher matcher = NmsUtils.PATTERN_VERSION.matcher(testVersion);
        assertEquals(true, matcher.matches());
        assertEquals("v1_8_R1", matcher.group(1));
    }

    @Test
    public void testGetNmsVersion() throws Exception {

        assertEquals(NucleusInit.NMS_TEST_VERSION, NmsUtils.getNmsVersion());
    }

    @Test
    public void testIsVersionCompatible() throws Exception {

        assertEquals(true, NmsUtils.isVersionCompatible("v1_8_R1"));
        assertEquals(false, NmsUtils.isVersionCompatible("v1_8_R2"));
        assertEquals(false, NmsUtils.isVersionCompatible("v1_9_R1"));
        assertEquals(false, NmsUtils.isVersionCompatible("v2_8_R1"));
        assertEquals(false, NmsUtils.isVersionCompatible("1_8_R1"));

    }

    @Test
    public void testEnforceNmsVersion() throws Exception {

        NucleusInit.init();

        DummyPlugin plugin = new DummyPlugin("dummy").enable();

        NmsUtils.enforceNmsVersion(plugin, "a");

        assertEquals(false, plugin.isEnabled());

    }
}