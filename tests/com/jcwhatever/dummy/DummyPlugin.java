package com.jcwhatever.dummy;

import com.avaje.ebean.EbeanServer;
import com.jcwhatever.dummy.v1_8_R1.DummyServer;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/*
 * 
 */
public class DummyPlugin implements Plugin {

    private String _name;
    private PluginDescriptionFile _description;
    private boolean _isEnabled;

    public DummyPlugin(String name) {
        _name = name;

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

        _description = descriptionFile;
    }

    @Override
    public File getDataFolder() {
        return null;
    }

    @Override
    public PluginDescriptionFile getDescription() {
        return _description;
    }

    @Override
    public FileConfiguration getConfig() {
        return null;
    }

    @Override
    public InputStream getResource(String s) {
        return null;
    }

    @Override
    public void saveConfig() {

    }

    @Override
    public void saveDefaultConfig() {

    }

    @Override
    public void saveResource(String s, boolean b) {

    }

    @Override
    public void reloadConfig() {

    }

    @Override
    public PluginLoader getPluginLoader() {
        return null;
    }

    @Override
    public Server getServer() {

        if (Bukkit.getServer() == null) {
            Bukkit.setServer(new DummyServer());
        }

        return Bukkit.getServer();
    }

    @Override
    public boolean isEnabled() {
        return _isEnabled;
    }

    public DummyPlugin enable() {
        _isEnabled = true;
        return this;
    }

    public DummyPlugin disable() {
        _isEnabled = false;
        return this;
    }

    @Override
    public void onDisable() {
        _isEnabled = false;
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        _isEnabled = true;
    }

    @Override
    public boolean isNaggable() {
        return false;
    }

    @Override
    public void setNaggable(boolean b) {

    }

    @Override
    public EbeanServer getDatabase() {
        return null;
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String s, String s1) {
        return null;
    }

    @Override
    public Logger getLogger() {
        return null;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
