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
import com.jcwhatever.nucleus.utils.coords.LocationUtils;
import com.jcwhatever.nucleus.utils.nms.INmsParticleEffectHandler;
import com.jcwhatever.nucleus.utils.text.components.IChatMessage;
import net.minecraft.server.v1_9_R1.BlockPosition;
import net.minecraft.server.v1_9_R1.ChatComponentText;
import net.minecraft.server.v1_9_R1.Container;
import net.minecraft.server.v1_9_R1.ContainerAnvil;
import net.minecraft.server.v1_9_R1.DataWatcher;
import net.minecraft.server.v1_9_R1.EntityHuman;
import net.minecraft.server.v1_9_R1.EntityLightning;
import net.minecraft.server.v1_9_R1.EntityLiving;
import net.minecraft.server.v1_9_R1.EntityPlayer;
import net.minecraft.server.v1_9_R1.EnumParticle;
import net.minecraft.server.v1_9_R1.IChatBaseComponent;
import net.minecraft.server.v1_9_R1.MinecraftKey;
import net.minecraft.server.v1_9_R1.Packet;
import net.minecraft.server.v1_9_R1.PacketPlayInCloseWindow;
import net.minecraft.server.v1_9_R1.PacketPlayOutChat;
import net.minecraft.server.v1_9_R1.PacketPlayOutNamedSoundEffect;
import net.minecraft.server.v1_9_R1.PacketPlayOutOpenWindow;
import net.minecraft.server.v1_9_R1.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_9_R1.PacketPlayOutSpawnEntityWeather;
import net.minecraft.server.v1_9_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_9_R1.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_9_R1.SoundCategory;
import net.minecraft.server.v1_9_R1.SoundEffect;
import net.minecraft.server.v1_9_R1.World;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_9_R1.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftContainer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Minecraft version v1_9_R1
 */
public class v1_9_R1_Nms  implements INms {

    private IReflection _reflection = Reflection.newContext();

    private IReflectedType _IChatBaseComponent = _reflection.nmsType("IChatBaseComponent");

    private IReflectedType _Entity = _reflection.nmsType("Entity")
            .fieldAlias("datawatcher", "datawatcher");

    private IReflectedType _EntityPlayer = _reflection.nmsType("EntityPlayer");

    private IReflectedType _EntityLiving = _reflection.nmsType("EntityLiving")
            /* protected field "bc" (boolean) usage found in EntityPlayer method
             * a(float f, float f1, *boolean jump*, boolean flag1), invoked from
             * PlayerConnection#a(PacketPlayInSteerVehicle packet) */
            .fieldAlias("vehicleJump", "bc");


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
    private v1_9_R1_Chat _chat = new v1_9_R1_Chat();

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
            NucMsg.debug("v1_9_R1_Nms: Failed to send packet because player is not an instance of CraftPlayer.");
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
    public Object getTitlePacketSub(CharSequence subTitle) {
        // sub title packet
        return new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE,
                IChatBaseComponent.ChatSerializer.a(subTitle.toString()));
    }

    @Override
    public Object getTitlePacket(CharSequence title) {
        // title packet
        return new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE,
                IChatBaseComponent.ChatSerializer.a(title.toString()));
    }

    @Override
    public Object getNamedSoundPacket(String soundName,
                                      double x, double y, double z, float volume, float pitch) {

        MinecraftKey key = new MinecraftKey(soundName);
        SoundEffect effect = SoundEffect.a.get(key);
        if (effect == null) {
            effect = new SoundEffect(key);
        }

        return new PacketPlayOutNamedSoundEffect(effect, SoundCategory.MASTER, x, y, z, volume, pitch);
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
    public Object getHeaderFooterPacket(@Nullable CharSequence headerText, @Nullable CharSequence footerText) {

        // create packet instance based on the presence of a header
        PacketPlayOutPlayerListHeaderFooter packet = headerText != null
                ? new PacketPlayOutPlayerListHeaderFooter(IChatBaseComponent.ChatSerializer.a(headerText.toString())) // header constructor
                : new PacketPlayOutPlayerListHeaderFooter(); // no header constructor

        if (footerText != null) {

            IChatBaseComponent footerComponent = IChatBaseComponent.ChatSerializer.a(footerText.toString());

            // insert footer into packet footer field
            _PacketPlayOutPlayerListHeaderFooter.reflect(packet).set("footer", footerComponent);
        }

        return packet;
    }

    @Override
    public Object getActionBarPacket(CharSequence text) {
        IChatBaseComponent baseComponent = IChatBaseComponent.ChatSerializer.a(text.toString());
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
        net.minecraft.server.v1_9_R1.Entity nmsEntity = ((CraftEntity)entity).getHandle();
        output.setX(nmsEntity.motX);
        output.setY(nmsEntity.motY);
        output.setZ(nmsEntity.motZ);
    }

    @Override
    public void setYaw(Entity entity, float yaw) {
        net.minecraft.server.v1_9_R1.Entity nmsEntity = ((CraftEntity)entity).getHandle();
        yaw = LocationUtils.clampYaw(yaw);
        nmsEntity.yaw = yaw;

        if (nmsEntity instanceof EntityLiving) {
            EntityLiving nmsLiving = (EntityLiving)nmsEntity;
            nmsLiving.aO = yaw;
            nmsLiving.aP = yaw;
            if (!(nmsEntity instanceof EntityHuman)) {
                nmsLiving.aM = yaw;
            }
        }
    }

    @Override
    public void setPitch(Entity entity, float pitch) {
        net.minecraft.server.v1_9_R1.Entity nmsEntity = ((CraftEntity)entity).getHandle();
        pitch = LocationUtils.limitPitch(pitch);
        nmsEntity.pitch = pitch;
    }

    @Override
    public void setStepHeight(Entity entity, float height) {
        net.minecraft.server.v1_9_R1.Entity nmsEntity = ((CraftEntity)entity).getHandle();
        /* public field P found in Entity. Default value for EntityLiving is 0.6f as set
        * in EntityLiving Constructor. */
        nmsEntity.P = height;
    }

    @Override
    public IChatMessage getMessage(final String text) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void send(Player player, IChatMessage message) {
        if (message.totalComponents() == 0)
            return;

        EntityPlayer nmsPlayer = ((CraftPlayer)player).getHandle();
        if (nmsPlayer.playerConnection == null)
            return;

        IChatBaseComponent nmsComponent = _chat.getComponent(message);
        nmsPlayer.playerConnection.sendPacket(new PacketPlayOutChat(nmsComponent));
    }

    @Override
    public void send(Collection<? extends Player> players, IChatMessage message) {
        if (message.totalComponents() == 0)
            return;

        IChatBaseComponent nmsComponent = _chat.getComponent(message);
        PacketPlayOutChat packet = new PacketPlayOutChat(nmsComponent);

        for (Player player : players) {
            EntityPlayer nmsPlayer = ((CraftPlayer)player).getHandle();
            if (nmsPlayer.playerConnection == null)
                continue;

            nmsPlayer.playerConnection.sendPacket(packet);
        }
    }

    @Override
    public float getVehicleForwardMotion(LivingEntity passenger) {
        /* public field "be" (float) usage found in EntityPlayer method
         * a(*float forward*, float f1, boolean flag, boolean flag1), invoked from
         * PlayerConnection#a(PacketPlayInSteerVehicle packet) */
        return ((CraftLivingEntity)passenger).getHandle().be;

    }

    @Override
    public void setVehicleForwardMotion(LivingEntity passenger, float value) {
        ((CraftLivingEntity)passenger).getHandle().be = value;
    }

    @Override
    public float getVehicleLateralMotion(LivingEntity passenger) {
        /* public field "bd" (float) usage found in EntityPlayer method
         * a(float f, *float lateral*, boolean flag, boolean flag1), invoked from
         * PlayerConnection#a(PacketPlayInSteerVehicle packet) */
        return ((CraftLivingEntity) passenger).getHandle().bd;
    }

    @Override
    public void setVehicleLateralMotion(LivingEntity passenger, float value) {
        ((CraftLivingEntity) passenger).getHandle().bd = value;
    }

    @Override
    public boolean isVehicleJumpPressed(LivingEntity passenger) {
        EntityLiving entity = ((CraftLivingEntity) passenger).getHandle();
        Object jumpObject = _EntityLiving.reflect(entity).get("vehicleJump");
        return jumpObject instanceof Boolean && (Boolean) jumpObject;
    }

    @Override
    public void setVehicleJumpPressed(LivingEntity passenger, boolean isPressed) {
        EntityLiving entity = ((CraftLivingEntity) passenger).getHandle();
        _EntityLiving.reflect(entity).set("vehicleJump", isPressed);
    }

    @Override
    public boolean isVehicleDismountPressed(LivingEntity passenger) {
        IDataWatcher watcher = getReplacedDataWatcher(passenger);
        return watcher != null
                ? watcher.isDismountPressed()
                : ((CraftLivingEntity)passenger).getHandle().isSneaking();
    }

    @Override
    public void setVehicleDismountPressed(LivingEntity passenger, boolean isPressed) {
        ((CraftLivingEntity) passenger).getHandle().setSneaking(isPressed);
    }

    @Override
    public boolean canDismount(LivingEntity passenger) {
        IDataWatcher watcher = getReplacedDataWatcher(passenger);
        return watcher == null || watcher.canDismount();
    }

    @Override
    public void setCanDismount(LivingEntity passenger, boolean canDismount) {
        IDataWatcher watcher = getReplacedDataWatcher(passenger);
        if (watcher == null) {
            if (canDismount)
                return;

            watcher = replaceDataWatcher(passenger);
        }
        watcher.setCanDismount(canDismount);
    }

    @Override
    public void removeArrows(Entity entity) {
        if (!(entity instanceof Player))
            return;

        IDataWatcher watcher = getReplacedDataWatcher((Player)entity);
        if (watcher == null) {
            watcher = replaceDataWatcher((Player)entity);
        }

        watcher.removeArrows();
    }

    @Override
    public boolean canArrowsStick(Entity entity) {
        if (!(entity instanceof Player))
            return false;

        IDataWatcher watcher = getReplacedDataWatcher((Player) entity);
        return watcher == null || watcher.canArrowsStick();
    }

    @Override
    public void setCanArrowsStick(Entity entity, boolean isAllowed) {
        if (!(entity instanceof Player))
            return;

        IDataWatcher watcher = getReplacedDataWatcher((Player) entity);
        if (watcher == null) {
            watcher = replaceDataWatcher((Player)entity);
        }

        watcher.setCanArrowsStick(isAllowed);
    }

    @Nullable
    private IDataWatcher getReplacedDataWatcher(LivingEntity entity) {
        DataWatcher watcher = ((CraftLivingEntity)entity).getHandle().getDataWatcher();
        if (!(watcher instanceof IDataWatcher))
            return null;

        return (IDataWatcher)watcher;
    }

    private IDataWatcher replaceDataWatcher(LivingEntity entity) {
        EntityLiving nmsEntity = ((CraftLivingEntity) entity).getHandle();
        IDataWatcher watcher = new v1_9_R1_DataWatcher(entity);
        _Entity.reflect(nmsEntity).set("datawatcher", watcher);
        return watcher;
    }
}
