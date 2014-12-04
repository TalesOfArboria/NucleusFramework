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


package com.jcwhatever.bukkit.generic.views;

import com.jcwhatever.bukkit.generic.utils.ItemStackUtils;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.text.TextUtils;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an item stack that can be selected in a menu view instance.
 */
public class MenuItem extends ViewMeta {

    private String _name;
    private IDataNode _itemNode;
    private AbstractMenuView _parent;

    private ItemStack _itemStack;
    private String _title;
    private String _description;

    private String _clickCommand;
    private String _clickViewName;

    private int _slot;

    /**
     * Constructor.
     *
     * @param name      The name of the menu item. Used for saving to a data node.
     * @param parent    The parent Menu View.
     * @param itemNode  The data node settings should be saved to.
     */
    public MenuItem(int slot, String name, AbstractMenuView parent, @Nullable IDataNode itemNode) {
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(parent);

        _name = name;
        _itemNode = itemNode;
        _parent = parent;
        _slot = slot;

        loadSettings();
    }

    /**
     * Constructor.
     *
     * @param name    The name of the menu item.
     * @param parent  The parent Menu View.
     */
    public MenuItem(int slot, String name, AbstractMenuView parent) {
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(parent);

        _name = name;
        _title = name;
        _parent = parent;
        _slot = slot;
    }

    /**
     * Gets the name of the menu item.
     */
    public String getName() {
        return _name;
    }

    /**
     * Gets the title of the menu item.
     * Title replaces the menu items ItemStack display name.
     */
    public String getTitle() {
        return _title;
    }

    /**
     * Gets the menu item description.
     * Description is added to the menu items ItemStack lore.
     */
    @Nullable
    public String getDescription() {
        return _description;
    }

    /**
     * Get the menu items ItemStack.
     */
    @Nullable
    public ItemStack getItemStack() {
        return _itemStack;
    }

    /**
     * Get the optional command that is executed on the players behalf
     * when the menu item is selected.
     */
    @Nullable
    public String getClickCommand() {
        return _clickCommand;
    }

    /**
     * Get the optional name of the view that is opened when
     * the menu item is clicked.
     */
    @Nullable
    public String getClickViewName() {
        return _clickViewName;
    }

    /**
     * Get the inventory slot the menu item is in.
     */
    public int getSlot() {
        return _slot;
    }

    // Internal. Set the inventory slot the menu item is in.
    void setSlot(int slot) {
        _slot = slot;
    }


    /**
     * Set the title of the menu item.
     * The title replaces the menu items ItemStack display name.
     *
     * @param title  The menu item title
     */
    public void setTitle(String title) {
        PreCon.notNull(title);

        _title = title;

        if (_itemNode != null) {
            _itemNode.set("title", title);
            _itemNode.saveAsync(null);
        }

        if (_itemStack != null)
            updateDisplayName();

        rebuildParent();
    }

    /**
     * Set the menu items description.
     * The description is added to the menu items ItemStack lore.
     *
     * @param description  The description.
     */
    public void setDescription(String description) {
        PreCon.notNull(description);

        _description = description;

        if (_itemNode != null) {
            _itemNode.set("description", description);
            _itemNode.saveAsync(null);
        }

        if (_itemStack != null)
            setLore(description);

        rebuildParent();
    }

    /**
     * Set the item stack used by the menu item.
     *
     * @param itemStack  The item stack
     */
    public void setItemStack(ItemStack itemStack) {
        PreCon.notNull(itemStack);

        _itemStack = itemStack;

        if (_itemNode != null) {
            _itemNode.set("item", itemStack);
            _itemNode.saveAsync(null);
        }

        if (_itemStack != null) {

            if (_title != null)
                updateDisplayName();

            if (_description != null)
                setLore(_description);
        }

        rebuildParent();
    }

    /**
     * Set the command to run on the players behalf if the menu item
     * is clicked. Null to remove.
     *
     * @param command  The command to execute
     */
    public void setClickCommand(@Nullable String command) {

        _clickCommand = command;

        if (_itemNode != null) {
            _itemNode.set("click.command", command);
            _itemNode.saveAsync(null);
        }
    }

    /**
     * Set the name of the view to open when the menu item
     * is clicked. Null to remove.
     *
     * @param viewName  The name of the view.
     */
    public void setClickViewName(@Nullable String viewName) {
        _clickViewName = viewName;

        if (_itemNode != null) {
            _itemNode.set("click.view", viewName);
            _itemNode.saveAsync(null);
        }
    }

    // initial load of settings
    private void loadSettings() {

        if (_itemNode == null)
            return;

        ItemStack[] items = _itemNode.getItemStacks("item");
        _title = _itemNode.getString("title");
        _description = _itemNode.getString("description");

        IDataNode clickNode = _itemNode.getNode("click");
        _clickCommand = clickNode.getString("command");
        _clickViewName = clickNode.getString("view");

        if (items != null && items.length > 0) {
            _itemStack = items[0];

            if (_title != null)
                updateDisplayName();

            if (_description != null) {
                setLore(_description);
            }
        }
    }

    // helper, set lore on menu item
    private void setLore(String loreString) {

        if (loreString != null) {
            loreString = TextUtils.format(loreString);

            List<String> lore = TextUtils.paginateString(loreString, 32, false);
            ItemStackUtils.setLore(_itemStack, lore);
        }
        else {
            ItemStackUtils.setLore(_itemStack, new ArrayList<String>(0));
        }

    }

    // rebuild parent inventory
    private void rebuildParent() {
        _parent.buildInventory();
    }

    // helper, set menu item display name
    private void updateDisplayName() {
        String title = _title;
        if (title != null)
            title = TextUtils.format(title);

        ItemStackUtils.setDisplayName(_itemStack, title);
    }

}
