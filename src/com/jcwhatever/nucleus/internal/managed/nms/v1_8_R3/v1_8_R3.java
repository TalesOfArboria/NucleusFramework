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

package com.jcwhatever.nucleus.internal.managed.nms.v1_8_R3;

import com.jcwhatever.nucleus.managed.reflection.IReflectedInstance;
import com.jcwhatever.nucleus.managed.reflection.IReflectedType;
import com.jcwhatever.nucleus.managed.reflection.IReflection;
import com.jcwhatever.nucleus.managed.reflection.Reflection;
import com.jcwhatever.nucleus.utils.nms.INmsHandler;
import org.bukkit.entity.Player;

/**
 * Reflected types for NMS version v1_8_R2
 */
class v1_8_R3 implements INmsHandler {

    static IReflection reflection = Reflection.newContext();

    static IReflectedType _IChatBaseComponent = reflection.nmsType("IChatBaseComponent");

    static IReflectedType _EnumTitleAction = reflection.nmsType("PacketPlayOutTitle$EnumTitleAction");

    static IReflectedType _EntityPlayer = reflection.nmsType("EntityPlayer");

    static IReflectedType _Packet = reflection.nmsType("Packet");

    static IReflectedType _PacketPlayOutTitle = reflection.nmsType("PacketPlayOutTitle")
            .constructorAlias("new", _EnumTitleAction.getHandle(), _IChatBaseComponent.getHandle())
            .constructorAlias("newTimes", int.class, int.class, int.class);

    static IReflectedType _PacketPlayOutChat = reflection.nmsType("PacketPlayOutChat")
            .constructorAlias("new", _IChatBaseComponent.getHandle(), byte.class);

    static IReflectedType _PacketPlayOutPlayerListHeaderFooter = reflection.nmsType("PacketPlayOutPlayerListHeaderFooter")
            .constructorAlias("new")
            .constructorAlias("newHeader", _IChatBaseComponent.getHandle())
            .fieldAlias("footer", "b");

    static IReflectedType _EnumParticle = reflection.nmsType("EnumParticle");

    static IReflectedType _PacketPlayOutWorldParticles = reflection.nmsType("PacketPlayOutWorldParticles")
            .constructorAlias("new", _EnumParticle.getHandle(), boolean.class,
                    float.class, float.class, float.class,
                    float.class, float.class, float.class,
                    float.class, int.class, int[].class);

    static IReflectedType _PacketPlayOutNamedSoundEffect = reflection.nmsType("PacketPlayOutNamedSoundEffect")
            .constructorAlias("new", String.class, double.class, double.class, double.class, float.class, float.class);

    static IReflectedType _ChatSerializer = reflection.nmsType("IChatBaseComponent$ChatSerializer")
            .methodAlias("serialize", "a", String.class);

    static IReflectedType _PlayerConnection = reflection.nmsType("PlayerConnection")
            .method("sendPacket", _Packet.getHandle());

    static IReflectedType _CraftPlayer = reflection.craftType("entity.CraftPlayer")
            .method("getHandle");

    protected boolean _isAvailable = true;

    @Override
    public boolean isAvailable() {
        return _isAvailable;
    }


    /**
     * Send an NMS packet.
     *
     * @param player  The player to send the packet to.
     * @param packet  The packet to send.
     */
    void sendPacket(Player player, Object packet) {

        try {

            IReflectedInstance entityPlayer = getEntityPlayer(player);

            IReflectedInstance connection = _PlayerConnection.reflect(entityPlayer.get("playerConnection"));

            connection.invoke("sendPacket", packet);
        }
        catch (RuntimeException e) {
            e.printStackTrace();
            _isAvailable = false;
        }
    }

    /**
     * Get the players NMS connection.
     *
     * @param player The player.
     */
    IReflectedInstance getConnection(Player player) {

        try {

            IReflectedInstance entityPlayer = getEntityPlayer(player);

            return _PlayerConnection.reflect(entityPlayer.get("playerConnection"));
        }
        catch (RuntimeException e) {
            _isAvailable = false;
            throw e;
        }
    }

    /**
     * Get the net.minecraft.server.EntityPlayer nms object of the player.
     *
     * @param player  The player.
     */
    IReflectedInstance getEntityPlayer(Player player) {
        try {
            return _EntityPlayer.reflect(_CraftPlayer.reflect(player).invoke("getHandle"));
        }
        catch (RuntimeException e) {
            _isAvailable = false;
            throw e;
        }
    }
}
