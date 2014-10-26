/* This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.generic.extended.serializable;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.file.GenericsByteReader;
import com.jcwhatever.bukkit.generic.file.GenericsByteWriter;
import com.jcwhatever.bukkit.generic.file.IGenericsSerializable;
import com.jcwhatever.bukkit.generic.utils.Scheduler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.SkullType;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.NoteBlock;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.io.IOException;

/**
 * A generics serializable wrapper for a BlockState that is
 * specifically designed to store tile entity data.
 *
 * <p>Cannot create a {@code BlockState} instance but instead retains the information
 * necessary to apply the data to the original block location.</p>
 *
 * <p>Supported Tile Entities:</p>
 *
 * <p>{@code InventoryHolder}</p>
 * <p>{@code CommandBlock}</p>
 * <p>{@code CreatureSpawner}</p>
 * <p>{@code NoteBlock}</p>
 * <p>{@code Sign}</p>
 * <p>{@code Skull}</p>
 */
public class SerializableBlockEntity implements IGenericsSerializable {

    private Location _location;
    private Material _material;
    private byte _data;

    // InventoryHolder
    private ItemStack[] _contents;

    // command blocks
    private String _commandName;
    private String _command;

    // CreatureSpawner
    private String _creatureTypeName;
    private int _creatureDelay;

    // NoteBlock
    private Note.Tone _noteTone;
    private int _noteOctave;
    private boolean _noteSharped;

    // Sign
    private String[] _signLines;

    // Skull
    private SkullType _skullType;
    private BlockFace _skullRotation;
    private String _skullOwner; // nullable

    /**
     * Constructor.
     *
     * <p>Required by {@code GenericsByteReader} to deserialize.</p>
     */
    public SerializableBlockEntity() {}

    /**
     * Constructor.
     *
     * @param blockState  The {@code BlockState} that needs to be serialized.
     */
    public SerializableBlockEntity(BlockState blockState) {

        _location = blockState.getLocation();
        _material = blockState.getType();
        _data = blockState.getRawData();

        if (blockState instanceof InventoryHolder) {
            _contents = ((InventoryHolder) blockState).getInventory().getContents();
        }

        if (blockState instanceof CommandBlock) {
            CommandBlock commandBlock = (CommandBlock)blockState;
            _commandName = commandBlock.getName();
            _command = commandBlock.getCommand();
        }

        if (blockState instanceof CreatureSpawner) {
            CreatureSpawner spawner = (CreatureSpawner)blockState;
            _creatureTypeName = spawner.getCreatureTypeName();
            _creatureDelay = spawner.getDelay();
        }

        if (blockState instanceof NoteBlock) {
            NoteBlock noteBlock = (NoteBlock)blockState;

            _noteTone = noteBlock.getNote().getTone();
            _noteOctave = noteBlock.getNote().getOctave();
            _noteSharped = noteBlock.getNote().isSharped();
        }

        if (blockState instanceof Sign) {
            Sign sign = (Sign)blockState;

            _signLines = sign.getLines().clone();
        }

        if (blockState instanceof Skull) {
            Skull skull =  (Skull)blockState;

            _skullType = skull.getSkullType();
            _skullRotation = skull.getRotation();
            _skullOwner = skull.getOwner();
        }
    }

    /**
     * Get the location of the block
     * the serialized {@code BlockState} represents.
     */
    @Nullable
    public Location getLocation() {
        return _location;
    }

    /**
     * Get the {@code Material} of the block.
     */
    @Nullable
    public Material getMaterial() {
        return _material;
    }

    /**
     * Get the raw byte data.
     */
    public byte getRawData() {
        return _data;
    }

    /**
     * Apply the stored {@code BlockState} data to the location
     * the original {@code BlockState} was taken from.
     */
    public void apply() {

        final BlockState blockState = getLocation().getBlock().getState();

        if (blockState.getType() == _material) {

            if (blockState.getRawData() != getRawData())
                _location.getBlock().setData(getRawData());

            applyTile(blockState);
        }
        else {

            _location.getBlock().setType(getMaterial());
            _location.getBlock().setData(getRawData());

            Scheduler.runTaskLater(GenericsLib.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    applyTile(blockState);
                }
            });
        }
    }


    @Override
    public void serializeToBytes(GenericsByteWriter writer) throws IOException {

        writer.write(getLocation());
        writer.write(getMaterial());
        writer.write(getRawData());

        boolean addInventoryHolder = _contents != null;
        boolean addCommandBlock = _commandName != null;
        boolean addCreatureSpawner = _creatureTypeName != null;
        boolean addNoteBlock = _noteTone != null;
        boolean addSign = _signLines != null;
        boolean addSkull = _skullType != null;

        writer.write(addInventoryHolder);
        writer.write(addCommandBlock);
        writer.write(addCreatureSpawner);
        writer.write(addNoteBlock);
        writer.write(addSign);
        writer.write(addSkull);

        // InventoryHolder
        if (addInventoryHolder) {

            writer.write(_contents.length);

            for (ItemStack _content : _contents) {
                writer.write(_content);
            }
        }

        // CommandBlocks
        if (addCommandBlock) {

            writer.write(_commandName);
            writer.write(_command);
        }

        // CreatureSpawner
        if (addCreatureSpawner) {
            writer.write(_creatureTypeName);
            writer.write(_creatureDelay);
        }

        // NoteBlock
        if (addNoteBlock) {
            writer.write(_noteTone);
            writer.write(_noteOctave);
            writer.write(_noteSharped);
        }

        // Sign
        if (addSign) {
            for (int i=0; i < 4; i++) {
                writer.write(_signLines[i]);
            }
        }

        // Skull
        if (addSkull) {

            writer.write(_skullType);
            writer.write(_skullRotation);
            writer.write(_skullOwner);
        }
    }

    @Override
    public void deserializeFromBytes(GenericsByteReader reader) throws IOException {

        _location = reader.getLocation();
        _material = reader.getEnum(Material.class);
        _data = reader.getByte();

        boolean hasInventoryHolder = reader.getBoolean();
        boolean hasCommandBlock = reader.getBoolean();
        boolean hasCreatureSpawner = reader.getBoolean();
        boolean hasNoteBlock = reader.getBoolean();
        boolean addSign = reader.getBoolean();
        boolean addSkull = reader.getBoolean();


        // InventoryHolder
        if (hasInventoryHolder) {
            // iterate content items
            int size = reader.getInteger();
            _contents = new ItemStack[size];

            for (int i=0; i < _contents.length; i++) {
                _contents[i] = reader.getItemStack();
            }
        }

        // CommandBlocks
        if (hasCommandBlock) {

            _commandName = reader.getString();
            _command = reader.getString();
        }

        // CreatureSpawner
        if (hasCreatureSpawner) {

            _creatureTypeName = reader.getString();
            _creatureDelay = reader.getInteger();
        }

        // NoteBlock
        if (hasNoteBlock) {

            _noteTone = reader.getEnum(Note.Tone.class);
            _noteOctave = reader.getInteger();
            _noteSharped = reader.getBoolean();
        }

        // Sign
        if (addSign) {

            _signLines = new String[4];
            for (int i=0; i < 4; i++) {
                _signLines[i] = reader.getString();
            }
        }

        // Skull
        if (addSkull) {

            _skullType = reader.getEnum(SkullType.class);
            _skullRotation = reader.getEnum(BlockFace.class);
            _skullOwner = reader.getString();
        }
    }

    /*
     * Apply stored tile entity data to the supplied BlockState.
     */
    private void applyTile(BlockState blockState) {

        boolean requiresUpdate = false;

        // InventoryHolder
        if (blockState instanceof InventoryHolder && _contents != null) {
            InventoryHolder holder = (InventoryHolder)blockState;

            Inventory inventory = holder.getInventory();

            inventory.setContents(_contents);

            requiresUpdate = true;
        }

        // CommandBlock
        if (blockState instanceof CommandBlock) {
            CommandBlock commandBlock = (CommandBlock)blockState;

            if (_commandName != null)
                commandBlock.setName(_commandName);

            if (_command != null)
                commandBlock.setCommand(_command);

            requiresUpdate = true;
        }

        // CreatureSpawner
        if (blockState instanceof CreatureSpawner) {
            CreatureSpawner spawner = (CreatureSpawner)blockState;

            if (_creatureTypeName != null) {
                spawner.setCreatureTypeByName(_creatureTypeName);
                spawner.setDelay(_creatureDelay);
            }

            requiresUpdate = true;
        }

        if (blockState instanceof NoteBlock && _noteTone != null) {
            NoteBlock noteBlock = (NoteBlock)blockState;

            Note note = new Note(_noteOctave, _noteTone, _noteSharped);
            noteBlock.setNote(note);

            requiresUpdate = true;
        }

        if (blockState instanceof Sign && _signLines != null) {
            Sign sign = (Sign)blockState;

            for (int i=0; i < 4; i++)
                sign.setLine(i, _signLines[i]);

            requiresUpdate = true;
        }

        if (blockState instanceof Skull && _skullType != null) {
            Skull skull =  (Skull)blockState;

            skull.setSkullType(_skullType);
            skull.setRotation(_skullRotation);
            skull.setOwner(_skullOwner);

            requiresUpdate = true;
        }

        if (requiresUpdate) {
            blockState.update(true);
        }
    }
}
