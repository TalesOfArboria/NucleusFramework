/* This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
 *
 * Copyright (c) JCThePants (www.jcwhatever.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


package com.jcwhatever.bukkit.generic.permissions;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.player.collections.PlayerMap;
import com.jcwhatever.bukkit.generic.storage.DataStorage;
import com.jcwhatever.bukkit.generic.storage.DataStorage.DataPath;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Basic Bukkit permissions handler
 */
public class BukkitPermissionsHandler extends AbstractPermissionsHandler {

    private static Map<UUID, PermissionAttachment> _transient = new PlayerMap<PermissionAttachment>();
    private static Listener _bukkitListener;

    private IDataNode _dataNode;

    /**
     * Constructor.
     */
    BukkitPermissionsHandler() {

        // get permissions data node
        _dataNode = DataStorage.getStorage(GenericsLib.getPlugin(), new DataPath("bukkit-permissions"));
        _dataNode.load();

        // initialize bukkit listener if its not already initialized
        if (_bukkitListener == null) {
            _bukkitListener = new PermissionListener();
            Bukkit.getPluginManager().registerEvents(_bukkitListener, GenericsLib.getPlugin());
        }
    }

    @Override
    public boolean hasGroupSupport() {
        return false;
    }

    @Override
    public boolean hasWorldSupport() {
        return false;
    }

    @Override
    public boolean has(Player p, String permissionName) {
        PreCon.notNull(p);
        PreCon.notNullOrEmpty(permissionName);

        return p.hasPermission(permissionName);
    }

    @Override
    public boolean has(Player p, World world, String permissionName) {
        PreCon.notNull(p);
        PreCon.notNull(world);
        PreCon.notNullOrEmpty(permissionName);

        return p.hasPermission(permissionName);
    }

    @Override
    public boolean addTransient(Plugin plugin, Player p, String permissionName) {
        PreCon.notNull(plugin);
        PreCon.notNull(p);
        PreCon.notNullOrEmpty(permissionName);

        IPermission perm = Permissions.get(permissionName);
        if (perm == null)
            return false;

        PermissionAttachment attachment = _transient.get(p.getUniqueId());
        if (attachment == null) {
            attachment = p.addAttachment(plugin);
            _transient.put(p.getUniqueId(), attachment);
        }

        attachment.setPermission((Permission)perm.getHandle(), true);

        return true;
    }

    @Override
    public boolean removeTransient(Plugin plugin, Player p, String permissionName) {
        PreCon.notNull(plugin);
        PreCon.notNull(p);
        PreCon.notNullOrEmpty(permissionName);

        IPermission perm = Permissions.get(permissionName);
        if (perm == null)
            return false;

        PermissionAttachment attachment = _transient.get(p.getUniqueId());
        if (attachment == null) {
            return false;
        }

        attachment.setPermission((Permission)perm.getHandle(), false);

        return true;
    }

    @Override
    public boolean add(Plugin plugin, Player p, String permissionName) {
        PreCon.notNull(plugin);
        PreCon.notNull(p);
        PreCon.notNullOrEmpty(permissionName);

        String pid = p.getUniqueId().toString() + '.' + plugin.getName();

        List<String> permissions = _dataNode.getStringList(pid, null);
        if (permissions == null)
            permissions = new ArrayList<String>(30);

        permissions.add(permissionName);

        _dataNode.set(pid, permissions);
        _dataNode.saveAsync(null);

        return addTransient(plugin, p, permissionName);
    }

    @Override
    public boolean add(Plugin plugin, Player p, World world, String permissionName) {
        PreCon.notNull(plugin);
        PreCon.notNull(p);
        PreCon.notNull(world);
        PreCon.notNullOrEmpty(permissionName);

        return add(plugin, p, permissionName);
    }

    @Override
    public boolean remove(Plugin plugin, Player p, String permissionName) {
        PreCon.notNull(plugin);
        PreCon.notNull(p);
        PreCon.notNullOrEmpty(permissionName);

        String pid = p.getUniqueId().toString() + '.' + plugin.getName();

        List<String> permissions = _dataNode.getStringList(pid, null);
        if (permissions == null)
            permissions = new ArrayList<String>(30);

        permissions.remove(permissionName);

        _dataNode.set(pid, permissions);

        return removeTransient(plugin, p, permissionName);
    }

    @Override
    public boolean remove(Plugin plugin, Player p, World world,	String permissionName) {
        PreCon.notNull(plugin);
        PreCon.notNull(p);
        PreCon.notNull(world);
        PreCon.notNullOrEmpty(permissionName);

        return remove(plugin, p, permissionName);
    }

    @Override
    public boolean addGroup(Plugin plugin, Player p, String groupName) {
        return false;
    }

    @Override
    public boolean addGroup(Plugin plugin, Player p, World world, String groupName) {
        return false;
    }

    @Override
    public boolean removeGroup(Plugin plugin, Player p, String groupName) {
        return false;
    }

    @Override
    public boolean removeGroup(Plugin plugin, Player p, World world, String groupName) {
        return false;
    }

    @Override
    public String[] getGroups() {
        return null;
    }

    @Override
    public String[] getGroups(Player p) {
        return null;
    }

    @Override
    public String[] getGroups(Player p, World world) {
        return null;
    }

    /**
     * Bukkit event listener
     */
    private static class PermissionListener implements Listener {

        /**
         * Add permissions on player join.
         */
        @EventHandler(priority= EventPriority.LOW)
        private void onPlayerJoin(PlayerJoinEvent event) {

            Player p = event.getPlayer();

            // give permissions
            if (Permissions.getImplementation() instanceof BukkitPermissionsHandler) {
                BukkitPermissionsHandler perms = (BukkitPermissionsHandler)Permissions.getImplementation();

                // get players permission data node
                IDataNode playerNode = perms._dataNode.getNode(p.getUniqueId().toString());

                // get the names of the plugins that have set permissions on the player.
                Set<String> pluginNames = playerNode.getSubNodeNames();

                for (String pluginName : pluginNames) {

                    // make sure the plugin is loaded.
                    Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
                    if (plugin == null)
                        continue;

                    // get the list of permissions the player has.
                    List<String> permissions = playerNode.getStringList(pluginName, null);

                    if (permissions == null || permissions.isEmpty())
                        continue;

                    // add permissions to player
                    for (String permission : permissions) {
                        // add permissions as transient to prevent re-saving them
                        Permissions.addTransient(plugin, p, permission);
                    }
                }

            }
        }

    }

}
