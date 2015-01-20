package com.jcwhatever.nucleus;

import com.jcwhatever.bukkit.BukkitTest;
import com.jcwhatever.nucleus.storage.DataStorageUtil;

/**
 * Helper to initialize Nucleus and Bukkit for testing.
 */
public class NucleusTest {

    public static void init() {

        if (!BukkitTest.init())
            return;

        DataStorageUtil.setTestMode();

        BukkitTest.initPlugin("NucleusFramework", "v0", BukkitPlugin.class);
    }
}
