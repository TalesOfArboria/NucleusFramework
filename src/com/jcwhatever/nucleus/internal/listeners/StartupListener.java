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
import com.jcwhatever.nucleus.NucleusPlugin;
import com.jcwhatever.nucleus.internal.NucMsg;
import com.jcwhatever.nucleus.mixins.IDisposable;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Used to prevent players from logging in before
 * Nucleus plugins are finished loading.
 */
public class StartupListener implements Listener, IDisposable {

    private boolean _isDisposed;

    /**
     * Prevent players from logging in.
     *
     * <p>Registers self and unregisters self as an event listener.</p>
     */
    public void preventLogins() {

        Bukkit.getPluginManager().registerEvents(this, Nucleus.getPlugin());

        new BukkitRunnable() {

            int seconds = 0;

            @Override
            public void run() {

                seconds++;
                if (seconds == 30) {
                    cancel();
                }

                for (NucleusPlugin plugin : Nucleus.getNucleusPlugins()) {
                    if (!plugin.isLoaded())
                        return;
                }

                cancel();
            }

            @Override
            public void cancel() {
                super.cancel();

                for (NucleusPlugin plugin : Nucleus.getNucleusPlugins()) {
                    if (!plugin.isLoaded() && plugin.isEnabled()) {
                        NucMsg.warning("Plugin '{0}' is taking a while to load.", plugin.getName());
                    }
                }

                dispose();
            }

        }.runTaskTimer(Nucleus.getPlugin(), 1, 20);
    }

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {
        _isDisposed = true;
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    private void onPlayerLogin(PlayerLoginEvent event) {
        event.setKickMessage("Server is still loading.");
        event.setResult(Result.KICK_OTHER);
    }
}
