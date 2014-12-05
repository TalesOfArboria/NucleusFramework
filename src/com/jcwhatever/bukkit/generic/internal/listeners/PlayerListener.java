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


package com.jcwhatever.bukkit.generic.internal.listeners;

import com.jcwhatever.bukkit.generic.GenericsLib;

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

public class PlayerListener implements Listener {

    @EventHandler
    private void onPlayerAchievmentAwarded(PlayerAchievementAwardedEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onPlayerAnimation(PlayerAnimationEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onPlayerBedEnter(PlayerBedEnterEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onPlayerBedLeave(PlayerBedLeaveEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onPlayerBucketFill(PlayerBucketFillEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onPlayerChangedWorld(PlayerChangedWorldEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onPlayerChat(PlayerChatTabCompleteEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onPlayerDropItem(PlayerDropItemEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onPlayerEditBook(PlayerEditBookEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onPlayerEggThrow(PlayerEggThrowEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onPlayerExpChange(PlayerExpChangeEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onPlayerFish(PlayerFishEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onPlayerInteractEntity(PlayerInteractEntityEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onPlayerItemBreak(PlayerItemBreakEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onPlayerItemConsume(PlayerItemConsumeEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onPlayerItemHeld(PlayerItemHeldEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onPlayerKick(PlayerKickEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onPlayerLevelChange(PlayerLevelChangeEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onPlayerLogin(PlayerLoginEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onPlayerPickupItem(PlayerPickupItemEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onPlayerPortal(PlayerPortalEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onPlayerRegisterChannel(PlayerRegisterChannelEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onPlayerRespawn(PlayerRespawnEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onPlayerShearEntity(PlayerShearEntityEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onPlayerStatisticIncrement(PlayerStatisticIncrementEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onPlayerTeleport(PlayerTeleportEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onPlayerToggleFlight(PlayerToggleFlightEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onPlayerToggleSneak(PlayerToggleSneakEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onPlayerToggleSprint(PlayerToggleSprintEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onPlayerUnleashEntity(PlayerUnleashEntityEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onPlayerUnregisterChannel(PlayerUnregisterChannelEvent event) {

        GenericsLib.getEventManager().call(event);
    }

    @EventHandler
    private void onPlayerVelocity(PlayerVelocityEvent event) {

        GenericsLib.getEventManager().call(event);
    }
}
