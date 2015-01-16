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
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.event.player.PlayerUnregisterChannelEvent;
import org.bukkit.event.player.PlayerVelocityEvent;

public final class PlayerListener implements Listener {

    @EventHandler
    private void onPlayerAchievmentAwarded(PlayerAchievementAwardedEvent event) {

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onPlayerAnimation(PlayerAnimationEvent event) {

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onPlayerBedEnter(PlayerBedEnterEvent event) {

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onPlayerBedLeave(PlayerBedLeaveEvent event) {

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onPlayerBucketFill(PlayerBucketFillEvent event) {

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onPlayerChangedWorld(PlayerChangedWorldEvent event) {

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onPlayerChat(PlayerChatTabCompleteEvent event) {

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onPlayerDropItem(PlayerDropItemEvent event) {

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onPlayerEditBook(PlayerEditBookEvent event) {

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onPlayerEggThrow(PlayerEggThrowEvent event) {

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onPlayerExpChange(PlayerExpChangeEvent event) {

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onPlayerFish(PlayerFishEvent event) {

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onPlayerInteractEntity(PlayerInteractEntityEvent event) {

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event) {

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onPlayerItemBreak(PlayerItemBreakEvent event) {

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onPlayerItemConsume(PlayerItemConsumeEvent event) {

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onPlayerItemHeld(PlayerItemHeldEvent event) {

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onPlayerKick(PlayerKickEvent event) {

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onPlayerLevelChange(PlayerLevelChangeEvent event) {

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onPlayerLogin(PlayerLoginEvent event) {

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event) {

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onPlayerPickupItem(PlayerPickupItemEvent event) {

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onPlayerPortal(PlayerPortalEvent event) {

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onPlayerRegisterChannel(PlayerRegisterChannelEvent event) {

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onPlayerRespawn(PlayerRespawnEvent event) {

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onPlayerShearEntity(PlayerShearEntityEvent event) {

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onPlayerStatisticIncrement(PlayerStatisticIncrementEvent event) {

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onPlayerTeleport(PlayerTeleportEvent event) {

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onPlayerToggleFlight(PlayerToggleFlightEvent event) {

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onPlayerToggleSneak(PlayerToggleSneakEvent event) {

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onPlayerToggleSprint(PlayerToggleSprintEvent event) {

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onPlayerUnleashEntity(PlayerUnleashEntityEvent event) {

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onPlayerUnregisterChannel(PlayerUnregisterChannelEvent event) {

        Nucleus.getEventManager().call(this, event);
    }

    @EventHandler
    private void onPlayerVelocity(PlayerVelocityEvent event) {

        Nucleus.getEventManager().call(this, event);
    }
}
