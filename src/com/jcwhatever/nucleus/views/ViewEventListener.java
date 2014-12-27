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

package com.jcwhatever.nucleus.views;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.events.manager.NucleusEventHandler;
import com.jcwhatever.nucleus.events.manager.NucleusEventListener;
import com.jcwhatever.nucleus.utils.Scheduler;
import com.jcwhatever.nucleus.views.data.ViewOpenReason;

import org.bukkit.entity.Entity;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.server.PluginDisableEvent;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Common view event Listener that works in conjunction with
 * {@link ViewSession}.
 */
class ViewEventListener extends NucleusEventListener {

    private static ViewEventListener _instance;

    /**
     * Used to register a view session so events can be
     * handled on its behalf.
     *
     * @param session  The session to register
     */
    static void register(ViewSession session) {
        if (_instance == null) {
            _instance = new ViewEventListener();
            Nucleus.getEventManager().register(_instance);
        }

        _instance._sessions.put(session.getPlayer(), session);
    }

    /**
     * Used to unregister a view session when it no longer
     * needs view events handled.
     *
     * @param session  The session to unregister.
     */
    static void unregister(ViewSession session) {

        _instance._sessions.remove(session.getPlayer());
    }

    private Map<Entity, ViewSession> _sessions = new WeakHashMap<>(25);

    /**
     * Constructor.
     */
    ViewEventListener() {
        super(Nucleus.getPlugin());
        _instance = this;
    }

    // Handle view closing.
    @NucleusEventHandler
    private void onInventoryClose(InventoryCloseEvent event) {

        ViewSession session = _sessions.get(event.getPlayer());
        if (session == null)
            return;

        // A disposed session should not be in the sessions map
        if (session.isDisposed()) {
            throw new RuntimeException("A view session that is disposed is still registered.");
        }

        // get the current session view.
        IView currentView = session.getCurrentView();
        if (currentView == null)
            throw new AssertionError();

        switch (currentView.getCloseReason()) {

            // Player pressed escape to go back to the previous view.
            case ESCAPE:
                session.escaped();
                if (session.getCurrentView() != null) {
                    openView(ViewOpenReason.PREV, session.getCurrentView());
                }
                break;

            // Called to go back to the previous view.
            case PREV:
                IView prevView = session.getPrevView();
                if (prevView != null) {
                    openView(ViewOpenReason.PREV, prevView);
                }
                break;

            // Closing and re-opening the current view.
            case REFRESH:
                openView(ViewOpenReason.REFRESH, currentView);
                break;

            // Closing the current view to open the next view.
            case NEXT:
                IView nextView = session.getNextView();
                if (nextView != null) {
                    openView(ViewOpenReason.NEXT, nextView);
                }
                break;

            default:
                throw new RuntimeException("The value returned from a views getCloseReason method " +
                        "cannot return null.");
        }

        currentView.resetCloseReason();
    }

    // reset static instance field if NucleusFramework is disabled. (i.e. reload server)
    @NucleusEventHandler
    private void onNucleusDisabled(PluginDisableEvent event) {
        if (event.getPlugin() == Nucleus.getPlugin())
            _instance = null;
    }

    // Open a view
    private void openView(final ViewOpenReason reason, final IView view) {

        Scheduler.runTaskLater(view.getPlugin(), new Runnable() {
            @Override
            public void run() {
                view.open(reason);
            }
        });
    }
}
