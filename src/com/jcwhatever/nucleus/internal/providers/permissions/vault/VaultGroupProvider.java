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

import com.jcwhatever.nucleus.collections.wrap.WrappedArrayList;
import com.jcwhatever.nucleus.providers.permissions.IGroupPermissionsProvider;
import com.jcwhatever.nucleus.providers.permissions.IPermissionGroup;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;

/**
 * Vault group permissions provider implementation.
 */
public final class VaultGroupProvider extends VaultProvider implements IGroupPermissionsProvider {

    @Override
    public boolean addGroup(Plugin plugin, OfflinePlayer player, String groupName) {
        PreCon.notNull(plugin);
        PreCon.notNull(player);
        PreCon.notNull(groupName);

        return player instanceof Player && _perms.playerAddGroup((Player) player, groupName);
    }

    @Override
    public boolean removeGroup(Plugin plugin, OfflinePlayer player, String groupName) {
        PreCon.notNull(plugin);
        PreCon.notNull(player);
        PreCon.notNull(groupName);

        return player instanceof Player && _perms.playerRemoveGroup((Player) player, groupName);
    }

    @Override
    public Collection<IPermissionGroup> getGroups() {

        String[] groupNames = _perms.getGroups();

        IPermissionGroup[] array = new IPermissionGroup[groupNames.length];

        for (int i=0; i < array.length; i++) {
            array[i] = new VaultGroupPermission(groupNames[i]);
        }

        return new WrappedArrayList<>(array);
    }

    @Override
    public Collection<IPermissionGroup> getGroups(OfflinePlayer player) {
        PreCon.notNull(player);

        if (!(player instanceof Player))
            return new WrappedArrayList<>(new IPermissionGroup[0]);

        String[] groupNames = _perms.getPlayerGroups((Player)player);

        IPermissionGroup[] array = new IPermissionGroup[groupNames.length];

        for (int i=0; i < array.length; i++) {
            array[i] = new VaultGroupPermission(groupNames[i]);
        }

        return new WrappedArrayList<>(array);
    }
}
