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

package com.jcwhatever.nucleus.internal.scripting.api.views;

import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.items.ItemStackMatcher;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import com.jcwhatever.nucleus.views.View;
import com.jcwhatever.nucleus.views.ViewOpenReason;
import com.jcwhatever.nucleus.views.ViewSession;
import com.jcwhatever.nucleus.views.menu.MenuItem;
import com.jcwhatever.nucleus.views.menu.MenuItemBuilder;
import com.jcwhatever.nucleus.views.menu.MenuView;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A {@link MenuView} implementation for scripts.
 */
public class ScriptMenuView extends MenuView implements IDisposable {

    private final String _title;
    private final Map<Integer, MenuItem> _menuItems = new HashMap<>(16);
    private final Player _player;

    private boolean _isDisposed;

    /**
     * Constructor.
     *
     * @param plugin  The owning plugin.
     * @param player  The player the view is for.
     * @param title   The view title.
     */
    protected ScriptMenuView(Plugin plugin, Player player, String title) {
        super(plugin, ItemStackMatcher.getDefault());

        PreCon.notNull(title);
        PreCon.notNull(player);

        _title = TextUtils.format(title);
        _player = player;
    }

    /**
     * Put a {@link MenuItem} into the menu view.
     *
     * <p>Use before opening the view.</p>
     *
     * @param menuItem  The {@link MenuItem} to add.
     *
     * @return  Self for chaining.
     */
    public ScriptMenuView putItem(MenuItem menuItem) {
        _menuItems.put(menuItem.getSlot(), menuItem);

        return this;
    }

    /**
     * Put a {@link MenuItemBuilder} into the menu view.
     *
     * <p>Use before opening the view.</p>
     *
     * @param slot             The inventory slot the {@link MenuItem} will be put into.
     * @param menuItemBuilder  The {@link MenuItemBuilder} to use to build the {@link MenuItem}.
     *
     * @return  Self for chaining.
     */
    public ScriptMenuView putItem(int slot, MenuItemBuilder menuItemBuilder) {

        MenuItem menuItem = menuItemBuilder.build(slot);

        _menuItems.put(menuItem.getSlot(), menuItem);

        return this;
    }

    /**
     * Open the view to the player.
     *
     * @return  Self for chaining.
     */
    public ScriptMenuView open() {

        ViewSession viewSession = ViewSession.get(_player, null);

        viewSession.next(this);

        return this;
    }

    /**
     * Close the view to the player.
     *
     * @return  Self for chaining.
     */
    public ScriptMenuView close() {

        ViewSession viewSession = ViewSession.get(_player, null);

        if (!viewSession.contains(this))
            return this;

        View view = viewSession.getCurrent();

        // close all views opened after this view
        while (view != null && !view.equals(this)) {
            viewSession.previous();
            view = viewSession.getCurrent();
        }

        if (view != null && view.equals(this))
            viewSession.previous();

        return this;
    }

    @Override
    public String getTitle() {
        return _title;
    }

    @Override
    protected List<MenuItem> createMenuItems() {
        return new ArrayList<>(_menuItems.values());
    }

    @Override
    protected void onItemSelect(MenuItem menuItem) {
        // do nothing
    }

    @Override
    protected void onShow(ViewOpenReason reason) {
        // do nothing
    }

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {

        List<MenuItem> menuItems = new ArrayList<>(getMenuItems());

        for (MenuItem menuItem : menuItems) {
            menuItem.clearOnClick();
        }

        _menuItems.clear();

        _isDisposed = true;
    }
}
