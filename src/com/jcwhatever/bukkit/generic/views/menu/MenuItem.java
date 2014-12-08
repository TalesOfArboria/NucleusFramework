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

package com.jcwhatever.bukkit.generic.views.menu;

import com.jcwhatever.bukkit.generic.items.ItemStackComparer;
import com.jcwhatever.bukkit.generic.mixins.ICancellable;
import com.jcwhatever.bukkit.generic.mixins.IMeta;
import com.jcwhatever.bukkit.generic.utils.ItemStackUtils;
import com.jcwhatever.bukkit.generic.utils.MetaKey;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import org.bukkit.Material;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

/*
 * 
 */
public class MenuItem implements IMeta, ICancellable {

    private Map<Object, Object> _metaMap = new HashMap<Object, Object>(10);

    private final int _slot;
    private String _title;
    private String _description;
    private ItemStack _baseItemStack;
    private ItemStack _menuItemStack;
    private List<Runnable> _onClick;
    private boolean _isCancelled;
    private MenuView _parentView;

    public MenuItem(int slot) {
        _slot = slot;
    }

    void setMenuView(MenuView menuView) {
        if (_parentView != null && _parentView != menuView)
            throw new RuntimeException("MenuItems can only be used in one MenuView instance.");

        _parentView = menuView;
    }

    public int getSlot() {
        return _slot;
    }

    public MenuView getMenuView() {
        if (_parentView == null)
            throw new RuntimeException("MenuView not set yet.");

        return _parentView;
    }

    @Nullable
    public String getTitle() {
        return _title;
    }

    @Nullable
    public String getDescription() {
        return _description;
    }

    public ItemStack getItemStack() {
        if (_menuItemStack == null) {
            _menuItemStack = generateItemStack();
        }
        return _menuItemStack;
    }

    public MenuItem setTitle(@Nullable String title) {
        _title = title;
        _menuItemStack = null;

        return this;
    }

    public MenuItem setDescription(@Nullable String description) {
        _description = description;
        _menuItemStack = null;

        return this;
    }

    public MenuItem setItemStack(ItemStack itemStack) {
        PreCon.notNull(itemStack);

        _baseItemStack = itemStack;
        _menuItemStack = null;

        return this;
    }

    public boolean set() {
        if (_parentView == null)
            return false;

        InventoryView view = _parentView.getInventoryView();
        if (view == null)
            return false;

        view.setItem(_slot, _menuItemStack);

        return true;
    }

    public boolean isVisible() {
        if (_parentView == null)
            return false;

        InventoryView view = _parentView.getInventoryView();
        if (view == null)
            return false;

        ItemStack itemStack = view.getItem(_slot);

        return ItemStackComparer.getDefault().isSame(itemStack, _menuItemStack);
    }

    public void setVisible(boolean isVisible) {
        if (_parentView == null)
            return;

        InventoryView view = _parentView.getInventoryView();
        if (view == null || isVisible() == isVisible)
            return;

        if (isVisible) {
            set();
        }
        else {
            view.setItem(_slot, null);
        }
    }

    public List<Runnable> getOnClick() {
        return _onClick;
    }

    public void onClick(Runnable runnable) {
        _onClick.add(runnable);
    }

    public boolean removeOnClick(Runnable runnable) {
        return _onClick.remove(runnable);
    }

    protected ItemStack generateItemStack() {
        if (_baseItemStack == null)
            _baseItemStack = new ItemStack(Material.WOOD);

        _menuItemStack = _baseItemStack.clone();

        if (_title != null)
            ItemStackUtils.setDisplayName(_menuItemStack, _title);

        if (_description != null)
            ItemStackUtils.setLore(_menuItemStack, _description);

        return _menuItemStack;
    }

    private boolean isViewAvailable() {
        return _parentView != null && _parentView.getInventoryView() != null;
    }

    @Nullable
    @Override
    public <T> T getMeta(MetaKey<T> key) {
        PreCon.notNull(key);

        @SuppressWarnings("unchecked")
        T value = (T)_metaMap.get(key);

        return value;
    }

    @Nullable
    @Override
    public Object getMetaObject(Object key) {
        PreCon.notNull(key);

        return _metaMap.get(key);
    }

    @Override
    public <T> void setMeta(MetaKey<T> key, @Nullable T value) {
        PreCon.notNull(key);

        if (value == null)
            _metaMap.remove(key);
        else
            _metaMap.put(key, value);
    }

    @Override
    public boolean isCancelled() {
        return _isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        _isCancelled = isCancelled;
    }
}
