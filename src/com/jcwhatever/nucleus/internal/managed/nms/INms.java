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

import com.jcwhatever.nucleus.managed.reflection.IReflectedInstance;
import com.jcwhatever.nucleus.managed.reflection.IReflection;
import com.jcwhatever.nucleus.utils.nms.INmsParticleEffectHandler;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

/**
 * Interface for a lower level NMS handler designed for a specific
 * Minecraft version.
 */
interface INms {

    /**
     * Determine if the instance is available for use.
     */
    boolean isAvailable();

    /**
     * Set the instances availability state.
     */
    void setAvailable(boolean isAvailable);

    /**
     * Get reflection.
     */
    IReflection getReflection();

    /**
     * Send an NMS packet.
     *
     * @param player  The player to send the packet to.
     * @param packet  The packet to send.
     */
    void sendPacket(Player player, Object packet);

    /**
     * Send an NMS packet.
     *
     * @param connection  The connection of the player to send the packet to.
     * @param packet      The packet to send.
     */
    void sendPacket(IReflectedInstance connection, Object packet);

    /**
     * Get the players NMS connection.
     *
     * @param player The player.
     */
    IReflectedInstance getConnection(Player player);

    /**
     * Get the net.minecraft.server.EntityPlayer nms object of the player.
     *
     * @param player  The player.
     */
    IReflectedInstance getEntityPlayer(Player player);


    /**
     * Get a new title packet instance for setting title times.
     *
     * @param fadeIn   The fade in delay.
     * @param stay     The stay delay.
     * @param fadeOut  The fade out delay.
     */
    Object getTitlePacketTimes(int fadeIn, int stay, int fadeOut);

    /**
     * Get a new title packet instance for setting the sub title.
     *
     * @param subTitle  The sub title.
     */
    Object getTitlePacketSub(String subTitle);

    /**
     * Get a new title packet instance for setting the title.
     *
     * @param title  The title.
     */
    Object getTitlePacket(String title);

    /**
     * Get a new named sound effect packet instance.
     *
     * @param soundName  The name of the sound.
     * @param x          The source X coordinate.
     * @param y          The source Y coordinate.
     * @param z          The source Z coordinate.
     * @param volume     The volume.
     * @param pitch      The pitch.
     */
    Object getNamedSoundPacket(String soundName,
                                      double x, double y, double z, float volume, float pitch);

    /**
     * Get a new particle packet instance.
     *
     * @param particleType  The particle type.
     * @param force         True to force player to see the particle even if particle settings are on off.
     * @param x             The X coordinate.
     * @param y             The Y coordinate.
     * @param z             The Z coordinate
     * @param offsetX       The X data.
     * @param offsetY       The Y data.
     * @param offsetZ       The Z data.
     * @param data          Extra data.
     * @param count         The particle count.
     */
    Object getParticlePacket(
            INmsParticleEffectHandler.INmsParticleType particleType, boolean force,
            double x, double y, double z,
            double offsetX, double offsetY, double offsetZ,
            float data, int count);

    /**
     * Get a new list header footer title packet instance.
     *
     * @param headerText  The header text.
     * @param footerText  The footer text.
     */
    Object getHeaderFooterPacket(@Nullable String headerText, @Nullable String footerText);


    /**
     * Get a new action bar packet instance.
     *
     * @param text  The action bar text.
     */
    Object getActionBarPacket(String text);
}
