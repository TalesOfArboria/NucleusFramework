/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.generic.internal.providers.permissions;

import com.jcwhatever.generic.GenericsLib;
import com.jcwhatever.generic.utils.ArrayUtils;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.permission.Permission;

import javax.annotation.Nullable;

/**
 * Vault interface implementation.
 */
public final class VaultPermissionsProvider extends AbstractPermissionsProvider {

    private Permission _perms = null;

    /**
     * Constructor.
     */
    public VaultPermissionsProvider() {
        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager()
                .getRegistration(net.milkbowl.vault.permission.Permission.class);

        if (permissionProvider != null) {
            _perms = permissionProvider.getProvider();
        }
    }

    @Override
    public String getName() {
        return "Vault";
    }

    @Override
    public String getVersion() {
        return GenericsLib.getPlugin().getDescription().getVersion();
    }

    @Override
    public int getLogicalVersion() {
        return 0;
    }

    @Override
    public boolean hasGroupSupport() {
        return _perms.hasGroupSupport();
    }

    @Override
    public boolean hasWorldSupport() {
        return true;
    }

    @Override
    public boolean has(CommandSender sender, String permissionName) {
        return _perms.has(sender, permissionName);
    }

    @Override
    public boolean has(CommandSender sender, World world, String permissionName) {
        Player p = getPlayer(sender);
        return p == null || _perms.has(p.getWorld(), sender.getName(), permissionName);
    }

    @Override
    public boolean addTransient(Plugin plugin, CommandSender sender, String permissionName) {
        Player p = getPlayer(sender);
        return p != null && _perms.playerAddTransient(p, permissionName);
    }

    @Override
    public boolean removeTransient(Plugin plugin, CommandSender sender, String permissionName) {
        Player p = getPlayer(sender);
        return p != null && _perms.playerRemoveTransient(p, permissionName);
    }

    @Override
    public boolean add(Plugin plugin, CommandSender sender, String permissionName) {
        Player p = getPlayer(sender);
        return p != null && _perms.playerAdd(p, permissionName);
    }

    @Override
    public boolean add(Plugin plugin, CommandSender sender, World world, String permissionName) {
        Player p = getPlayer(sender);
        return p != null && _perms.playerAdd(world, p.getName(), permissionName);
    }

    @Override
    public boolean remove(Plugin plugin, CommandSender sender, String permissionName) {
        Player p = getPlayer(sender);
        return p != null && _perms.playerRemove(p, permissionName);
    }

    @Override
    public boolean remove(Plugin plugin, CommandSender sender, World world,	String permissionName) {
        Player p = getPlayer(sender);
        return p != null && _perms.playerRemove(world, p.getName(), permissionName);
    }

    @Override
    public boolean addGroup(Plugin plugin, CommandSender sender, String groupName) {
        Player p = getPlayer(sender);
        return p != null && _perms.playerAddGroup(p, groupName);
    }

    @Override
    public boolean addGroup(Plugin plugin, CommandSender sender, World world, String groupName) {
        Player p = getPlayer(sender);
        return p != null && _perms.playerAddGroup(world, p.getName(), groupName);
    }

    @Override
    public boolean removeGroup(Plugin plugin, CommandSender sender, String groupName) {
        Player p = getPlayer(sender);
        return p != null && _perms.playerRemoveGroup(p, groupName);
    }

    @Override
    public boolean removeGroup(Plugin plugin, CommandSender sender, World world, String groupName) {
        return _perms.playerRemoveGroup(world, sender.getName(), groupName);
    }

    @Override
    public String[] getGroups() {
        return _perms.getGroups();
    }

    @Override
    public String[] getGroups(CommandSender sender) {
        Player p = getPlayer(sender);
        if (p == null)
            return ArrayUtils.EMPTY_STRING_ARRAY;

        return _perms.getPlayerGroups(p);
    }

    @Override
    public String[] getGroups(CommandSender sender, World world) {
        return _perms.getPlayerGroups(world, sender.getName());
    }

    @Nullable
    private Player getPlayer(CommandSender sender) {
        if (!(sender instanceof Player))
            return null;

        return (Player)sender;
    }
}
