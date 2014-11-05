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


package com.jcwhatever.bukkit.generic.internal.listeners;

import com.jcwhatever.bukkit.generic.events.GenericsEventManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockExpEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.event.block.SignChangeEvent;

public class BlockListener implements Listener{

    @EventHandler
    private void onBlockBreak(BlockBreakEvent event) {

        GenericsEventManager.getGlobal().call(event);
    }

    @EventHandler
    private void onBlockBurn(BlockBurnEvent event) {

        GenericsEventManager.getGlobal().call(event);
    }

    @EventHandler
    private void onBlockCanBuildEvent(BlockCanBuildEvent event) {

        GenericsEventManager.getGlobal().call(event);
    }

    @EventHandler
    private void onBlockDamage(BlockDamageEvent event) {

        GenericsEventManager.getGlobal().call(event);
    }

    @EventHandler
    private void onBlockDispense(BlockDispenseEvent event) {

        GenericsEventManager.getGlobal().call(event);
    }

    @EventHandler
    private void onBlockExp(BlockExpEvent event) {

        GenericsEventManager.getGlobal().call(event);
    }

    @EventHandler
    private void onBlockFade(BlockFadeEvent event) {

        GenericsEventManager.getGlobal().call(event);
    }

    @EventHandler
    private void onBlockForm(BlockFormEvent event) {

        GenericsEventManager.getGlobal().call(event);
    }

    @EventHandler
    private void onBlockFromTo(BlockFromToEvent event) {

        GenericsEventManager.getGlobal().call(event);
    }

    @EventHandler
    private void onBlockGrow(BlockGrowEvent event) {

        GenericsEventManager.getGlobal().call(event);
    }

    @EventHandler
    private void onBlockIgnite(BlockIgniteEvent event) {

        GenericsEventManager.getGlobal().call(event);
    }

    @EventHandler
    private void onBlockMultiPlace(BlockMultiPlaceEvent event) {

        GenericsEventManager.getGlobal().call(event);
    }

    @EventHandler
    private void onBlockPhysics(BlockPhysicsEvent event) {

        GenericsEventManager.getGlobal().call(event);
    }

    @EventHandler
    private void onBlockPistonExtend(BlockPistonExtendEvent event) {

        GenericsEventManager.getGlobal().call(event);
    }

    @EventHandler
    private void onBlockPistonRetractEvent(BlockPistonRetractEvent event) {

        GenericsEventManager.getGlobal().call(event);
    }

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent event) {

        GenericsEventManager.getGlobal().call(event);
    }

    @EventHandler
    private void onBlockRedstone(BlockRedstoneEvent event) {

        GenericsEventManager.getGlobal().call(event);
    }

    @EventHandler
    private void onBlockSpread(BlockSpreadEvent event) {

        GenericsEventManager.getGlobal().call(event);
    }

    @EventHandler
    private void onEntityBlockForm(EntityBlockFormEvent event) {

        GenericsEventManager.getGlobal().call(event);
    }

    @EventHandler
    private void onLeavesDecay(LeavesDecayEvent event) {

        GenericsEventManager.getGlobal().call(event);
    }

    @EventHandler
    private void onNotePlay(NotePlayEvent event) {

        GenericsEventManager.getGlobal().call(event);
    }

    @EventHandler
    private void onSignChange(SignChangeEvent event) {

        GenericsEventManager.getGlobal().call(event);
    }
}
