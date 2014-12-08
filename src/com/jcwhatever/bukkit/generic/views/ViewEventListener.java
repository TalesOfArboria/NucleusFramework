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

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.events.GenericsEventHandler;
import com.jcwhatever.bukkit.generic.events.IGenericsEventListener;
import com.jcwhatever.bukkit.generic.utils.Scheduler;
import com.jcwhatever.bukkit.generic.views.data.ViewOpenReason;

import org.bukkit.entity.Entity;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.Map;
import java.util.WeakHashMap;

/*
 * 
 */
class ViewEventListener implements IGenericsEventListener {

    private static ViewEventListener _instance;

    ViewEventListener() {
        _instance = this;
    }

    // TODO: clear _instance when GenericsLib is disabled.

    static void register(ViewSession session) {
        if (_instance == null) {
            _instance = new ViewEventListener();
            GenericsLib.getEventManager().register(_instance);
        }

        _instance._sessions.put(session.getPlayer(), session);
    }

    static void unregister(ViewSession session) {

        _instance._sessions.remove(session.getPlayer());
    }

    private Map<Entity, ViewSession> _sessions = new WeakHashMap<>(25);

    // Cleanup data related to closed chest
    @GenericsEventHandler
    private void onInventoryClose(InventoryCloseEvent event) {

        ViewSession session = _sessions.get(event.getPlayer());
        if (session == null)
            return;

        if (session.isDisposed()) {
            throw new RuntimeException("A view session that is disposed is still registered.");
        }

        IView currentView = session.getCurrentView();
        if (currentView == null)
            return;

        IView previousView = session.getPrevView();
        if (previousView == null)
            return;

        switch (previousView.getCloseReason()) {
            case PREV:
                openView(ViewOpenReason.PREV, previousView);
                break;
            case NEXT:
                // fall through
            case REFRESH:
                break;
            default:
                throw new RuntimeException("The value returned from a views getCloseReason method cannot return null.");
        }

        currentView.resetCloseReason();
    }

    private void openView(final ViewOpenReason reason, final IView view) {

        Scheduler.runTaskLater(view.getPlugin(), new Runnable() {
            @Override
            public void run() {
                view.getFactory().open(reason, view);
            }
        });
    }
}
