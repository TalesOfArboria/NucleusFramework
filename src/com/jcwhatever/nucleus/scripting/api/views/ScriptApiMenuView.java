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

package com.jcwhatever.nucleus.scripting.api.views;

import com.jcwhatever.nucleus.collections.WeakHashSet;
import com.jcwhatever.nucleus.scripting.IEvaluatedScript;
import com.jcwhatever.nucleus.scripting.ScriptApiInfo;
import com.jcwhatever.nucleus.scripting.api.IScriptApiObject;
import com.jcwhatever.nucleus.scripting.api.NucleusScriptApi;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.items.ItemStackUtils;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;
import com.jcwhatever.nucleus.views.menu.MenuItemBuilder;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Set;

@ScriptApiInfo(
        variableName = "menus",
        description = "Add NucleusFramework menu view scripting API support.")
public class ScriptApiMenuView extends NucleusScriptApi {

    /**
     * Constructor.
     *
     * @param plugin The owning plugin
     */
    public ScriptApiMenuView(Plugin plugin) {
        super(plugin);
    }

    @Override
    public IScriptApiObject getApiObject(IEvaluatedScript script) {
        return new ApiObject();
    }

    public class ApiObject implements IScriptApiObject {

        private final Set<ScriptMenuView> _menuViews = new WeakHashSet<>(10);
        private boolean _isDisposed;

        @Override
        public boolean isDisposed() {
            return _isDisposed;
        }

        @Override
        public void dispose() {

            for (ScriptMenuView menuView : _menuViews) {
                menuView.dispose();
            }
            _menuViews.clear();
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

            ScriptMenuView menuView = new ScriptMenuView(getPlugin(), p, title);
            _menuViews.add(menuView);

            return menuView;
        }
    }
}
