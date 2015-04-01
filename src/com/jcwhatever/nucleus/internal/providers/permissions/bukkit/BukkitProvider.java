/*
 * This file is part of NucleusFramework for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.nucleus.internal.providers.permissions.bukkit;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.collections.players.PlayerMap;
import com.jcwhatever.nucleus.internal.providers.InternalProviderInfo;
import com.jcwhatever.nucleus.internal.providers.permissions.AbstractPermissionsProvider;
import com.jcwhatever.nucleus.providers.permissions.IPermission;
import com.jcwhatever.nucleus.storage.DataPath;
import com.jcwhatever.nucleus.storage.DataStorage;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.Permissions;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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
import java.util.UUID;

/**
 * Basic Bukkit permissions handler
 */
public final class BukkitProvider extends AbstractPermissionsProvider {

    public static final String NAME = "NucleusBukkitPerms";

    private final Map<UUID, PermissionAttachment> _transient = new PlayerMap<>(Nucleus.getPlugin());
    private final IDataNode _dataNode;

    /**
     * Constructor.
     */
    public BukkitProvider() {

        setInfo(new InternalProviderInfo(this.getClass(),
                NAME, "Default Bukkit permissions provider."));

        // get permissions data node
        _dataNode = DataStorage.get(Nucleus.getPlugin(), new DataPath("bukkit-permissions"));
        _dataNode.load();

        Bukkit.getPluginManager().registerEvents(new PermissionListener(this), Nucleus.getPlugin());
    }

    @Override
    public boolean has(OfflinePlayer player, String permissionName) {
        PreCon.notNull(player);
        PreCon.notNullOrEmpty(permissionName);

        return player instanceof Player && ((Player) player).hasPermission(permissionName);
    }

    @Override
    public boolean addTransient(Plugin plugin, Player player, String permissionName) {
        PreCon.notNull(plugin);
        PreCon.notNull(player);
        PreCon.notNullOrEmpty(permissionName);

        IPermission perm = Permissions.get(permissionName);
        if (perm == null)
            return false;

        PermissionAttachment attachment = _transient.get(player.getUniqueId());
        if (attachment == null) {
            attachment = player.addAttachment(plugin);
            _transient.put(player.getUniqueId(), attachment);
        }

        assert perm.getHandle() != null;
        attachment.setPermission((Permission)perm.getHandle(), true);

        return true;
    }

    @Override
    public boolean removeTransient(Plugin plugin, Player player, String permissionName) {
        PreCon.notNull(plugin);
        PreCon.notNull(player);
        PreCon.notNullOrEmpty(permissionName);

        IPermission perm = Permissions.get(permissionName);
        if (perm == null)
            return false;

        PermissionAttachment attachment = _transient.get(player.getUniqueId());
        if (attachment == null) {
            return false;
        }

        assert perm.getHandle() != null;
        attachment.setPermission((Permission) perm.getHandle(), false);

        return true;
    }

    @Override
    public boolean add(Plugin plugin, OfflinePlayer player, String permissionName) {
        PreCon.notNull(plugin);
        PreCon.notNull(player);
        PreCon.notNullOrEmpty(permissionName);

        String pid = player.getUniqueId().toString() + '.' + plugin.getName();

        List<String> permissions = _dataNode.getStringList(pid, null);
        if (permissions == null)
            permissions = new ArrayList<String>(25);

        permissions.add(permissionName);

        _dataNode.set(pid, permissions);
        _dataNode.save();

        return !(player instanceof Player) || addTransient(plugin, (Player) player, permissionName);
    }

    @Override
    public boolean remove(Plugin plugin, OfflinePlayer player, String permissionName) {
        PreCon.notNull(plugin);
        PreCon.notNull(player);
        PreCon.notNullOrEmpty(permissionName);

        String pid = player.getUniqueId().toString() + '.' + plugin.getName();

        List<String> permissions = _dataNode.getStringList(pid, null);
        if (permissions == null)
            permissions = new ArrayList<String>(30);

        permissions.remove(permissionName);

        _dataNode.set(pid, permissions);
        _dataNode.save();

        return !(player instanceof Player) || removeTransient(plugin, (Player) player, permissionName);
    }

    @Override
    public Object getHandle() {
        return this;
    }

    /**
     * Bukkit event listener
     */
    private static class PermissionListener implements Listener {

        private BukkitProvider _provider;

        PermissionListener(BukkitProvider provider) {
            _provider = provider;
        }

        /**
         * Add permissions on player join.
         */
        @EventHandler(priority= EventPriority.LOW)
        private void onPlayerJoin(PlayerJoinEvent event) {

            Player p = event.getPlayer();

            // give permissions

            // get players permission data node
            IDataNode playerNode = _provider._dataNode.getNode(p.getUniqueId().toString());

            // get the names of the plugins that have set permissions on the player.
            for (IDataNode node : playerNode) {

                // make sure the plugin is loaded.
                Plugin plugin = Bukkit.getPluginManager().getPlugin(node.getName());
                if (plugin == null)
                    continue;

                // get the list of permissions the player has.
                List<String> permissions = playerNode.getStringList(plugin.getName(), null);

                if (permissions == null || permissions.isEmpty())
                    continue;

                // add permissions to player
                for (String permission : permissions) {
                    // add permissions as transient to prevent re-saving them
                    _provider.addTransient(plugin, p, permission);
                }
            }
        }
    }
}
