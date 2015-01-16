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

package com.jcwhatever.nucleus.internal.listeners;

import com.jcwhatever.nucleus.Nucleus;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.event.world.SpawnChangeEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public final class WorldListener implements Listener {

    @EventHandler
    private void onChunkLoad(ChunkLoadEvent event) {
        if (event.isAsynchronous())
            return;

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onChunkPopulate(ChunkPopulateEvent event) {
        if (event.isAsynchronous())
            return;

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onChunkUnload(ChunkUnloadEvent event) {
        if (event.isAsynchronous())
            return;

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onPortalCreate(PortalCreateEvent event) {
        if (event.isAsynchronous())
            return;

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onSpawnChange(SpawnChangeEvent event) {
        if (event.isAsynchronous())
            return;

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onStructureGrow(StructureGrowEvent event) {
        if (event.isAsynchronous())
            return;

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onWorldInit(WorldInitEvent event) {
        if (event.isAsynchronous())
            return;

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onWorldLoad(WorldLoadEvent event) {
        if (event.isAsynchronous())
            return;

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onWorldSave(WorldSaveEvent event) {
        if (event.isAsynchronous())
            return;

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onWorldUnload(WorldUnloadEvent event) {
        if (event.isAsynchronous())
            return;

        Nucleus.getEventManager().call(this, event);
    }
}
