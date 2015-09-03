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

package com.jcwhatever.nucleus.internal.managed.scripting.api.views;

import com.jcwhatever.nucleus.collections.WeakHashSet;
import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.items.ItemFilter;
import com.jcwhatever.nucleus.utils.items.ItemStackUtils;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;
import com.jcwhatever.nucleus.views.ViewSession;
import com.jcwhatever.nucleus.views.menu.MenuItemBuilder;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Set;

public class SAPI_Views implements IDisposable {

    private final Plugin _plugin;
    private final Set<IDisposable> _views = new WeakHashSet<>(10);
    private boolean _isDisposed;

    public SAPI_Views(Plugin plugin) {
        PreCon.notNull(plugin);

        _plugin = plugin;
    }

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {

        for (IDisposable view : _views) {
            view.dispose();
        }
        _views.clear();
        _isDisposed = true;
    }

    /**
     * Create a new {@link MenuItemBuilder}.
     *
     * @param itemStack  The {@link ItemStack} that will be used to represent the item in a menu inventory.
     *                   The argument can be an {@link org.bukkit.inventory.ItemStack},
     *                   {@link org.bukkit.Material}, {@link org.bukkit.material.MaterialData}, the name or
     *                   alternate of the material or a serialized item stack string.
     */
    public MenuItemBuilder menuItemBuilder(Object itemStack) {
        PreCon.notNull(itemStack);

        ItemStack stack = ItemStackUtils.getItemStack(itemStack);
        PreCon.isValid(stack != null, "Invalid itemStack. Must be an ItemStack, " +
                "material name, serializable item stack string, Material, or MaterialData.");

        return new MenuItemBuilder(stack);
    }

    /**
     * Create a new menu view.
     *
     * @param player  The {@link org.bukkit.entity.Player} the view is for. Argument can also be
     *                the player name, the players UUID or an
     *                {@link com.jcwhatever.nucleus.mixins.IPlayerReference} instance.
     * @param title   The views title.
     */
    public ScriptMenuView createMenu(Object player, String title) {
        PreCon.notNull(player);
        PreCon.notNull(title);

        Player p = PlayerUtils.getPlayer(player);
        PreCon.isValid(p != null, "Invalid player object.");

        ScriptMenuView menuView = new ScriptMenuView(_plugin, p, title);
        _views.add(menuView);

        return menuView;
    }

    /**
     * Create a new workbench view.
     *
     * @param player  The {@link org.bukkit.entity.Player} the view is for. Argument can also be
     *                the player name, the players UUID or an
     *                {@link com.jcwhatever.nucleus.mixins.IPlayerReference} instance.
     */
    public ScriptWorkbenchView createWorkbench(Object player) {
        PreCon.notNull(player);

        Player p = PlayerUtils.getPlayer(player);
        PreCon.isValid(p != null, "Invalid player object.");

        ScriptWorkbenchView view = new ScriptWorkbenchView(_plugin, p, new ItemFilter(_plugin, null));
        _views.add(view);

        return view;
    }

    /**
     * Create a new anvil view.
     *
     * @param player  The {@link org.bukkit.entity.Player} the view is for. Argument can also be
     *                the player name, the players UUID or an
     *                {@link com.jcwhatever.nucleus.mixins.IPlayerReference} instance.
     */
    public ScriptAnvilView createAnvil(Object player) {
        PreCon.notNull(player);

        Player p = PlayerUtils.getPlayer(player);
        PreCon.isValid(p != null, "Invalid player object.");

        ScriptAnvilView view = new ScriptAnvilView(_plugin, p, new ItemFilter(_plugin, null));
        _views.add(view);

        return view;
    }

    /**
     * Determine if a player is currently viewing an artificial inventory view.
     *
     * @param player  The {@link org.bukkit.entity.Player} to check. Argument can also be
     *                the player name, the players UUID or an
     *                {@link com.jcwhatever.nucleus.mixins.IPlayerReference} instance.
     */
    public boolean isViewing(Object player) {
        PreCon.notNull(player);

        Player p = PlayerUtils.getPlayer(player);
        PreCon.isValid(p != null, "Invalid player object.");

        ViewSession session = ViewSession.getCurrent(p);
        return session != null && session.getCurrent() != null;
    }
}
