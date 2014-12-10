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

package com.jcwhatever.bukkit.generic.scripting.api.views;

import com.jcwhatever.bukkit.generic.scripting.IEvaluatedScript;
import com.jcwhatever.bukkit.generic.scripting.api.GenericsScriptApi;
import com.jcwhatever.bukkit.generic.scripting.api.IScriptApiObject;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.views.IView;
import com.jcwhatever.bukkit.generic.views.ViewSession;
import com.jcwhatever.bukkit.generic.views.chest.ChestView;
import com.jcwhatever.bukkit.generic.views.data.ViewArguments;
import com.jcwhatever.bukkit.generic.views.menu.MenuItem;
import com.jcwhatever.bukkit.generic.views.menu.MenuView;
import com.jcwhatever.bukkit.generic.views.menu.ScriptMenuView;
import com.jcwhatever.bukkit.generic.views.menu.ScriptMenuViewFactory;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

/*
 * 
 */
public class ScriptApiViews extends GenericsScriptApi {

    private ScriptMenuViewFactory _menuFactory;

    /**
     * Constructor.
     *
     * @param plugin The owning plugin
     */
    public ScriptApiViews(Plugin plugin) {
        super(plugin);

        _menuFactory = new ScriptMenuViewFactory(plugin, "Menu");
    }

    @Override
    public IScriptApiObject getApiObject(IEvaluatedScript script) {
        return null;
    }

    public class ApiObject {

        private Map<IView, Void> _openViews = new WeakHashMap<>(10);


        public MenuViewReference openMenu(String name, Player p, String title, int slots, @Nullable Block sourceBlock) {

            ViewSession session = ViewSession.get(p, sourceBlock);

            ScriptMenuView view = _menuFactory.create(title, session, new ViewArguments(), slots);

            _openViews.put(view, null);

            return new MenuViewReference(view);
        }

        public class MenuViewReference {
            private MenuView _view;

            MenuViewReference(MenuView view) {
                _view = view;
            }

            public MenuItem addMenuItem(int slot, ItemStack itemStack, String title, String description) {
                PreCon.positiveNumber(slot);
                PreCon.isValid(slot <= ChestView.MAX_SLOTS);
                PreCon.notNull(itemStack);

                MenuItem menuItem = new MenuItem(slot);

                menuItem.setItemStack(itemStack);

                if (title != null)
                    menuItem.setTitle(title);

                if (description != null)
                    menuItem.setDescription(description);

                _view.setMenuItem(menuItem);

                return menuItem;
            }
        }
    }
}
