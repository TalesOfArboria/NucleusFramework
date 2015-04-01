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


package com.jcwhatever.nucleus.internal.providers.permissions.vault;

import com.jcwhatever.nucleus.internal.providers.InternalProviderInfo;
import com.jcwhatever.nucleus.internal.providers.permissions.AbstractPermissionsProvider;
import com.jcwhatever.nucleus.providers.permissions.IPermissionsProvider;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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

    public static final String NAME = "VaultPermissions";

    protected Permission _perms = null;

    /**
     * Determine if a Vault permissions provider is installed.
     */
    public static boolean hasVaultPermissions() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Vault");
        if (plugin == null)
            return false;

        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager()
                .getRegistration(net.milkbowl.vault.permission.Permission.class);

        return permissionProvider != null && permissionProvider.getProvider() != null;
    }

    /**
     * Create a new instance of a {@link VaultProvider}.
     *
     * <p>Detects if the Vault provider supports groups and returns the
     * appropriate implementation.</p>
     */
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

        setInfo(new InternalProviderInfo(this.getClass(),
                NAME, "Default Vault permissions provider."));

        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager()
                .getRegistration(net.milkbowl.vault.permission.Permission.class);

        if (permissionProvider != null) {
            _perms = permissionProvider.getProvider();
        }
    }

    @Override
    public boolean has(OfflinePlayer player, String permissionName) {
        PreCon.notNull(player);
        PreCon.notNull(permissionName);

        return player instanceof Player && _perms.has((Player) player, permissionName);
    }

    @Override
    public boolean addTransient(Plugin plugin, Player player, String permissionName) {
        PreCon.notNull(plugin);
        PreCon.notNull(player);
        PreCon.notNull(permissionName);

        return _perms.playerAddTransient(player, permissionName);
    }

    @Override
    public boolean removeTransient(Plugin plugin, Player player, String permissionName) {
        PreCon.notNull(plugin);
        PreCon.notNull(player);
        PreCon.notNull(permissionName);

        return _perms.playerRemoveTransient(player, permissionName);
    }

    @Override
    public boolean add(Plugin plugin, OfflinePlayer player, String permissionName) {
        PreCon.notNull(plugin);
        PreCon.notNull(player);
        PreCon.notNull(permissionName);

        return player instanceof Player && _perms.playerAdd((Player) player, permissionName);
    }

    @Override
    public boolean remove(Plugin plugin, OfflinePlayer player, String permissionName) {
        PreCon.notNull(plugin);
        PreCon.notNull(player);
        PreCon.notNull(permissionName);

        return player instanceof Player && _perms.playerRemove((Player) player, permissionName);
    }

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
