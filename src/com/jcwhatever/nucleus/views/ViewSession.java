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
import com.jcwhatever.nucleus.collections.players.PlayerMap;
import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.mixins.IMeta;
import com.jcwhatever.nucleus.mixins.IPlayerReference;
import com.jcwhatever.nucleus.utils.MetaKey;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.Scheduler;
import com.jcwhatever.nucleus.utils.observer.result.FutureResultAgent;
import com.jcwhatever.nucleus.utils.observer.result.FutureResultAgent.Future;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * A session that tracks and provides session context data
 * to view instances.
 *
 * <p>Not thread safe. {@link ViewSession} should always be
 * invoked from the main thread.</p>
 */
public final class ViewSession implements IMeta, Iterable<View>, IPlayerReference, IDisposable {

    private static final Map<UUID, ViewSession> _sessionMap = new PlayerMap<>(Nucleus.getPlugin());

    /**
     * Get a players current view session.
     *
     * @param p  The player to check.
     *
     * @return  Null if the player does not have a view session.
     */
    @Nullable
    public static ViewSession getCurrent(Player p) {
        ViewSession session = _sessionMap.get(p.getUniqueId());
        if (session == null)
            return null;

        if (session.isDisposed()) {
            _sessionMap.remove(p.getUniqueId());
            return null;
        }

        return session;
    }

    /**
     * Get the players current view session or create a new one.
     *
     * @param p             The player.
     * @param sessionBlock  The session block to use if a new session is created.
     */
    public static ViewSession get(Player p, @Nullable Block sessionBlock) {
        ViewSession session = getCurrent(p);
        if (session == null || session.isDisposed()) {
            session = new ViewSession(p, sessionBlock);
            _sessionMap.put(p.getUniqueId(), session);
        }

        return session;
    }

    private final Player _player;
    private final Map<Object, Object> _meta = new HashMap<>(10);
    private final Block _sessionBlock;

    protected ViewContainer _first;
    protected ViewContainer _current;
    private boolean _isDisposed;

    /**
     * Constructor.
     *
     * @param player        The player to create the session for.
     * @param sessionBlock  The optional session block, a block that represents the view.
     */
    private ViewSession(Player player, @Nullable Block sessionBlock) {
        _player = player;
        _sessionBlock = sessionBlock;
        ViewEventListener.register(this);
        _sessionMap.put(player.getUniqueId(), this);
    }

    /**
     * Get the player the view session is for.
     */
    @Override
    public final Player getPlayer() {
        return _player;
    }

    /**
     * Get the block that is the source of the
     * view session. This is normally the block that
     * a player clicks in order to open the view.
     *
     * @return  Null if a block did not start the session.
     */
    @Nullable
    public Block getSessionBlock() {
        return _sessionBlock;
    }

    /**
     * Determine if the view session contains the specified
     * {@link View}.
     *
     * @param view  The view to check.
     */
    public boolean contains(View view) {
        PreCon.notNull(view);

        ViewContainer container = _first;
        while (container != null) {

            if (container.view.equals(view))
                return true;

            container = container.next;
        }

        return false;
    }

    /**
     * Get the view instance the player is currently looking at.
     *
     * @return  Null if the player is not looking at any views in the session.
     */
    @Nullable
    public View getCurrent() {
        if (_current == null)
            return null;

        return _current.view;
    }

    /**
     * Get the previous view, if any.
     *
     * @return  Null if the current view is the first view or there is no
     * current view.
     */
    @Nullable
    public View getPrev() {
        if (_current == null || _current.prev == null)
            return null;

        return _current.prev.view;
    }

    /**
     * Get the next view, if any.
     *
     * @return  Null if the current view is the last view or there is
     * no current view.
     */
    @Nullable
    public View getNext() {
        if (_current == null || _current.next == null)
            return null;

        return _current.next.view;
    }

    /**
     * Get the first view, if any.
     *
     * @return Null if there are no views.
     */
    @Nullable
    public View getFirst() {

        if (_current == null)
            return null;

        ViewContainer current = _current;

        while (current.prev != null) {
            current = current.prev;
        }

        return current.view;
    }

    /**
     * Get the last view, if any.
     *
     * @return Null if there are no views.
     */
    @Nullable
    public View getLast() {

        if (_current == null)
            return null;

        ViewContainer current = _current;

        while (current.next != null) {
            current = current.next;
        }

        return current.view;
    }

    /**
     * Close the current view and go to the previous view.
     *
     * <p>If there is no previous view, the session is ended.</p>
     *
     * <p>There is a 1 tick delay before the previous view is shown. The
     * state of the view will reflect the previous state until then.</p>
     *
     * @return A future that will return the result once it has completed. Possible
     * outcomes are success or cancel.
     *
     * @throws java.lang.IllegalStateException if there is no current view.
     */
    public Future<View> previous() {
        if (_current == null)
            throw new IllegalStateException();

        if (_isDisposed)
            throw new RuntimeException("Cannot use a disposed ViewSession.");

        final FutureResultAgent<View> agent = new FutureResultAgent<>();

        Scheduler.runTaskLater(_current.view.getPlugin(), new Runnable() {
            @Override
            public void run() {

                if (_current.view.close(ViewCloseReason.PREV)) {
                    _current = _current.prev;
                    if (_current == null) {
                        dispose();
                    }
                    agent.success(getCurrent());
                }
                else {
                    agent.cancel(getCurrent());
                }
            }
        });

        return agent.getFuture();
    }

    /**
     * Called to indicate a menu was escaped.
     * The same as calling back except the view
     * is not called to close.
     */
    @Nullable
    void escaped() {
        if (_current == null)
            return;

        _current.view.close(ViewCloseReason.ESCAPE);

        _current = _current.prev;
        if (_current == null) {
            dispose();
        }
    }

    /**
     * Show the next view.
     *
     * <p>There is a 1 tick delay before the view is actually
     * opened. The state of the view may be inaccurate until then.</p>
     *
     * @param view  The view instance to display next.
     *
     * @return A future that will return the result of the operation once
     * it has completed. Possible outcomes are success or cancel.
     */
    public Future<View> next(final View view) {
        PreCon.notNull(view);

        if (_isDisposed)
            throw new RuntimeException("Cannot use a disposed ViewSession.");

        final FutureResultAgent<View> agent = new FutureResultAgent<>();

        view.setViewSession(this);

        if (_current == null) {

            _first = new ViewContainer(view, null, null);
            _current = _first;

            Scheduler.runTaskLater(view.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    if (_current.view.open(ViewOpenReason.FIRST)) {
                        agent.success(getCurrent());
                    }
                    else {
                        _first = null;
                        _current = null;
                        agent.cancel(null);
                    }
                }
            });
        }
        else {

            Scheduler.runTaskLater(view.getPlugin(), new Runnable() {
                @Override
                public void run() {

                    ViewContainer prev = _current;
                    ViewContainer origNext = prev.next;
                    ViewContainer current = new ViewContainer(view, prev, null);
                    prev.next = current;

                    // close current view, ViewEventListener will open next view
                    if (_current.view.close(ViewCloseReason.NEXT)) {

                        _current = current;
                        agent.success(getCurrent());
                    }
                    else {
                        prev.next = origNext;
                        agent.cancel(getCurrent());
                    }
                }
            });
        }

        return agent.getFuture();
    }

    /**
     * Close and re-open the current view.
     *
     * <p>There is a 1-2 tick delay before the view refresh
     * is complete. The state of the view will be inaccurate until then.</p>
     *
     * @return A future that will return the result of the operation once
     * it has completed. Possible outcomes are success, error or cancel.
     */
    public Future<View> refresh() {

        if (_isDisposed)
            throw new RuntimeException("Cannot use a disposed ViewSession.");

        final FutureResultAgent<View> agent = new FutureResultAgent<>();

        final View view = getCurrent();
        if (view == null) {
            return agent.cancel(null, "No current view to refresh.");
        }

        if (!view.close(ViewCloseReason.REFRESH)) {
            return agent.cancel(view);
        }

        Scheduler.runTaskLater(view.getPlugin(), new Runnable() {
            @Override
            public void run() {
                if (view.open(ViewOpenReason.REFRESH)) {
                    agent.success(view);
                }
                else {
                    // error: refresh is not an appropriate time to not be able to re-open the view.
                    agent.error(view);
                }
            }
        });

        return agent.getFuture();
    }

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {
        if (_isDisposed)
            return;

        ViewEventListener.unregister(this);
        _sessionMap.remove(_player.getUniqueId());

        if (_current != null) {

            ViewContainer current = _current;

            while (current != null) {
                current.view.onDispose();
                current = current.prev;
            }
        }

        _player.closeInventory();

        _isDisposed = true;
    }

    @Nullable
    @Override
    public <T> T getMeta(MetaKey<T> key) {
        PreCon.notNull(key);

        @SuppressWarnings("unchecked")
        T item = (T)_meta.get(key);

        return item;
    }

    @Nullable
    @Override
    public Object getMetaObject(Object key) {
        PreCon.notNull(key);

        return _meta.get(key);
    }

    @Override
    public <T> void setMeta(MetaKey<T> key, @Nullable T value) {
        PreCon.notNull(key);

        if (_isDisposed)
            throw new RuntimeException("Cannot use a disposed ViewSession.");

        if (value == null) {
            _meta.remove(key);
        }
        else {
            _meta.put(key, value);
        }
    }

    @Override
    public Iterator<View> iterator() {
        return new Iterator<View>() {

            ViewContainer _current = _first;

            @Override
            public boolean hasNext() {
                return _current.next != null;
            }

            @Override
            public View next() {
                _current = _current.next;
                return _current.view;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    protected static class ViewContainer {
        final View view;
        final ViewContainer prev;
        ViewContainer next;

        protected ViewContainer(View view, @Nullable ViewContainer prev,
                                @Nullable ViewContainer next) {
            this.view = view;
            this.prev = prev;
            this.next = next;
        }
    }
}
