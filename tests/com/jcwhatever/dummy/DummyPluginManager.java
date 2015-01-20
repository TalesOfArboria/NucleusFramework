package com.jcwhatever.dummy;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.UnknownDependencyException;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/*
 * 
 */
public class DummyPluginManager implements PluginManager {
    @Override
    public void registerInterface(Class<? extends PluginLoader> aClass) throws IllegalArgumentException {

    }

    @Override
    public Plugin getPlugin(String s) {
        if (s.equals("dummy"))
            return new DummyPlugin("dummy");

        return null;
    }

    @Override
    public Plugin[] getPlugins() {
        return new Plugin[] { new DummyPlugin("dummy") };
    }

    @Override
    public boolean isPluginEnabled(String s) {
        return false;
    }

    @Override
    public boolean isPluginEnabled(Plugin plugin) {
        return false;
    }

    @Override
    public Plugin loadPlugin(File file) throws InvalidPluginException, InvalidDescriptionException, UnknownDependencyException {
        return new DummyPlugin("dummy");
    }

    @Override
    public Plugin[] loadPlugins(File file) {
        return new Plugin[] { new DummyPlugin("dummy") };
    }

    @Override
    public void disablePlugins() {

    }

    @Override
    public void clearPlugins() {

    }

    @Override
    public void callEvent(Event event) throws IllegalStateException {

    }

    @Override
    public void registerEvents(Listener listener, Plugin plugin) {

    }

    @Override
    public void registerEvent(Class<? extends Event> aClass, Listener listener, EventPriority eventPriority, EventExecutor eventExecutor, Plugin plugin) {

    }

    @Override
    public void registerEvent(Class<? extends Event> aClass, Listener listener, EventPriority eventPriority, EventExecutor eventExecutor, Plugin plugin, boolean b) {

    }

    @Override
    public void enablePlugin(Plugin plugin) {

        if (plugin instanceof DummyPlugin)
            ((DummyPlugin) plugin).enable();

    }

    @Override
    public void disablePlugin(Plugin plugin) {

        if (plugin instanceof DummyPlugin)
            ((DummyPlugin) plugin).disable();

    }

    @Override
    public Permission getPermission(String s) {
        return null;
    }

    @Override
    public void addPermission(Permission permission) {

    }

    @Override
    public void removePermission(Permission permission) {

    }

    @Override
    public void removePermission(String s) {

    }

    @Override
    public Set<Permission> getDefaultPermissions(boolean b) {
        return new HashSet<>(0);
    }

    @Override
    public void recalculatePermissionDefaults(Permission permission) {

    }

    @Override
    public void subscribeToPermission(String s, Permissible permissible) {

    }

    @Override
    public void unsubscribeFromPermission(String s, Permissible permissible) {

    }

    @Override
    public Set<Permissible> getPermissionSubscriptions(String s) {
        return new HashSet<>(0);
    }

    @Override
    public void subscribeToDefaultPerms(boolean b, Permissible permissible) {

    }

    @Override
    public void unsubscribeFromDefaultPerms(boolean b, Permissible permissible) {

    }

    @Override
    public Set<Permissible> getDefaultPermSubscriptions(boolean b) {
        return new HashSet<>(0);
    }

    @Override
    public Set<Permission> getPermissions() {
        return new HashSet<>(0);
    }

    @Override
    public boolean useTimings() {
        return false;
    }
}
