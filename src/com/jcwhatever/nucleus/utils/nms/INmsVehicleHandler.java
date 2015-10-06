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

package com.jcwhatever.nucleus.utils.nms;

import org.bukkit.entity.LivingEntity;

/**
 * Interface for NucleusFramework's Minecraft vehicle handler.
 *
 * @see NmsUtils
 */
public interface INmsVehicleHandler {

    /**
     * Get the forward motion applied by the specified passenger.
     *
     * @param passenger  The passenger.
     */
    float getForwardMotion(LivingEntity passenger);

    /**
     * Set the forward motion applied by the specified passenger.
     *
     * @param passenger  The passenger.
     * @param value      The forward motion value. 0 is no motion, negative values are reverse,
     *                   positive values are forward.
     */
    void setForwardMotion(LivingEntity passenger, float value);

    /**
     * Get the lateral motion applied by the specified passenger.
     *
     * @param passenger  The passenger.
     */
    float getLateralMotion(LivingEntity passenger);

    /**
     * Set the lateral motion applied by the specified passenger.
     *
     * @param passenger  The passenger.
     * @param value      The lateral motion value. 0 is no motion, negative values are left,
     *                   positive values are right.
     */
    void setLateralMotion(LivingEntity passenger, float value);

    /**
     * Determine if the specified passenger is pressing the move forward button (W).
     *
     * @param passenger  The passenger.
     */
    boolean isForwardPressed(LivingEntity passenger);

    /**
     * Determine if the specified passenger is pressing the move backwards button (S).
     *
     * @param passenger  The passenger.
     */
    boolean isBackwardPressed(LivingEntity passenger);

    /**
     * Determine if the specified passenger is pressing the move left button (A).
     *
     * @param passenger  The passenger.
     */
    boolean isLeftPressed(LivingEntity passenger);

    /**
     * Determine if the specified passenger is pressing the move right button (D).
     *
     * @param passenger  The passenger.
     */
    boolean isRightPressed(LivingEntity passenger);

    /**
     * Determine if the specified passenger is pressing the jump button (SPACE).
     *
     * @param passenger  The passenger.
     */
    boolean isJumpPressed(LivingEntity passenger);

    /**
     * Determine if the specified passenger is pressing the jump button (SPACE).
     *
     * @param passenger  The passenger.
     * @param isPressed  True to set jump flag to pressed, otherwise false.
     */
    void setJumpPressed(LivingEntity passenger, boolean isPressed);

    /**
     * Determine if the specified passenger is pressing the dismount button (L.SHIFT)
     *
     * @param passenger  The passenger.
     */
    boolean isDismountPressed(LivingEntity passenger);

    /**
     * Set the specified passengers dismount flag.
     *
     * @param passenger  The passenger.
     * @param isPressed  True to set dismount pressed, otherwise false.
     */
    void setDismountPressed(LivingEntity passenger, boolean isPressed);

    /**
     * Determine if the specified passenger is allowed to dismount vehicle.
     *
     * @param passenger  The passenger.
     */
    boolean canDismount(LivingEntity passenger);

    /**
     * Set flag that allows passenger to dismount vehicle.
     *
     * @param passenger    The passenger.
     * @param canDismount  True to allow dismounting from vehicle, otherwise false.
     */
    void setCanDismount(LivingEntity passenger, boolean canDismount);
}
