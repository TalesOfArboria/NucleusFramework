package com.jcwhatever.nucleus;

import com.jcwhatever.dummy.v1_8_R1.DummyServer;
import com.jcwhatever.nucleus.storage.DataStorageUtil;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R1.scheduler.CraftScheduler;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
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

    public static final String NMS_TEST_VERSION = "v1_8_R1";

    private static boolean _isInit;
    private static long _nextHeartBeat;
    private static int _currentTick = 0;

    public static void heartBeat() {
        if (!_isInit || System.currentTimeMillis() < _nextHeartBeat)
            return;

        ((CraftScheduler)Bukkit.getServer().getScheduler()).mainThreadHeartbeat(_currentTick);
        _currentTick++;

        _nextHeartBeat = System.currentTimeMillis() + 50;
    }

    public static void init() {

        _currentTick = 0;
        _nextHeartBeat = 0;

        if (_isInit) {
            return;
        }

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


        try {
            Method method = JavaPlugin.class.getDeclaredMethod("setEnabled", boolean.class);
            method.setAccessible(true);

            method.invoke(plugin, true);

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        plugin.onEnable();
    }

}
