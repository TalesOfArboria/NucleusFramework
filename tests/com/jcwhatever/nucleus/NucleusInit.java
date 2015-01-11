package com.jcwhatever.nucleus;

import com.jcwhatever.dummy.DummyServer;
import com.jcwhatever.nucleus.storage.DataStorageUtil;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper to initialize Nucleus and Bukkit for testing.
 */
public class NucleusInit {

    private static boolean _isInit;

    public static void init() {

        if (_isInit)
            return;

        _isInit = true;

        try {
            Bukkit.setServer(new DummyServer());
        }
        catch (UnsupportedOperationException ignore) {}

        DataStorageUtil.setTestMode();

        PluginDescriptionFile descriptionFile = new PluginDescriptionFile("dummy", "v0", "");

        Map<String, Object> descriptionMap = new HashMap<>(10);
        descriptionMap.put("version", "v0");
        descriptionMap.put("name", "dummy");
        descriptionMap.put("main", "");
        descriptionMap.put("commands", new HashMap(0));

        try {
            Method method = descriptionFile.getClass().getDeclaredMethod("loadMap", Map.class);
            method.setAccessible(true);

            method.invoke(descriptionFile, descriptionMap);

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        BukkitPlugin plugin = new BukkitPlugin(new JavaPluginLoader(Bukkit.getServer()), descriptionFile,
                new File(""), new File(""));

        plugin.onEnable();

    }

}
