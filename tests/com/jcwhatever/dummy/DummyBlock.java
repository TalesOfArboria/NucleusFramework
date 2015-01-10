package com.jcwhatever.dummy;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.List;

/*
 * 
 */
public class DummyBlock implements Block {

    private World _world;
    private Material _material;
    private int _x;
    private int _y;
    private int _z;

    public DummyBlock(World world, Material material, int x, int y, int z) {
        _world = world;
        _material = material;
        _x = x;
        _y = y;
        _z = z;
    }

    @Override
    public byte getData() {
        return 0;
    }

    @Override
    public Block getRelative(int x, int y, int z) {
        return new DummyBlock(_world, Material.AIR, _x + x, _y + y, _z + z);
    }

    @Override
    public Block getRelative(BlockFace blockFace) {
        return new DummyBlock(_world, Material.AIR,
                _x + blockFace.getModX(), _y + blockFace.getModY(), _z + blockFace.getModZ());
    }

    @Override
    public Block getRelative(BlockFace blockFace, int i) {
        return new DummyBlock(_world, Material.AIR,
                _x + (blockFace.getModX() * i), _y + (blockFace.getModY() * i), _z + (blockFace.getModZ() * i));
    }

    @Override
    public Material getType() {
        return _material;
    }

    @Override
    public int getTypeId() {
        return _material.getId();
    }

    @Override
    public byte getLightLevel() {
        return 15;
    }

    @Override
    public byte getLightFromSky() {
        return 15;
    }

    @Override
    public byte getLightFromBlocks() {
        return 15;
    }

    @Override
    public World getWorld() {
        return _world;
    }

    @Override
    public int getX() {
        return _x;
    }

    @Override
    public int getY() {
        return _y;
    }

    @Override
    public int getZ() {
        return _z;
    }

    @Override
    public Location getLocation() {
        return new Location(_world, _x, _y, _z);
    }

    @Override
    public Location getLocation(Location location) {
        return new Location(_world, _x, _y, _z);
    }

    @Override
    public Chunk getChunk() {
        return new DummyChunk(_world, (int)Math.floor(_x / 16.0D), (int)Math.floor(_z / 16.0D));
    }

    @Override
    public void setData(byte b) {

    }

    @Override
    public void setData(byte b, boolean b1) {

    }

    @Override
    public void setType(Material material) {
        _material = material;
    }

    @Override
    public boolean setTypeId(int i) {
        Material material = Material.getMaterial(i);
        if (material != null) {
            _material = material;
            return true;
        }
        return false;
    }

    @Override
    public boolean setTypeId(int i, boolean b) {
        Material material = Material.getMaterial(i);
        if (material != null) {
            _material = material;
            return true;
        }
        return false;
    }

    @Override
    public boolean setTypeIdAndData(int i, byte b, boolean b1) {
        Material material = Material.getMaterial(i);
        if (material != null) {
            _material = material;
            return true;
        }
        return false;
    }

    @Override
    public BlockFace getFace(Block block) {
        return BlockFace.NORTH;
    }

    @Override
    public BlockState getState() {
        return null;
    }

    @Override
    public Biome getBiome() {
        return Biome.BEACH;
    }

    @Override
    public void setBiome(Biome biome) {

    }

    @Override
    public boolean isBlockPowered() {
        return false;
    }

    @Override
    public boolean isBlockIndirectlyPowered() {
        return false;
    }

    @Override
    public boolean isBlockFacePowered(BlockFace blockFace) {
        return false;
    }

    @Override
    public boolean isBlockFaceIndirectlyPowered(BlockFace blockFace) {
        return false;
    }

    @Override
    public int getBlockPower(BlockFace blockFace) {
        return 0;
    }

    @Override
    public int getBlockPower() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isLiquid() {
        return false;
    }

    @Override
    public double getTemperature() {
        return 0;
    }

    @Override
    public double getHumidity() {
        return 0;
    }

    @Override
    public PistonMoveReaction getPistonMoveReaction() {
        return PistonMoveReaction.MOVE;
    }

    @Override
    public boolean breakNaturally() {
        return false;
    }

    @Override
    public boolean breakNaturally(ItemStack itemStack) {
        return false;
    }

    @Override
    public Collection<ItemStack> getDrops() {
        return null;
    }

    @Override
    public Collection<ItemStack> getDrops(ItemStack itemStack) {
        return null;
    }

    @Override
    public void setMetadata(String s, MetadataValue metadataValue) {

    }

    @Override
    public List<MetadataValue> getMetadata(String s) {
        return null;
    }

    @Override
    public boolean hasMetadata(String s) {
        return false;
    }

    @Override
    public void removeMetadata(String s, Plugin plugin) {

    }
}
