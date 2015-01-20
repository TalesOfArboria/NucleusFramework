package com.jcwhatever.nucleus;

import com.jcwhatever.bukkit.v1_8_R1.BukkitTest;
import com.jcwhatever.nucleus.storage.DataStorageUtil;

/**
 * Helper to initialize Nucleus and Bukkit for testing.
 */
public class NucleusTest {

    public static void init() {

        if (!BukkitTest.init())
            return;

        DataStorageUtil.setTestMode();

        BukkitPlugin plugin = BukkitTest.initPlugin("NucleusFramework", "v0", BukkitPlugin.class);

        BukkitTest.getServer().getPluginManager().enablePlugin(plugin);
    }
}
