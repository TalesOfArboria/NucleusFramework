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

import com.jcwhatever.bukkit.generic.events.GenericsEventManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.vehicle.VehicleUpdateEvent;

public class VehicleListener implements Listener {

    @EventHandler
    private void onVehicleCreate(VehicleCreateEvent event) {

        GenericsEventManager.getGlobal().call(event);
    }

    @EventHandler
    private void onVehicleDamage(VehicleDamageEvent event) {

        GenericsEventManager.getGlobal().call(event);
    }

    @EventHandler
    private void onVehicleDestroy(VehicleDestroyEvent event) {

        GenericsEventManager.getGlobal().call(event);
    }

    @EventHandler
    private void onVehicleEnter(VehicleEnterEvent event) {

        GenericsEventManager.getGlobal().call(event);
    }

    @EventHandler
    private void onVehicleEntityCollision(VehicleEntityCollisionEvent event) {

        GenericsEventManager.getGlobal().call(event);
    }

    @EventHandler
    private void onVehicleExit(VehicleExitEvent event) {

        GenericsEventManager.getGlobal().call(event);
    }

    @EventHandler
    private void onVehicleMove(VehicleMoveEvent event) {

        GenericsEventManager.getGlobal().call(event);
    }

    @EventHandler
    private void onVehicleUpdate(VehicleUpdateEvent event) {

        GenericsEventManager.getGlobal().call(event);
    }

}
