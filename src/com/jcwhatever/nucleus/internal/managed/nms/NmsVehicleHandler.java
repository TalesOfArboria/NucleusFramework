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

package com.jcwhatever.nucleus.internal.managed.nms;

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.nms.INmsVehicleHandler;
import org.bukkit.entity.LivingEntity;

/**
 * Minecraft vehicle handler.
 */
class NmsVehicleHandler extends AbstractNMSHandler implements INmsVehicleHandler {

    @Override
    public float getForwardMotion(LivingEntity passenger) {
        PreCon.notNull(passenger);

        return nms().getVehicleForwardMotion(passenger);
    }

    @Override
    public void setForwardMotion(LivingEntity passenger, float value) {
        PreCon.notNull(passenger);

        nms().setVehicleForwardMotion(passenger, value);
    }

    @Override
    public float getLateralMotion(LivingEntity passenger) {
        PreCon.notNull(passenger);

        return nms().getVehicleLateralMotion(passenger);
    }

    @Override
    public void setLateralMotion(LivingEntity passenger, float value) {
        PreCon.notNull(passenger);

        nms().setVehicleLateralMotion(passenger, value);
    }

    @Override
    public boolean isForwardPressed(LivingEntity player) {
        PreCon.notNull(player);

        return nms().getVehicleForwardMotion(player) > 0;
    }

    @Override
    public boolean isBackwardPressed(LivingEntity passenger) {
        PreCon.notNull(passenger);

        return nms().getVehicleForwardMotion(passenger) < 0;
    }

    @Override
    public boolean isLeftPressed(LivingEntity passenger) {
        PreCon.notNull(passenger);

        return nms().getVehicleLateralMotion(passenger) > 0;
    }

    @Override
    public boolean isRightPressed(LivingEntity passenger) {
        PreCon.notNull(passenger);

        return nms().getVehicleLateralMotion(passenger) < 0;
    }

    @Override
    public boolean isJumpPressed(LivingEntity passenger) {
        PreCon.notNull(passenger);

        return nms().isVehicleJumpPressed(passenger);
    }

    @Override
    public void setJumpPressed(LivingEntity passenger, boolean isPressed) {
        PreCon.notNull(passenger);

        nms().setVehicleJumpPressed(passenger, isPressed);
    }

    @Override
    public boolean isDismountPressed(LivingEntity player) {
        PreCon.notNull(player);

        return nms().isVehicleDismountPressed(player);
    }

    @Override
    public void setDismountPressed(LivingEntity passenger, boolean isPressed) {
        PreCon.notNull(passenger);

        nms().setVehicleDismountPressed(passenger, isPressed);
    }

    @Override
    public boolean canDismount(LivingEntity passenger) {
        PreCon.notNull(passenger);

        return nms().canDismount(passenger);
    }

    @Override
    public void setCanDismount(LivingEntity passenger, boolean canDismount) {
        PreCon.notNull(passenger);

        nms().setCanDismount(passenger, canDismount);
    }
}
