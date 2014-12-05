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

package com.jcwhatever.bukkit.generic.internal;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.events.GenericsEventManager;
import com.jcwhatever.bukkit.generic.internal.listeners.BlockListener;
import com.jcwhatever.bukkit.generic.internal.listeners.EnchantmentListener;
import com.jcwhatever.bukkit.generic.internal.listeners.EntityListener;
import com.jcwhatever.bukkit.generic.internal.listeners.HangingListener;
import com.jcwhatever.bukkit.generic.internal.listeners.InventoryListener;
import com.jcwhatever.bukkit.generic.internal.listeners.PlayerListener;
import com.jcwhatever.bukkit.generic.internal.listeners.VehicleListener;
import com.jcwhatever.bukkit.generic.internal.listeners.WeatherListener;
import com.jcwhatever.bukkit.generic.internal.listeners.WorldListener;
import com.jcwhatever.bukkit.generic.utils.Scheduler;

import org.bukkit.Bukkit;

public final class InternalEventManager extends GenericsEventManager {

    public InternalEventManager() {
        super(null);

        Scheduler.runTaskLater(GenericsLib.getLib(), new Runnable() {
            @Override
            public void run() {

                Bukkit.getPluginManager().registerEvents(new BlockListener(), GenericsLib.getLib());
                Bukkit.getPluginManager().registerEvents(new EnchantmentListener(), GenericsLib.getLib());
                Bukkit.getPluginManager().registerEvents(new EntityListener(), GenericsLib.getLib());
                Bukkit.getPluginManager().registerEvents(new HangingListener(), GenericsLib.getLib());
                Bukkit.getPluginManager().registerEvents(new InventoryListener(), GenericsLib.getLib());
                Bukkit.getPluginManager().registerEvents(new PlayerListener(), GenericsLib.getLib());
                Bukkit.getPluginManager().registerEvents(new VehicleListener(), GenericsLib.getLib());
                Bukkit.getPluginManager().registerEvents(new WeatherListener(), GenericsLib.getLib());
                Bukkit.getPluginManager().registerEvents(new WorldListener(), GenericsLib.getLib());
            }
        });
    }

    @Override
    public void unregisterAll() {

        // The global manager cannot unregister all.
        throw new RuntimeException("Cannot unregister all handlers at once from the global event manager.");
    }

    @Override
    public void dispose() {

        // The global manager cannot be disposed.
        throw new RuntimeException("Cannot dispose the global event manager.");
    }
}
