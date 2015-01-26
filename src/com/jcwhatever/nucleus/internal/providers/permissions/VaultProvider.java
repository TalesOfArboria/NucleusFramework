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


package com.jcwhatever.nucleus.internal.providers.permissions;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.providers.permissions.IPermissionsProvider;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.permission.Permission;

import javax.annotation.Nullable;

/**
 * Vault interface implementation.
 */
public class VaultProvider extends AbstractPermissionsProvider {

    protected Permission _perms = null;

    public static boolean hasVaultPermissions() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Vault");
        if (plugin == null)
            return false;

        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager()
                .getRegistration(net.milkbowl.vault.permission.Permission.class);

        return permissionProvider != null && permissionProvider.getProvider() != null;
    }

    public static IPermissionsProvider getVaultProvider() {

        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager()
                .getRegistration(net.milkbowl.vault.permission.Permission.class);

        if (permissionProvider == null)
            throw new RuntimeException("Vault permissions not found.");


        Permission perms = permissionProvider.getProvider();

        return perms.hasGroupSupport() ? new VaultGroupProvider() : new VaultProvider();
    }

    /**
     * Constructor.
     */
    public VaultProvider() {
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
        return Nucleus.getPlugin().getDescription().getVersion();
    }

    @Override
    public int getLogicalVersion() {
        return 0;
    }

    @Override
    public void onRegister() {
        // do nothing
    }

    @Override
    public void onEnable() {
        // do nothing
    }

    @Override
    public void onDisable() {
        // do nothing
    }

    @Override
    public boolean has(CommandSender sender, String permissionName) {
        return _perms.has(sender, permissionName);
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
    public boolean remove(Plugin plugin, CommandSender sender, String permissionName) {
        Player p = getPlayer(sender);
        return p != null && _perms.playerRemove(p, permissionName);
    }

    @Nullable
    @Override
    public Object getHandle() {
        return _perms;
    }

    @Nullable
    protected Player getPlayer(CommandSender sender) {
        if (!(sender instanceof Player))
            return null;

        return (Player)sender;
    }
}
