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

import com.jcwhatever.nucleus.internal.NucMsg;
import com.jcwhatever.nucleus.managed.reflection.IReflectedInstance;
import com.jcwhatever.nucleus.managed.reflection.IReflectedType;
import com.jcwhatever.nucleus.managed.reflection.IReflection;
import com.jcwhatever.nucleus.managed.reflection.Reflection;
import com.jcwhatever.nucleus.utils.nms.INmsParticleEffectHandler;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.Container;
import net.minecraft.server.v1_8_R3.ContainerAnvil;
import net.minecraft.server.v1_8_R3.EntityLightning;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInCloseWindow;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedSoundEffect;
import net.minecraft.server.v1_8_R3.PacketPlayOutOpenWindow;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityWeather;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftContainer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;

/**
 * Minecraft version v1_8_R3
 */
class v1_8_R3_Nms implements INms {

    private IReflection _reflection = Reflection.newContext();

    private IReflectedType _IChatBaseComponent = _reflection.nmsType("IChatBaseComponent");

    private IReflectedType _EntityPlayer = _reflection.nmsType("EntityPlayer");

    private IReflectedType _Packet = _reflection.nmsType("Packet");

    private IReflectedType _PacketPlayOutPlayerListHeaderFooter = _reflection.nmsType("PacketPlayOutPlayerListHeaderFooter")
            .constructorAlias("new")
            .constructorAlias("newHeader", _IChatBaseComponent.getHandle())
            .fieldAlias("footer", "b");

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

        if (!(player instanceof CraftPlayer)) {
            NucMsg.debug("v1_8_R3_Nms: Failed to send packet because player is not an instance of CraftPlayer.");
            return;
        }

        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        entityPlayer.playerConnection.sendPacket((Packet)packet);
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
        // times packet
        return new PacketPlayOutTitle(fadeIn, stay, fadeOut);
    }

    @Override
    public Object getTitlePacketSub(String subTitle) {
        // sub title packet
        return new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE,
                IChatBaseComponent.ChatSerializer.a(subTitle));
    }

    @Override
    public Object getTitlePacket(String title) {
        // title packet
        return new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE,
                IChatBaseComponent.ChatSerializer.a(title));
    }

    @Override
    public Object getNamedSoundPacket(String soundName,
                                      double x, double y, double z, float volume, float pitch) {
        return new PacketPlayOutNamedSoundEffect(soundName, x, y, z, volume, pitch);
    }

    @Override
    public Object getParticlePacket(
            INmsParticleEffectHandler.INmsParticleType particleType, boolean force,
            double x, double y, double z,
            double offsetX, double offsetY, double offsetZ,
            float data, int count) {

        EnumParticle enumParticle = EnumParticle.valueOf(particleType.getName());

        return new PacketPlayOutWorldParticles(enumParticle, force,
                (float)x, (float)y, (float)z,
                (float)offsetX, (float)offsetY, (float)offsetZ,
                data, count, particleType.getPacketInts());
    }

    @Override
    public Object getHeaderFooterPacket(@Nullable String headerText, @Nullable String footerText) {

        // create packet instance based on the presence of a header
        PacketPlayOutPlayerListHeaderFooter packet = headerText != null
                ? new PacketPlayOutPlayerListHeaderFooter(IChatBaseComponent.ChatSerializer.a(headerText)) // header constructor
                : new PacketPlayOutPlayerListHeaderFooter(); // no header constructor

        if (footerText != null) {

            IChatBaseComponent footerComponent = IChatBaseComponent.ChatSerializer.a(footerText);

            // insert footer into packet footer field
            _PacketPlayOutPlayerListHeaderFooter.reflect(packet).set("footer", footerComponent);
        }

        return packet;
    }

    @Override
    public Object getActionBarPacket(String text) {
        IChatBaseComponent baseComponent = IChatBaseComponent.ChatSerializer.a(text);
        return new PacketPlayOutChat(baseComponent, (byte)2);
    }

    @Nullable
    @Override
    public InventoryView openAnvilInventory(Player bukkitPlayer, @Nullable Block block) {

        if (!(bukkitPlayer instanceof CraftPlayer))
            return null;

        EntityPlayer player = ((CraftPlayer)bukkitPlayer).getHandle();

        BlockPosition position = block == null
                ? new BlockPosition(0, 0, 0)
                : new BlockPosition(block.getX(), block.getY(), block.getZ());

        if (player.playerConnection == null)
            return null;

        if (player.activeContainer != player.defaultContainer) {
            // fire INVENTORY_CLOSE if one already open
            player.playerConnection.a(new PacketPlayInCloseWindow(player.activeContainer.windowId));
        }

        String windowType = CraftContainer.getNotchInventoryType(InventoryType.ANVIL);

        Container container = new ContainerAnvil(player.inventory, player.world, position, player);
        container = CraftEventFactory.callInventoryOpenEvent(player, container);
        if(container == null)
            return null;

        container.windowId = player.nextContainerCounter();

        String title = container.getBukkitView().getTitle();
        int size = 0;

        player.playerConnection.sendPacket(new PacketPlayOutOpenWindow(
                container.windowId, windowType, new ChatComponentText(title), size));

        player.activeContainer = container;
        player.activeContainer.addSlotListener(player);
        container.checkReachable = false;

        return container.getBukkitView();
    }

    @Override
    public Object getLightningPacket(Location strikeLocation) {

        World nmsWorld = ((CraftWorld)strikeLocation.getWorld()).getHandle();

        EntityLightning lightning = new EntityLightning(nmsWorld,
                strikeLocation.getX(), strikeLocation.getY(), strikeLocation.getZ(), true);

        return new PacketPlayOutSpawnEntityWeather(lightning);
    }

    @Override
    public boolean isEntityVisible(Entity entity) {
        return !((CraftEntity)entity).getHandle().isInvisible();
    }

    @Override
    public void setEntityVisible(Entity entity, boolean isVisible) {
        ((CraftEntity)entity).getHandle().setInvisible(!isVisible);
    }

    @Override
    public void getVelocity(Entity entity, Vector output) {
        net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity)entity).getHandle();
        output.setX(nmsEntity.motX);
        output.setY(nmsEntity.motY);
        output.setZ(nmsEntity.motZ);
    }
}
