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

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Vault interface implementation.
 */
public class VaultPermissionsHandler extends AbstractPermissionsHandler {

    private Permission _perms = null;

    /**
     * Constructor.
     */
    VaultPermissionsHandler() {
        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager()
                .getRegistration(net.milkbowl.vault.permission.Permission.class);

        if (permissionProvider != null) {
            _perms = permissionProvider.getProvider();
        }
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
    public boolean has(Player p, String permissionName) {
        return _perms.has(p, permissionName);
    }

    @Override
    public boolean has(Player p, World world, String permissionName) {
        return _perms.has(p.getWorld(), p.getName(), permissionName);
    }

    @Override
    public boolean addTransient(Plugin plugin, Player p, String permissionName) {
        return _perms.playerAddTransient(p, permissionName);
    }

    @Override
    public boolean removeTransient(Plugin plugin, Player p, String permissionName) {
        return _perms.playerRemoveTransient(p, permissionName);
    }

    @Override
    public boolean add(Plugin plugin, Player p, String permissionName) {
        return _perms.playerAdd(p, permissionName);
    }

    @Override
    public boolean add(Plugin plugin, Player p, World world, String permissionName) {
        return _perms.playerAdd(world, p.getName(), permissionName);
    }

    @Override
    public boolean remove(Plugin plugin, Player p, String permissionName) {
        return _perms.playerRemove(p, permissionName);
    }

    @Override
    public boolean remove(Plugin plugin, Player p, World world,	String permissionName) {
        return _perms.playerRemove(world, p.getName(), permissionName);
    }

    @Override
    public boolean addGroup(Plugin plugin, Player p, String groupName) {
        return _perms.playerAddGroup(p, groupName);
    }

    @Override
    public boolean addGroup(Plugin plugin, Player p, World world, String groupName) {
        return _perms.playerAddGroup(world, p.getName(), groupName);
    }

    @Override
    public boolean removeGroup(Plugin plugin, Player p, String groupName) {
        return _perms.playerRemoveGroup(p, groupName);
    }

    @Override
    public boolean removeGroup(Plugin plugin, Player p, World world, String groupName) {
        return _perms.playerRemoveGroup(world, p.getName(), groupName);
    }

    @Override
    public String[] getGroups() {
        return _perms.getGroups();
    }

    @Override
    public String[] getGroups(Player p) {
        return _perms.getPlayerGroups(p);
    }

    @Override
    public String[] getGroups(Player p, World world) {
        return _perms.getPlayerGroups(world, p.getName());
    }

}
