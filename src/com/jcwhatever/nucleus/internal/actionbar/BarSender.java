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

package com.jcwhatever.nucleus.internal.actionbar;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.collections.ElementCounter;
import com.jcwhatever.nucleus.collections.ElementCounter.RemovalPolicy;
import com.jcwhatever.nucleus.collections.SetMap;
import com.jcwhatever.nucleus.collections.WeakHashSetMap;
import com.jcwhatever.nucleus.collections.players.PlayerMap;
import com.jcwhatever.nucleus.managed.actionbar.ActionBarPriority;
import com.jcwhatever.nucleus.utils.TimeScale;
import com.jcwhatever.nucleus.collections.timed.TimedDistributor;
import com.jcwhatever.nucleus.internal.NucMsg;
import com.jcwhatever.nucleus.utils.nms.INmsActionBarHandler;
import com.jcwhatever.nucleus.managed.scheduler.Scheduler;
import com.jcwhatever.nucleus.utils.nms.NmsUtils;
import com.jcwhatever.nucleus.utils.text.dynamic.IDynamicText;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Sends action bars to players and manages {@link PersistentActionBar}'s
 * per player.
 *
 * <p>Also manages packet send timing to reduce network traffic and client side lag
 * based on the max refresh rate required to persist the bar and the minimum
 * refresh rate preferred by the action bars dynamic text. The absolute minimum
 * refresh rate is 1 tick while the absolute max is 40 ticks. The refresh rate is
 * dynamic, meaning it may change due to the dynamic texts refresh rate being dynamic.
 * The refresh rate is managed per each {@link PersistentActionBar} instance.</p>
 */
class BarSender implements Runnable {

    static final int MAX_REFRESH_RATE = 40 * 50;
    static final int MIN_REFRESH_RATE = 50;

    private static final Map<UUID, BarDistributor> _playerMap = new PlayerMap<>(Nucleus.getPlugin());
    private static final SetMap<ActionBar, PlayerBar> _barMap = new WeakHashSetMap<>(35, 3);
    private static final INmsActionBarHandler _nmsHandler;
    private static final Object _sync = new Object();
    static volatile BarSender _instance;

    static {
        _nmsHandler = NmsUtils.getActionBarHandler();

        if (_nmsHandler == null) {
            NucMsg.debug("Failed to get Action Bar NMS handler.");
        }
    }

    /**
     * Determine if the player is currently viewing a persistent action bar.
     *
     * @param player  The player to check.
     */
    static boolean isViewing(Player player) {
        synchronized (_sync) {
            return _playerMap.containsKey(player.getUniqueId());
        }
    }

    /**
     * Start the sender. Does nothing if already started.
     */
    static void start() {

        if (_instance != null || _nmsHandler == null)
            return;

        synchronized (_sync) {

            if (_instance != null)
                return;

            _instance = new BarSender();
            Scheduler.runTaskRepeat(Nucleus.getPlugin(), 1, MIN_REFRESH_RATE / 50, _instance);
        }
    }

    /**
     * Add a {@link PersistentActionBar} to show to a player.
     *
     * @param player     The player who will see the bar.
     * @param actionBar  The action bar to show.
     * @param duration   The duration value. Determines the minimum time slice the bar
     *                   is given when shown with other {@link PersistentActionBar}'s. If the
     *                   action bar is an instance of {@link TimedActionBar}, then duration
     *                   represents the time the bar is displayed before being automatically removed.
     * @param timeScale  The time scale value.
     * @param priority   The action bar priority.
     */
    static void addBar(Player player, PersistentActionBar actionBar,
                       int duration, TimeScale timeScale, ActionBarPriority priority) {

        if (_nmsHandler == null)
            return;

        PlayerBar playerBar = new PlayerBar(player, actionBar, duration, timeScale, priority);

        BarDistributor distributor = BarSender.getDistributor(player);

        synchronized (distributor._sync) {

            // ensure distributor does not already contain the playerBar
            if (distributor.contains(playerBar))
                return;

            switch (priority) {
                case DEFAULT:
                    if (distributor.priority.contains(ActionBarPriority.HIGH)) {
                        return;
                    }
                    break;
                case LOW:
                    if (distributor.priority.contains(ActionBarPriority.HIGH) ||
                            distributor.priority.contains(ActionBarPriority.DEFAULT)) {
                        return;
                    }
                    break;
            }

            // add playerBar to distributor
            distributor.add(playerBar, duration, timeScale);
        }

        synchronized (_sync) {
            // add to _barMap
            _barMap.put(actionBar, playerBar);
        }

        if(_instance == null && Bukkit.isPrimaryThread()) {
            start();
        }
    }

    /**
     * Remove a {@link PersistentActionBar} from a player view.
     *
     * @param player     The player.
     * @param actionBar  The action bar to remove.
     */
    static void removeBar(Player player, PersistentActionBar actionBar) {
        if (_nmsHandler == null)
            return;

        PlayerBar playerBar = new PlayerBar(player, actionBar, 0, null, null);
        BarDistributor distributor = BarSender.getDistributor(player);

        boolean isEmpty;

        synchronized (distributor._sync) {
            distributor.remove(playerBar);
            isEmpty = distributor.isEmpty();
        }

        synchronized (_sync) {

            if (isEmpty) {
                _playerMap.remove(player.getUniqueId());
            }

            _barMap.removeValue(actionBar, playerBar);
        }
    }

    /**
     * Remove a {@link PersistentActionBar} from a player view.
     *
     * @param actionBar  The action bar to remove.
     */
    static void removeBar(PersistentActionBar actionBar) {
        if (_nmsHandler == null)
            return;

        Set<PlayerBar> playerBars;

        synchronized (_sync) {
            playerBars = _barMap.removeAll(actionBar);
        }

        for (PlayerBar bar : playerBars) {
            removeBar(bar);
        }
    }

    /**
     * Remove a {@link PlayerBar}.
     *
     * @param bar  The player bar view instance to remove.
     */
    static void removeBar(PlayerBar bar) {
        if (_nmsHandler == null)
            return;

        BarDistributor distributor = BarSender.getDistributor(bar.player());
        boolean isEmpty;

        synchronized (distributor._sync) {
            distributor.remove(bar);
            distributor.priority.subtract(bar.priority());
            isEmpty = distributor.isEmpty();
        }

        if (isEmpty) {
            synchronized (_sync) {
                _playerMap.remove(bar.player().getUniqueId());
            }
        }
    }

    /**
     * Remove all action bars from a player.
     *
     * @param player  The player to remove the action bars from.
     */
    static void removePlayer(Player player) {
        if (_nmsHandler == null)
            return;

        BarDistributor distributor = _playerMap.get(player.getUniqueId());
        if (distributor == null)
            return;

        List<PlayerBar> bars = new ArrayList<>(distributor);

        for (PlayerBar bar : bars) {
            removeBar(player, bar.bar());
        }
    }

    /**
     * Get the players current distributor or create a new one.
     *
     * @param player  The player.
     */
    static BarDistributor getDistributor(Player player) {

        BarDistributor distributor;

        synchronized (_sync) {
            distributor = _playerMap.get(player.getUniqueId());
        }

        if (distributor == null) {

            synchronized (_sync) {
                distributor = new BarDistributor();
                _playerMap.put(player.getUniqueId(), distributor);
            }
        }

        return distributor;
    }

    /**
     * Get the current priority of the action bar being displayed to
     * a player, if any.
     *
     * @param player  The player.
     *
     * @return  The priority or {@link ActionBarPriority#LOW} if no action bars
     * are being displayed.
     */
    static ActionBarPriority getPriority(Player player) {

        BarDistributor distributor = getDistributor(player);
        if (distributor == null)
            return ActionBarPriority.LOW;

        synchronized (distributor._sync) {

            if (distributor.priority.contains(ActionBarPriority.HIGH))
                return ActionBarPriority.HIGH;
            else if (distributor.priority.contains(ActionBarPriority.DEFAULT))
                return ActionBarPriority.DEFAULT;
        }

        return ActionBarPriority.LOW;
    }


    @Override
    public void run() {

        List<BarDistributor> distributors;

        synchronized (_sync){
            if (_playerMap.isEmpty())
                return;

            // copy distributors to a new list to prevent concurrent modification errors.
            distributors = new ArrayList<>(_playerMap.values());
        }

        long now = System.currentTimeMillis();

        for (BarDistributor distributor : distributors) {

            PlayerBar playerBar;

            synchronized (distributor._sync) {
                // get the current action bar
                playerBar = distributor.current();
                if (playerBar == null)
                    continue;
            }

            // remove expired action bars
            if (playerBar.expires() > 0 && playerBar.expires() <= now) {
                removeBar(playerBar);
                NucMsg.debug("Removing Bar");
                continue;
            }

            // send action bar packet if time to update
            if (playerBar.nextUpdate() == 0 ||
                    playerBar.nextUpdate() <= System.currentTimeMillis()) {

                playerBar.send();
            }
        }
    }

    /**
     * Send an action bar packet to a player.
     *
     * @param player     The player.
     * @param actionBar  The action bar to send.
     *
     * @return  The next update time in milliseconds.
     */
    static long send(final Player player, ActionBar actionBar) {
        if (_nmsHandler == null)
            return 0;

        IDynamicText dynText = actionBar.getText();
        final String text = dynText.nextText();
        if (text != null) {

            if (Bukkit.isPrimaryThread()) {
                _nmsHandler.send(player, text);
            }
            else {

                Scheduler.runTaskSync(Nucleus.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        _nmsHandler.send(player, text);
                    }
                });

            }
        }

        int interval = dynText.getRefreshRate();
        if (interval <= 0) {
            interval = MAX_REFRESH_RATE;
        }
        else {
            interval = Math.min(interval * 50, MAX_REFRESH_RATE);
            interval = Math.max(interval, MIN_REFRESH_RATE);
        }

        return System.currentTimeMillis() + interval;
    }

    static class BarDistributor extends TimedDistributor<PlayerBar> {
        final Object _sync = new Object();

        final ElementCounter<ActionBarPriority> priority =
                new ElementCounter<ActionBarPriority>(RemovalPolicy.REMOVE);
    }
}