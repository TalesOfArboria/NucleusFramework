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

package com.jcwhatever.nucleus.internal;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.events.manager.EventManager;
import com.jcwhatever.nucleus.internal.listeners.BlockListener;
import com.jcwhatever.nucleus.internal.listeners.EnchantmentListener;
import com.jcwhatever.nucleus.internal.listeners.EntityListener;
import com.jcwhatever.nucleus.internal.listeners.HangingListener;
import com.jcwhatever.nucleus.internal.listeners.InventoryListener;
import com.jcwhatever.nucleus.internal.listeners.PlayerListener;
import com.jcwhatever.nucleus.internal.listeners.VehicleListener;
import com.jcwhatever.nucleus.internal.listeners.WeatherListener;
import com.jcwhatever.nucleus.internal.listeners.WorldListener;
import com.jcwhatever.nucleus.utils.Scheduler;

import org.bukkit.Bukkit;

public final class InternalEventManager extends EventManager {

    public InternalEventManager() {
        super(null);

        Scheduler.runTaskLater(Nucleus.getPlugin(), new Runnable() {
            @Override
            public void run() {

                Bukkit.getPluginManager().registerEvents(new BlockListener(), Nucleus.getPlugin());
                Bukkit.getPluginManager().registerEvents(new EnchantmentListener(), Nucleus.getPlugin());
                Bukkit.getPluginManager().registerEvents(new EntityListener(), Nucleus.getPlugin());
                Bukkit.getPluginManager().registerEvents(new HangingListener(), Nucleus.getPlugin());
                Bukkit.getPluginManager().registerEvents(new InventoryListener(), Nucleus.getPlugin());
                Bukkit.getPluginManager().registerEvents(new PlayerListener(), Nucleus.getPlugin());
                Bukkit.getPluginManager().registerEvents(new VehicleListener(), Nucleus.getPlugin());
                Bukkit.getPluginManager().registerEvents(new WeatherListener(), Nucleus.getPlugin());
                Bukkit.getPluginManager().registerEvents(new WorldListener(), Nucleus.getPlugin());
            }
        });
    }

    @Override
    public void dispose() {

        // The global manager cannot be disposed.
        throw new RuntimeException("Cannot dispose the global event manager.");
    }
}
