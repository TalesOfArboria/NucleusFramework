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


package com.jcwhatever.bukkit.generic.views.triggers;

import com.jcwhatever.bukkit.generic.extended.MaterialExt;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.storage.settings.SettingDefinitions;
import com.jcwhatever.bukkit.generic.storage.settings.ValueType;
import com.jcwhatever.bukkit.generic.views.IView;
import com.jcwhatever.bukkit.generic.views.ViewManager;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.HashSet;
import java.util.Set;

/**
 * View trigger that activates when a block type is clicked.
 */
public class BlockTypeTrigger extends AbstractViewTrigger {

    private static Set<BlockTypeTrigger> _instances = new HashSet<BlockTypeTrigger>(10);
    private static EventListener _eventListener;
    private static SettingDefinitions _possibleSettings = new SettingDefinitions();

    static {
        _possibleSettings
                .set("block-type", ValueType.ITEMSTACK,
                        "An ItemStack that represents the block material type that must be " +
                                "right clicked to activate trigger.")
        ;
    }

    private MaterialData _blockTypeData;
    private MaterialExt _material;

    @Override
    protected void onInit(IView view, ViewManager viewManager, IDataNode dataNode) {

        // initialize global Bukkit event listener if it isn't already initialized
        if (_eventListener == null) {
            _eventListener = new EventListener();
            Bukkit.getPluginManager().registerEvents(_eventListener, getViewManager().getPlugin());
        }
    }

    @Override
    public void dispose() {
        _instances.remove(this);
    }

    /**
     * Get the material data of the block type that
     * must be clicked in order to activate the view.
     */
    public MaterialData getMaterialData() {
        return _blockTypeData;
    }

    /**
     * Set the material data of the block type that
     * must be clicked in order to activate the view.
     */
    public void setMaterialData(ItemStack stack) {
        _blockTypeData = stack.getData().clone();
        _material = MaterialExt.from(_blockTypeData.getItemType());
    }

    /**
     * Set the material data of the block type that
     * must be clicked in order to activate the view.
     */
    public void setMaterialData(MaterialData data) {
        _blockTypeData = data;
        _material = MaterialExt.from(_blockTypeData.getItemType());
    }

    @Override
    protected void onLoadSettings(IDataNode dataNode) {
        _instances.remove(this);

        _blockTypeData = null;

        ItemStack[] items = dataNode.getItemStacks("block-type");
        if (items != null && items.length > 0) {

            _blockTypeData = items[0].getData();
            _material = MaterialExt.from(_blockTypeData.getItemType());
            _instances.add(this);
        }
    }

    @Override
    protected SettingDefinitions getPossibleSettings() {
        return _possibleSettings;
    }

    private boolean trigger(Player p, BlockState blockState) {

        boolean isMatch = !_material.usesColorData() && !_material.usesSubMaterialData()
                ? blockState.getType() == _material.getMaterial()
                : blockState.getData().equals(_blockTypeData);

        if (isMatch) {
            getViewManager().show(p, getView(), blockState.getBlock(), null);
            return true;
        }

        return false;
    }


    private static class EventListener implements Listener {

        @EventHandler(priority=EventPriority.LOW)
        private void onPlayerInteract(PlayerInteractEvent event) {

            if (event.getAction() != Action.RIGHT_CLICK_BLOCK || !event.hasBlock())
                return;

            // prevent inventory clicks
            if (event.getClickedBlock().getLocation() == null)
                return;

            BlockState state = event.getClickedBlock().getState();

            for (BlockTypeTrigger trigger : _instances) {
                if (trigger.trigger(event.getPlayer(), state)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }









}
