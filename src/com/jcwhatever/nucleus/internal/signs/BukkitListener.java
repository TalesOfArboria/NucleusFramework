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


package com.jcwhatever.nucleus.internal.signs;

import com.jcwhatever.nucleus.events.signs.SignInteractEvent;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;

/**
 * Global Bukkit event listener for {@link InternalSignManager} instances.
 */
class BukkitListener implements Listener {

    private final InternalSignManager _manager;

    BukkitListener(InternalSignManager manager) {
        _manager = manager;
    }

    @EventHandler(priority=EventPriority.NORMAL)
    private void onSignChange(SignChangeEvent event) {

        BlockState state = event.getBlock().getState();
        Sign sign = (Sign)state;

        _manager.signChange(sign, event);
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    private void onBlockBreak(BlockBreakEvent event) {

        // Signs
        Material material = event.getBlock().getType();
        if (material != Material.SIGN_POST && material != Material.WALL_SIGN)
            return;

        if (event.getBlock().getState() instanceof Sign) {

            BlockState state = event.getBlock().getState();
            Sign sign = (Sign)state;

            _manager.signBreak(sign, event);
        }

    }

    @EventHandler(priority=EventPriority.NORMAL)
    private void onSignInteract(SignInteractEvent event) {

        _manager.signClick(event);
    }
}
