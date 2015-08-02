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

package com.jcwhatever.nucleus.events.manager;

import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.utils.PreCon;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockExpEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreeperPowerEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.EntityPortalExitEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.HorseJumpEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PigZapEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.SheepDyeWoolEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
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
import org.bukkit.event.player.PlayerEvent;
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
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.event.world.SpawnChangeEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;

/**
 * Used to forward Bukkit events.
 *
 * <p>The forwarder listens to events that are registered via {@link #register}
 * and forwards the events to once of the appropriate abstract methods.</p>
 *
 * <p>An event forwarder can be used to forward the received Bukkit events to
 * another event manager instance that is used in a context that might be
 * interested in the event.</p>
 *
 * <p>If you need to unregister the forwarder, invoke the {@link #dispose} method.</p>
 */
public abstract class BukkitEventForwarder implements IPluginOwned, IDisposable {

    private final Plugin _plugin;
    private final Forwarder _forwarder;
    private final EventPriority _priority;
    private final Listener _dummyListener = new Listener() {};
    private final EventExecutor _executor = new EventExecutor() {
        @Override
        public void execute(Listener listener, Event event) throws EventException {
            _forwarder.on(event);
        }
    };
    private final Set<Class<? extends Event>> _registered = new HashSet<>(35);

    private boolean _isDisposed;

    /**
     * Constructor.
     *
     * @param plugin        The owning plugin.
     * @param priority      The priority of the forwarded events.
     */
    public BukkitEventForwarder(Plugin plugin, EventPriority priority) {
        PreCon.notNull(plugin);
        PreCon.notNull(priority);

        _plugin = plugin;
        _priority = priority;
        _forwarder = new Forwarder();
    }

    /**
     * Register an event to forward.
     *
     * @param event  The event to forward.
     */
    public void register(Class<? extends Event> event) {
        if (_registered.contains(event))
            return;

        _registered.add(event);
        Bukkit.getPluginManager().registerEvent(event, _dummyListener, _priority, _executor, _plugin, false);
    }

    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {

        _isDisposed = true;

        HandlerList.unregisterAll(_dummyListener);
        _registered.clear();
    }

    /**
     * Invoked whenever a registered event is called.
     *
     * @param event  The called event
     */
    protected abstract void onEvent(Event event);

    /**
     * Invoked whenever a registered block event is called.
     *
     * <p>Intended for optional override.</p>
     *
     * @param event  The called event.
     */
    protected void onBlockEvent(BlockEvent event) {}

    /**
     * Invoked whenever a registered player event is called.
     *
     * <p>Intended for optional override.</p>
     *
     * @param event  The called event.
     */
    protected void onPlayerEvent(PlayerEvent event) {}

    /**
     * Invoked whenever a registered inventory event is called.
     *
     * <p>Intended for optional override.</p>
     *
     * @param event  The called event.
     */
    protected void onInventoryEvent(InventoryEvent event) {}

    /**
     * Invoked whenever a registered hanging event is called.
     *
     * <p>Intended for optional override.</p>
     *
     * @param event  The called event.
     */
    protected void onHangingEvent(HangingEvent event) {}

    /**
     * Invoked whenever a registered vehicle event is called.
     *
     * <p>Intended for optional override.</p>
     *
     * @param event  The called event.
     */
    protected void onVehicleEvent(VehicleEvent event) {}

    /**
     * Invoked whenever a registered entity event is called.
     *
     * <p>Intended for optional override.</p>
     *
     * @param event  The called event.
     */
    protected void onEntityEvent(EntityEvent event) {}

    public class Forwarder {

        public void on(Object e) {

            if (!(e instanceof Event))
                return;

            Event event = (Event) e;

            onEvent(event);

            if (event instanceof PlayerEvent) {
                onPlayerEvent((PlayerEvent)event);
            }
            else if (event instanceof BlockEvent) {
                onBlockEvent((BlockEvent)event);
            }
            else if (event instanceof HangingEvent) {
                onHangingEvent((HangingEvent)event);
            }
            else if (event instanceof InventoryEvent) {
                onInventoryEvent((InventoryEvent) event);
            }
            else if (event instanceof VehicleEvent) {
                onVehicleEvent((VehicleEvent) event);
            }
            else if (event instanceof EntityEvent) {
                onEntityEvent((EntityEvent) event);
            }
        }
    }

    private static class RegistrationHelper {

        BukkitEventForwarder forwarder;

        RegistrationHelper(BukkitEventForwarder forwarder) {
            this.forwarder = forwarder;
        }

        RegistrationHelper reg(Class<? extends Event> event) {
            forwarder.register(event);
            return this;
        }
    }

    /**
     * Register Bukkit events with the forwarder.
     *
     * @param forwarder  The forwarder to register the events on.
     */
    public static void registerBukkitEvents(BukkitEventForwarder forwarder) {

        new RegistrationHelper(forwarder)

                /* Block events */
                .reg(BlockBreakEvent.class)
                .reg(BlockBurnEvent.class)
                .reg(BlockCanBuildEvent.class)
                .reg(BlockDamageEvent.class)
                .reg(BlockDispenseEvent.class)
                .reg(BlockExpEvent.class)
                .reg(BlockFadeEvent.class)
                .reg(BlockFormEvent.class)
                .reg(BlockFromToEvent.class)
                .reg(BlockGrowEvent.class)
                .reg(BlockIgniteEvent.class)
                .reg(BlockMultiPlaceEvent.class)
                .reg(BlockPhysicsEvent.class)
                .reg(BlockPistonExtendEvent.class)
                .reg(BlockPistonRetractEvent.class)
                .reg(BlockPlaceEvent.class)
                .reg(BlockRedstoneEvent.class)
                .reg(BlockSpreadEvent.class)
                .reg(EntityBlockFormEvent.class)
                .reg(LeavesDecayEvent.class)
                .reg(NotePlayEvent.class)
                .reg(SignChangeEvent.class)

                /* Enchantment events */
                .reg(EnchantItemEvent.class)
                .reg(PrepareItemEnchantEvent.class)

                /* Entity events*/
                .reg(CreatureSpawnEvent.class)
                .reg(CreeperPowerEvent.class)
                .reg(EntityBreakDoorEvent.class)
                .reg(EntityChangeBlockEvent.class)
                .reg(EntityCombustByBlockEvent.class)
                .reg(EntityCombustByEntityEvent.class)
                .reg(EntityCombustEvent.class)
                .reg(EntityCreatePortalEvent.class)
                .reg(EntityDamageByBlockEvent.class)
                .reg(EntityDamageByEntityEvent.class)
                .reg(EntityDamageEvent.class)
                .reg(EntityDeathEvent.class)
                .reg(EntityExplodeEvent.class)
                .reg(EntityInteractEvent.class)
                .reg(EntityPortalEnterEvent.class)
                .reg(EntityPortalEvent.class)
                .reg(EntityPortalExitEvent.class)
                .reg(EntityRegainHealthEvent.class)
                .reg(EntityShootBowEvent.class)
                .reg(EntityTameEvent.class)
                .reg(EntityTargetEvent.class)
                .reg(EntityTargetLivingEntityEvent.class)
                .reg(EntityTeleportEvent.class)
                .reg(EntityUnleashEvent.class)
                .reg(ExpBottleEvent.class)
                .reg(ExplosionPrimeEvent.class)
                .reg(FoodLevelChangeEvent.class)
                .reg(HorseJumpEvent.class)
                .reg(ItemDespawnEvent.class)
                .reg(ItemSpawnEvent.class)
                .reg(PigZapEvent.class)
                .reg(PlayerDeathEvent.class)
                .reg(PlayerLeashEntityEvent.class)
                .reg(PotionSplashEvent.class)
                .reg(ProjectileHitEvent.class)
                .reg(ProjectileLaunchEvent.class)
                .reg(SheepDyeWoolEvent.class)
                .reg(SlimeSplitEvent.class)

                /* Hanging events*/
                .reg(HangingBreakByEntityEvent.class)
                .reg(HangingBreakEvent.class)
                .reg(HangingPlaceEvent.class)

                /* Inventory events */
                .reg(BrewEvent.class)
                .reg(CraftItemEvent.class)
                .reg(FurnaceBurnEvent.class)
                .reg(FurnaceExtractEvent.class)
                .reg(FurnaceSmeltEvent.class)
                .reg(InventoryClickEvent.class)
                .reg(InventoryCloseEvent.class)
                .reg(InventoryCreativeEvent.class)
                .reg(InventoryDragEvent.class)
                .reg(InventoryInteractEvent.class)
                .reg(InventoryMoveItemEvent.class)
                .reg(InventoryOpenEvent.class)
                .reg(InventoryPickupItemEvent.class)
                .reg(PrepareItemCraftEvent.class)

                /* Player events*/
                .reg(PlayerAnimationEvent.class)
                .reg(PlayerBedEnterEvent.class)
                .reg(PlayerBedLeaveEvent.class)
                .reg(PlayerBucketEmptyEvent.class)
                .reg(PlayerBucketFillEvent.class)
                .reg(PlayerChangedWorldEvent.class)
                .reg(PlayerChatTabCompleteEvent.class)
                .reg(PlayerCommandPreprocessEvent.class)
                .reg(PlayerDropItemEvent.class)
                .reg(PlayerEditBookEvent.class)
                .reg(PlayerEggThrowEvent.class)
                .reg(PlayerExpChangeEvent.class)
                .reg(PlayerFishEvent.class)
                .reg(PlayerGameModeChangeEvent.class)
                .reg(PlayerInteractEntityEvent.class)
                .reg(PlayerInteractEvent.class)
                .reg(PlayerItemBreakEvent.class)
                .reg(PlayerItemConsumeEvent.class)
                .reg(PlayerItemHeldEvent.class)
                .reg(PlayerJoinEvent.class)
                .reg(PlayerKickEvent.class)
                .reg(PlayerLevelChangeEvent.class)
                .reg(PlayerLoginEvent.class)
                .reg(PlayerMoveEvent.class)
                .reg(PlayerPickupItemEvent.class)
                .reg(PlayerPortalEvent.class)
                .reg(PlayerQuitEvent.class)
                .reg(PlayerRegisterChannelEvent.class)
                .reg(PlayerRespawnEvent.class)
                .reg(PlayerShearEntityEvent.class)
                .reg(PlayerStatisticIncrementEvent.class)
                .reg(PlayerTeleportEvent.class)
                .reg(PlayerToggleFlightEvent.class)
                .reg(PlayerToggleSneakEvent.class)
                .reg(PlayerToggleSprintEvent.class)
                .reg(PlayerUnleashEntityEvent.class)
                .reg(PlayerUnregisterChannelEvent.class)
                .reg(PlayerVelocityEvent.class)

                /* Vehicle events */
                .reg(VehicleCreateEvent.class)
                .reg(VehicleDamageEvent.class)
                .reg(VehicleDestroyEvent.class)
                .reg(VehicleEnterEvent.class)
                .reg(VehicleEntityCollisionEvent.class)
                .reg(VehicleExitEvent.class)
                .reg(VehicleMoveEvent.class)
                .reg(VehicleUpdateEvent.class)

                /* Weather events */
                .reg(LightningStrikeEvent.class)
                .reg(ThunderChangeEvent.class)
                .reg(WeatherChangeEvent.class)

                /* World events */
                .reg(ChunkLoadEvent.class)
                .reg(ChunkPopulateEvent.class)
                .reg(ChunkUnloadEvent.class)
                .reg(PortalCreateEvent.class)
                .reg(SpawnChangeEvent.class)
                .reg(StructureGrowEvent.class)
                .reg(WorldInitEvent.class)
                .reg(WorldLoadEvent.class)
                .reg(WorldSaveEvent.class)
                .reg(WorldUnloadEvent.class);
    }
}
