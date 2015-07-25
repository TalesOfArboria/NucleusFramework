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
import com.jcwhatever.nucleus.events.spawnegg.PreSpawnEggEvent;
import com.jcwhatever.nucleus.events.spawnegg.SpawnEggEvent;
import com.jcwhatever.nucleus.managed.entity.mob.ISerializableMob;
import com.jcwhatever.nucleus.managed.entity.mob.Mobs;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SpawnEggListener implements Listener {

    private static final Location LOCATION = new Location(null, 0, 0, 0);

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void eggListener(PlayerInteractEvent event) {

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        ItemStack inHand = event.getItem();
        if (inHand == null || inHand.getType() != Material.MONSTER_EGG)
            return;

        Player player = event.getPlayer();

        PreSpawnEggEvent preSpawnEggEvent = new PreSpawnEggEvent(player, inHand);
        Nucleus.getEventManager().callBukkit(this, preSpawnEggEvent);

        if (preSpawnEggEvent.isCancelled()) {
            event.setUseItemInHand(Event.Result.DENY);
            event.setCancelled(true);
            return;
        }

        ISerializableMob mob = Mobs.deserialize(inHand);
        if (mob == null)
            return;

        Location location = event.getClickedBlock().getLocation(LOCATION).add(0, 1, 0);
        Entity entity = mob.spawn(location);

        event.setUseItemInHand(Event.Result.DENY);
        event.setCancelled(true);

        SpawnEggEvent spawnEggEvent = new SpawnEggEvent(player, inHand, entity);
        Nucleus.getEventManager().callBukkit(this, spawnEggEvent);

        if (player.getGameMode() == GameMode.SURVIVAL) {
            if (inHand.getAmount() <= 1) {
                inHand = null;
            }
            else {
                inHand.setAmount(inHand.getAmount() - 1);
            }
            player.getInventory().setItemInHand(inHand);
        }
    }
}
