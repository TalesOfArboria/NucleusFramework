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

import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Interface for NucleusFramework's Minecraft Named Sound Effect handler.
 *
 * @see NmsUtils
 */
public interface INmsSoundEffectHandler extends INmsHandler {

    /**
     * Send a named sound effect to a collection of players.
     *
     * @param players    The players to send the sound to.
     * @param soundName  The name of the sound.
     * @param x          The X coordinates to play the sound at.
     * @param y          The Y coordinates to play the sound at.
     * @param z          The Z coordinates to play the sound at.
     * @param volume     The volume of the sound.
     * @param pitch      The sound pitch.
     */
    void send(Collection<? extends Player> players,
              String soundName, double x, double y, double z, float volume, float pitch);
}
