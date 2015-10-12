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

package com.jcwhatever.nucleus.events.block;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.events.HandlerListExt;
import com.jcwhatever.nucleus.mixins.ICancellable;
import com.jcwhatever.nucleus.mixins.IPlayerReference;
import com.jcwhatever.nucleus.utils.PreCon;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;

/**
 * Call whenever a player transforms a block into another block using
 * custom methods.
 */
public class PlayerTransformBlockEvent extends BlockEvent
        implements Cancellable, ICancellable, IPlayerReference {

    private static final HandlerList handlers = new HandlerListExt(
            Nucleus.getPlugin(), PlayerTransformBlockEvent.class);

    private final Player _player;
    private Material _material;
    private int _data;

    private boolean _isCancelled;

    /**
     * Constructor.
     *
     * @param player       The player transforming the block.
     * @param block        The block to be transformed.
     * @param newMaterial  The material the block will be transformed to.
     * @param newData      The new data.
     */
    public PlayerTransformBlockEvent(Player player, Block block, Material newMaterial, int newData) {
        super(block);
        _player = player;
        _material = newMaterial;
        _data = newData;
    }

    /**
     * Get the player.
     */
    @Override
    public Player getPlayer() {
        return _player;
    }

    /**
     * Get the new material.
     */
    public Material getNewMaterial() {
        return _material;
    }

    /**
     * Get the new data.
     */
    public int getNewData() {
        return _data;
    }

    /**
     * Set the new material.
     *
     * @param material  The new material.
     */
    public void setNewMaterial(Material material) {
        PreCon.notNull(material);

        _material = material;
    }

    /**
     * Set the new data.
     *
     * @param data  The new data.
     */
    public void setNewData(int data) {
        _data = data;
    }

    @Override
    public boolean isCancelled() {
        return _isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        _isCancelled = isCancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

