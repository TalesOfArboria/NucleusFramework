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
import com.jcwhatever.nucleus.managed.reflection.IReflectedType;
import com.jcwhatever.nucleus.managed.reflection.IReflection;
import com.jcwhatever.nucleus.managed.reflection.Reflection;
import com.jcwhatever.nucleus.utils.nms.INmsParticleEffectHandler;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

/**
 * Minecraft version v1_8_R1
 */
class v1_8_R2 implements INms {

    private IReflection _reflection = Reflection.newContext();

    private IReflectedType _IChatBaseComponent = _reflection.nmsType("IChatBaseComponent");

    private IReflectedType _EnumTitleAction = _reflection.nmsType("PacketPlayOutTitle$EnumTitleAction");

    private IReflectedType _EntityPlayer = _reflection.nmsType("EntityPlayer");

    private IReflectedType _Packet = _reflection.nmsType("Packet");

    private IReflectedType _PacketPlayOutTitle = _reflection.nmsType("PacketPlayOutTitle")
            .constructorAlias("new", _EnumTitleAction.getHandle(), _IChatBaseComponent.getHandle())
            .constructorAlias("newTimes", int.class, int.class, int.class);

    private IReflectedType _PacketPlayOutChat = _reflection.nmsType("PacketPlayOutChat")
            .constructorAlias("new", _IChatBaseComponent.getHandle(), byte.class);

    private IReflectedType _PacketPlayOutPlayerListHeaderFooter = _reflection.nmsType("PacketPlayOutPlayerListHeaderFooter")
            .constructorAlias("new")
            .constructorAlias("newHeader", _IChatBaseComponent.getHandle())
            .fieldAlias("footer", "b");

    private IReflectedType _EnumParticle = _reflection.nmsType("EnumParticle");

    private IReflectedType _PacketPlayOutWorldParticles = _reflection.nmsType("PacketPlayOutWorldParticles")
            .constructorAlias("new", _EnumParticle.getHandle(), boolean.class,
                    float.class, float.class, float.class,
                    float.class, float.class, float.class,
                    float.class, int.class, int[].class);

    private IReflectedType _PacketPlayOutNamedSoundEffect = _reflection.nmsType("PacketPlayOutNamedSoundEffect")
            .constructorAlias("new", String.class, double.class, double.class, double.class, float.class, float.class);

    private IReflectedType _ChatSerializer = _reflection.nmsType("IChatBaseComponent$ChatSerializer")
            .methodAlias("serialize", "a", String.class);

    private IReflectedType _PlayerConnection = _reflection.nmsType("PlayerConnection")
            .method("sendPacket", _Packet.getHandle());

    private IReflectedType _CraftPlayer = _reflection.craftType("entity.CraftPlayer")
            .method("getHandle");

    private boolean _isAvailable = true;

    @Override
    public boolean isAvailable() {
        return _isAvailable;
    }

    @Override
    public void setAvailable(boolean isAvailable) {
        _isAvailable = isAvailable;
    }

    @Override
    public IReflection getReflection() {
        return _reflection;
    }

    /**
     * Send an NMS packet.
     *
     * @param player  The player to send the packet to.
     * @param packet  The packet to send.
     */
    @Override
    public void sendPacket(Player player, Object packet) {

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

    @Override
    public void sendPacket(IReflectedInstance connection, Object packet) {
        try {
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
    @Override
    public IReflectedInstance getConnection(Player player) {

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
    @Override
    public IReflectedInstance getEntityPlayer(Player player) {
        try {
            return _EntityPlayer.reflect(_CraftPlayer.reflect(player).invoke("getHandle"));
        }
        catch (RuntimeException e) {
            _isAvailable = false;
            throw e;
        }
    }

    @Override
    public Object getTitlePacketTimes(int fadeIn, int stay, int fadeOut) {
        return _PacketPlayOutTitle.construct("newTimes", fadeIn, stay, fadeOut);
    }

    @Override
    public Object getTitlePacketSub(String subTitle) {
        Object subTitleComponent = _ChatSerializer.invokeStatic("serialize", subTitle);
        return _PacketPlayOutTitle.construct(
                "new", _EnumTitleAction.getEnum("SUBTITLE"), subTitleComponent);
    }

    @Override
    public Object getTitlePacket(String title) {
        Object titleComponent = _ChatSerializer.invokeStatic("serialize", title);
        return _PacketPlayOutTitle.construct(
                "new", _EnumTitleAction.getEnum("TITLE"), titleComponent);
    }

    @Override
    public Object getNamedSoundPacket(String soundName, double x, double y, double z, float volume, float pitch) {
        return _PacketPlayOutNamedSoundEffect.construct("new", soundName, x, y, z, volume, pitch);
    }

    @Override
    public Object getParticlePacket(INmsParticleEffectHandler.INmsParticleType particleType,
                                    boolean force, double x, double y, double z,
                                    double offsetX, double offsetY, double offsetZ, float data, int count) {
        Object enumParticle = _EnumParticle.getEnum(particleType.getName());

        return _PacketPlayOutWorldParticles.construct("new", enumParticle, force,
                (float)x, (float)y, (float)z,
                (float)offsetX, (float)offsetY, (float)offsetZ,
                data, count, particleType.getPacketInts());
    }

    @Override
    public Object getHeaderFooterPacket(@Nullable String headerText, @Nullable String footerText) {
        // create packet instance based on the presence of a header
        Object packet = headerText != null
                ? _PacketPlayOutPlayerListHeaderFooter.construct("newHeader",
                _ChatSerializer.invokeStatic("serialize", headerText)) // header constructor
                : _PacketPlayOutPlayerListHeaderFooter.construct("new"); // no header constructor

        if (footerText != null) {

            Object footerComponent = _ChatSerializer.invokeStatic("serialize", footerText);

            // insert footer into packet footer field
            _PacketPlayOutPlayerListHeaderFooter.reflect(packet).set("footer", footerComponent);
        }

        return packet;
    }

    @Override
    public Object getActionBarPacket(String text) {
        Object titleComponent = _ChatSerializer.invokeStatic("serialize", text);

        return _PacketPlayOutChat.construct("new", titleComponent, (byte) 2);
    }
}
