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

/**
 * DataWatcher wrapper interface.
 */
public interface IDataWatcher {

    /**
     * Determine if the entity is allowed to dismount a vehicle.
     */
    boolean canDismount();

    /**
     * Set if the entity is allowed to dismount the vehicle.
     *
     * @param canDismount  True to allow dismount, otherwise false.
     */
    void setCanDismount(boolean canDismount);

    /**
     * Determine if the player is pressing the dismount button (L.SHIFT)
     */
    boolean isDismountPressed();

    /**
     * Set player pressing dismount button.
     *
     * @param isPressed  True if pressed, otherwise false.
     */
    void setDismountPressed(boolean isPressed);

    /**
     * Remove arrows. (Player)
     */
    void removeArrows();

    /**
     * Determine if arrows can get stuck in the player. (Player)
     */
    boolean canArrowsStick();

    /**
     * Set allow arrows to get stuck in an entity.
     *
     * @param isAllowed  True to allow arrows to get stuck.
     */
    void setCanArrowsStick(boolean isAllowed);
}
