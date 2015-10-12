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

package com.jcwhatever.nucleus.events;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.events.manager.BukkitEventForwarder;
import com.jcwhatever.nucleus.utils.PreCon;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

/**
 * Extended Bukkit event HandlerList which automatically registers
 * the event with the global event forwarder.
 */
public class HandlerListExt extends HandlerList {

    /**
     * Constructor.
     *
     * @param plugin      The plugin the event is from.
     * @param eventClass  The event class.
     */
    public HandlerListExt(final Plugin plugin, final Class<? extends Event> eventClass) {
        super();
        PreCon.notNull(plugin);
        PreCon.notNull(eventClass);

        Bukkit.getScheduler().runTaskLater(Nucleus.getPlugin(), new Runnable() {
            @Override
            public void run() {
                BukkitEventForwarder.registerGlobal(plugin, eventClass);
            }
        }, 1);
    }
}
