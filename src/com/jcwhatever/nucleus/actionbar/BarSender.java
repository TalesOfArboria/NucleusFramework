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

package com.jcwhatever.nucleus.actionbar;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.Nucleus.NmsHandlers;
import com.jcwhatever.nucleus.collections.SetMap;
import com.jcwhatever.nucleus.collections.WeakHashSetMap;
import com.jcwhatever.nucleus.collections.players.PlayerMap;
import com.jcwhatever.nucleus.collections.timed.TimeScale;
import com.jcwhatever.nucleus.collections.timed.TimedDistributor;
import com.jcwhatever.nucleus.internal.NucMsg;
import com.jcwhatever.nucleus.nms.INmsActionBarHandler;
import com.jcwhatever.nucleus.utils.Scheduler;
import com.jcwhatever.nucleus.utils.text.SimpleJSONBuilder;
import com.jcwhatever.nucleus.utils.text.dynamic.IDynamicText;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Sends action bars to players and manages {@code PersistentActionBar}'s
 * per player.
 *
 * <p>Also manages packet send timing to reduce network traffic and client side lag
 * based on the max refresh rate required to persist the bar and the minimum
 * refresh rate preferred by the action bars dynamic text. The absolute minimum
 * refresh rate is 1 tick while the absolute max is 40 ticks. The refresh rate is
 * dynamic, meaning it may change due to the dynamic texts refresh rate being dynamic.
 * The refresh rate is managed per each {@code PersistentActionBar} instance.</p>
 */
class BarSender implements Runnable {

    static final int MAX_REFRESH_RATE = 40 * 50;
    static final int MIN_REFRESH_RATE = 50;

    static Map<UUID, TimedDistributor<PlayerBar>> _playerMap = new PlayerMap<>(Nucleus.getPlugin());
    static SetMap<ActionBar, PlayerBar> _barMap = new WeakHashSetMap<>(35, 3);
    static INmsActionBarHandler _nmsHandler;
    static BarSender _instance;

    static {
        _nmsHandler = Nucleus.getNmsManager().getNmsHandler(NmsHandlers.ACTION_BAR.name());

        if (_nmsHandler == null) {
            NucMsg.debug("Failed to get Action Bar NMS handler.");
        }
    }

    /**
     * Start the sender. Does nothing if already started.
     */
    static void start() {
        if (_instance != null || _nmsHandler == null)
            return;

        _instance = new BarSender();
        Scheduler.runTaskRepeat(Nucleus.getPlugin(), 1, MIN_REFRESH_RATE / 50, _instance);
    }

    /**
     * Add a {@code PersistentActionBar} to show to a player.
     *
     * @param player     The player who will see the bar.
     * @param actionBar  The action bar to show.
     * @param duration   The duration value. Determines the minimum time slice the bar
     *                   is given when shown with other {@code PersistentActionBar}'s. If the
     *                   action bar is an instance of {@code TimedActionBar}, then duration
     *                   represents the time the bar is displayed before being automatically removed.
     * @param timeScale  The time scale value.
     */
    static void addBar(Player player, PersistentActionBar actionBar,
                       int duration, TimeScale timeScale) {

        if (_nmsHandler == null)
            return;

        TimedDistributor<PlayerBar> distributor = BarSender.getDistributor(player);

        PlayerBar playerBar = new PlayerBar(player, actionBar, duration, timeScale);

        if (distributor.contains(playerBar))
            return;

        distributor.add(playerBar, duration, timeScale);

        _barMap.put(actionBar, playerBar);

        start();
    }

    /**
     * Remove a {@code PersistentActionBar} from a player view.
     *
     * @param player     The player.
     * @param actionBar  The action bar to remove.
     */
    static void removeBar(Player player, PersistentActionBar actionBar) {
        if (_nmsHandler == null)
            return;

        PlayerBar playerBar = new PlayerBar(player, actionBar, 0, null);

        TimedDistributor<PlayerBar> distributor = BarSender.getDistributor(player);
        distributor.remove(playerBar);

        if (distributor.isEmpty())
            _playerMap.remove(player.getUniqueId());

        _barMap.removeValue(actionBar, playerBar);
    }

    /**
     * Remove a {@code PersistentActionBar} from a player view.
     *
     * @param actionBar  The action bar to remove.
     */
    static void removeBar(PersistentActionBar actionBar) {
        if (_nmsHandler == null)
            return;

        Set<PlayerBar> playerBars =  _barMap.removeAll(actionBar);

        for (PlayerBar bar : playerBars) {
            removeBar(bar);
        }
    }

    /**
     * Remove a {@code PlayerBar}.
     *
     * @param bar  The player bar view instance to remove.
     */
    static void removeBar(PlayerBar bar) {
        if (_nmsHandler == null)
            return;

        TimedDistributor<PlayerBar> distributor = BarSender.getDistributor(bar.player);
        distributor.remove(bar);

        if (distributor.isEmpty())
            _playerMap.remove(bar.player.getUniqueId());
    }

    /**
     * Get the players current distributor or create a new one.
     *
     * @param player  The player.
     */
    static TimedDistributor<PlayerBar> getDistributor(Player player) {

        TimedDistributor<PlayerBar> distributor = _playerMap.get(player.getUniqueId());

        if (distributor == null) {
            distributor = new TimedDistributor<>();
            _playerMap.put(player.getUniqueId(), distributor);
        }

        return distributor;
    }


    @Override
    public void run() {

        if (_playerMap.isEmpty())
            return;

        // copy distributors to a new list to prevent concurrent modification errors.
        List<TimedDistributor<PlayerBar>> distributors = new ArrayList<>(_playerMap.values());

        long now = System.currentTimeMillis();

        for (TimedDistributor<PlayerBar> distributor : distributors) {

            // get the current action bar
            PlayerBar playerBar = distributor.current();
            if (playerBar == null)
                continue;

            // remove expired action bars
            if (playerBar.expires > 0 && playerBar.expires <= now) {
                removeBar(playerBar);
                NucMsg.debug("Removing Bar");
                continue;
            }

            // send action bar packet if time to update
            if (playerBar.nextUpdate == 0 ||
                    playerBar.nextUpdate <= System.currentTimeMillis()) {

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
    static long send(Player player, ActionBar actionBar) {
        if (_nmsHandler == null)
            return 0;

        IDynamicText dynText = actionBar.getText();
        String text = dynText.nextText();
        if (text != null) {
            _nmsHandler.send(player, SimpleJSONBuilder.text(text));
        }

        int interval = Math.min(dynText.getRefreshRate() * 50, MAX_REFRESH_RATE);
        interval = Math.max(interval, MIN_REFRESH_RATE);

        return System.currentTimeMillis() + interval;
    }

    public static class PlayerBar {
        final PersistentActionBar bar;
        final Player player;
        final long expires;
        long nextUpdate;


        PlayerBar(Player player, PersistentActionBar bar, int duration, @Nullable TimeScale timeScale) {
            this.player = player;
            this.bar = bar;

            expires = timeScale != null && bar instanceof TimedActionBar
                    ? System.currentTimeMillis() + (duration * timeScale.getTimeFactor())
                    : 0;
        }

        private void send() {
            nextUpdate = BarSender.send(player, bar);
        }

        @Override
        public int hashCode() {
            return bar.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof PlayerBar && ((PlayerBar) obj).bar.equals(bar);
        }
    }
}