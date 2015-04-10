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


package com.jcwhatever.nucleus.utils.file;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.managed.items.meta.IItemMetaHandler;
import com.jcwhatever.nucleus.managed.items.meta.ItemMetaValue;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.coords.SyncLocation;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Write bytes to a stream. In order to read the stream
 * properly, {@link BasicByteReader} needs to be used.
 */
public class BasicByteWriter extends OutputStream implements IByteWriter {

    private static final byte[] BOOLEAN_FLAGS = new byte[] { 1, 2, 4, 8, 16, 32, 64 };

    private final OutputStream _stream;
    private final byte[] _buffer = new byte[64];
    private long _bytesWritten = 0;

    private int _booleanCount = 0;
    private byte[] _booleanBuffer = new byte[1];

    /**
     * Constructor.
     *
     * @param outputStream  The output stream.
     */
    public BasicByteWriter(OutputStream outputStream) {
        _stream = outputStream;
    }

    /**
     * Get the number of bytes written.
     */
    @Override
    public long getBytesWritten() {
        return _bytesWritten;
    }

    /**
     * Write a boolean value.
     *
     * <p>Booleans written sequentially are written as bits into the current byte. When the byte
     * runs out of bits, the next bit is written to the next byte. Because of this, 7 boolean
     * values written sequentially use only 1 byte of space in the stream instead of 7 bytes.</p>
     *
     * <p>Bytes that store the boolean values do not share their bits with other data types.</p>
     *
     * @param booleanValue  The boolean value.
     *
     * @throws IOException
     */
    @Override
    public void write(boolean booleanValue) throws IOException {

        if (_booleanCount == 7) {
            writeBooleans();
        }

        if (booleanValue) {
            _booleanBuffer[0] |= BOOLEAN_FLAGS[_booleanCount];
        }

        _booleanCount++;
    }

    /**
     * Write a byte.
     *
     * @param byteValue  The byte.
     *
     * @throws IOException
     */
    @Override
    public void write(byte byteValue) throws IOException {

        // write buffered booleans
        writeBooleans();

        _stream.write(byteValue);
        _bytesWritten++;
    }

    /**
     * Write a byte array.
     *
     * <p>Writes an array by first writing an integer (4 bytes) which
     * indicates the length of the array, then writes the entire byte array.</p>
     *
     * <p>If the array is null or empty then the integer 0 (4 bytes) is
     * written and no array bytes are added.</p>
     *
     * @param byteArray  The byte array.
     *
     * @throws IOException
     */
    @Override
    public void write(@Nullable byte[] byteArray) throws IOException {

        // write buffered booleans
        writeBooleans();

        if (byteArray == null || byteArray.length == 0) {
            write(0);
            return;
        }

        write(byteArray.length);
        _stream.write(byteArray, 0, byteArray.length);
        _bytesWritten += byteArray.length;
    }

    /**
     * Write a 16-bit number (2 bytes).
     *
     * @param shortValue  The short.
     *
     * @throws IOException
     */
    @Override
    public void write(short shortValue) throws IOException {

        // write buffered booleans
        writeBooleans();

        _buffer[0] = (byte)(shortValue >> 8 & 0xFF);
        _buffer[1] = (byte)(shortValue & 0xFF);

        _stream.write(_buffer, 0, 2);
        _bytesWritten += 2;
    }

    /**
     * Write a 32-bit number (4 bytes).
     *
     * @param integerValue  The integer.
     *
     * @throws IOException
     */
    @Override
    public void write(int integerValue) throws IOException {

        // write buffered booleans
        writeBooleans();

        _buffer[0] = (byte)(integerValue >> 24 & 0xFF);
        _buffer[1] = (byte)(integerValue >> 16 & 0xFF);
        _buffer[2] = (byte)(integerValue >> 8 & 0xFF);
        _buffer[3] = (byte)(integerValue & 0xFF);
        _stream.write(_buffer, 0, 4);
        _bytesWritten+=4;
    }

    /**
     * Write a 64 bit number (8 bytes).
     *
     * @param longValue  The long.
     *
     * @throws IOException
     */
    @Override
    public void write(long longValue) throws IOException {

        // write buffered booleans
        writeBooleans();

        _buffer[0] = (byte)(longValue >> 56 & 0xFF);
        _buffer[1] = (byte)(longValue >> 48 & 0xFF);
        _buffer[2] = (byte)(longValue >> 40 & 0xFF);
        _buffer[3] = (byte)(longValue >> 32 & 0xFF);
        _buffer[4] = (byte)(longValue >> 24 & 0xFF);
        _buffer[5] = (byte)(longValue >> 16 & 0xFF);
        _buffer[6] = (byte)(longValue >> 8 & 0xFF);
        _buffer[7] = (byte)(longValue & 0xFF);
        _stream.write(_buffer, 0, 8);
        _bytesWritten+=8;
    }

    /**
     * Write a floating point number.
     *
     * <p>Float values are written as a UTF-8 byte array preceded with 1 byte
     * to indicate the number of bytes in the array.</p>
     *
     * @param floatValue  The floating point value.
     *
     * @throws IOException
     */
    @Override
    public void write(float floatValue) throws IOException {
        writeSmallString(String.valueOf(floatValue));
    }

    /**
     * Write a double number.
     *
     * <p>Double values are written using a UTF-8 byte array preceded with 1 byte
     * to indicate the number of bytes in the array.</p>
     *
     * @param doubleValue  The floating point double.
     *
     * @throws IOException
     */
    @Override
    public void write(double doubleValue) throws IOException {
        writeSmallString(String.valueOf(doubleValue));
    }

    /**
     * Write a {@link BigDecimal} number.
     *
     * <p>Serializes number using {@link ObjectOutputStream}.
     * (See {@link #write(Serializable)}).</p>
     *
     * @param decimal  The big decimal.
     *
     * @throws IOException
     */
    @Override
    public void write(@Nullable BigDecimal decimal) throws IOException {
        write((Serializable) decimal);
    }

    /**
     * Write a {@link BigInteger} number.
     *
     * <p>Serializes number using {@link ObjectOutputStream}.
     * (See {@link #write(Serializable)}).</p>
     *
     * @param integer  The big integer.
     *
     * @throws IOException
     */
    @Override
    public void write(@Nullable BigInteger integer) throws IOException {
        write((Serializable) integer);
    }

    /**
     * Write a text string using UTF-16 encoding.
     *
     * <p>The first 2 bytes (short) written indicate the length
     * of the string in bytes.</p>
     *
     * <p>If the string is null then the short -1 (2 bytes) is written
     * and no array bytes are written.</p>
     *
     * <p>If the string is empty then the short 0 (2 bytes) is written
     * and no array bytes are written.</p>
     *
     * @param text  The text to write. Can be null.
     *
     * @throws IOException
     */
    @Override
    public void write(@Nullable String text) throws IOException {
        write(text, StandardCharsets.UTF_16);
    }

    /**
     * Write a text string.
     *
     * <p>The first 2 bytes (short) written indicate the length
     * of the string in bytes.</p>
     *
     * <p>If the string is null then the short -1 (2 bytes) is written
     * and no array bytes are written.</p>
     *
     * <p>If the string is empty then the short 0 (2 bytes) is written
     * and no array bytes are written.</p>
     *
     * @param text     The text to write. Can be null.
     * @param charset  The charset encoding to use.
     *
     * @throws IOException
     */
    @Override
    public void write(@Nullable String text, Charset charset) throws IOException {

        // write buffered booleans
        writeBooleans();

        // handle null text
        if (text == null) {
            write(-1);
            return;
        }
        // handle empty text
        else if (text.length() == 0) {
            write((short)0);
            return;
        }

        byte[] bytes = text.getBytes(charset);

        // write string byte length
        write((short)bytes.length);

        // write string bytes
        _stream.write(bytes);

        // record bytes written
        _bytesWritten+=bytes.length;
    }

    /**
     * Write a text string that is expected to be no more than 255 bytes in length
     * using UTF-8 encoding.
     *
     * <p>The first byte written indicate the length of the string in bytes.</p>
     *
     * <p>If the string is null then the byte -1 is written
     * and no array bytes are written.</p>
     *
     * <p>If the string is empty then the byte 0 is written
     * and no array bytes are written.</p>
     *
     * @param text     The text to write. Can be null.
     *
     * @throws IOException
     */
    @Override
    public void writeSmallString(String text) throws IOException {

        // write buffered booleans
        writeBooleans();

        // handle null text
        if (text == null) {
            write((byte)-1);
            return;
        }
        // handle empty text
        else if (text.length() == 0) {
            write((byte)0);
            return;
        }

        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);

        // write string byte length
        write((byte)bytes.length);

        // write string bytes
        _stream.write(bytes);

        // record bytes written
        _bytesWritten+=bytes.length;
    }

    /**
     * Write an enum.
     *
     * <p>Enum values are written using a UTF-8 byte array preceded with 1 byte
     * to indicate the number of bytes in the array. The UTF-8 byte array
     * is the string value of the enum constants name.</p>
     *
     * @param enumConstant  The enum constant.
     *
     * @param <T>  The enum type.
     *
     * @throws IOException
     */
    @Override
    public <T extends Enum<T>> void write(T enumConstant) throws IOException {
        PreCon.notNull(enumConstant);

        writeSmallString(enumConstant.name());
    }

    /**
     * Write a UUID.
     *
     * <p>The UUID is written as 16 bytes, the first 8 bytes are the most
     * significant bits while the last 8 bytes are the least significant bits.</p>
     *
     * @param uuid  The UUID to write.
     *
     * @throws IOException
     */
    @Override
    public void write(UUID uuid) throws IOException {
        PreCon.notNull(uuid);

        write(uuid.getMostSignificantBits());
        write(uuid.getLeastSignificantBits());
    }

    /**
     * Write a {@link Location}.
     *
     * <p>The location is written as follows:</p>
     *
     * <ul>
     *     <li>The world name - UTF-8 String preceded with a single byte to indicate length.</li>
     *     <li>The X value - Double (See {@link #write(double)})</li>
     *     <li>The Y value - Double (See {@link #write(double)})</li>
     *     <li>The Z value - Double (See {@link #write(double)})</li>
     *     <li>The Yaw value - Double (See {@link #write(double)})</li>
     *     <li>The Pitch value - Double (See {@link #write(double)})</li>
     * </ul>
     *
     * @param location  The location.
     *
     * @throws IOException
     */
    @Override
    public void write(Location location) throws IOException {
        PreCon.notNull(location);

        String worldName = null;

        if (location instanceof SyncLocation) {
            worldName = ((SyncLocation) location).getWorldName();
        }
        else if (location.getWorld() != null) {
            worldName = location.getWorld().getName();
        }

        writeSmallString(worldName);
        write(location.getX());
        write(location.getY());
        write(location.getZ());
        write(location.getYaw());
        write(location.getPitch());
    }

    /**
     * Write an {@link EulerAngle}.
     *
     * The angle is written as three doubles representing
     * x, y and z. (See {@link #write(double)}.
     *
     * @param angle  The angle.
     *
     * @throws IOException
     */
    @Override
    public void write(EulerAngle angle) throws IOException {
        PreCon.notNull(angle);

        write(angle.getX());
        write(angle.getY());
        write(angle.getZ());
    }

    /**
     * Write a {@link Vector}.
     *
     * <p>The vector is written as three doubles representing
     * x, y and z. (See {@link #write(double)}</p>
     *
     * @param vector  The vector.
     *
     * @throws IOException
     */
    @Override
    public void write(Vector vector) throws IOException {
        PreCon.notNull(vector);

        write(vector.getX());
        write(vector.getY());
        write(vector.getZ());
    }

    /**
     * Write an {@link ItemStack}.
     *
     * <p>Writes the item stack as follows:</p>
     * <ul>
     *     <li>Boolean (bit or byte depending on the data structure) indicating
     *         if the item stack is null. 0 = null. (See {@link #write(boolean)})</li>
     *     <li>Material - Enum (See {@link #write(Enum)})</li>
     *     <li>Durability - Short (See {@link #write(int)})</li>
     *     <li>Meta count - Integer (See {@link #write(int)})</li>
     *     <li>Meta collection</li>
     * </ul>
     *
     * <p>Meta is written first with an integer to indicate the number of
     * meta elements followed by the elements, each with the following format:</p>
     * <ul>
     *     <li>Meta Name - UTF-8 String preceded with a byte to indicate length.
     *     (See {@link #writeSmallString(String)}</li>
     *     <li>Meta Data - UTF-16 String (See {@link #write(String)})</li>
     * </ul>
     *
     * @param itemStack  The item stack.
     *
     * @throws IOException
     */
    @Override
    public void write(@Nullable ItemStack itemStack) throws IOException {

        write(itemStack != null);
        if (itemStack == null)
            return;

        // write basic data
        write(itemStack.getType());
        write((int)itemStack.getDurability());
        write(itemStack.getAmount());

        Collection<IItemMetaHandler> handlers = Nucleus.getItemMetaHandlers().getHandlers();

        List<ItemMetaValue> metaObjects = new ArrayList<>(10);

        for (IItemMetaHandler handler : handlers) {
            metaObjects.addAll(handler.getMeta(itemStack));
        }

        write(metaObjects.size());

        for (ItemMetaValue metaObject : metaObjects) {
            writeSmallString(metaObject.getName());
            write(metaObject.getRawData(), StandardCharsets.UTF_16);
        }
    }

    /**
     * Serialize an {@link IBinarySerializable} object.
     *
     * <p>A boolean is written to indicate if the object is null (0 = null) and if not
     * null the object serializes itself into the stream.</p>
     *
     * @param object  The object to serialize.
     *
     * @param <T>  The object type.
     *
     * @throws IOException
     */
    @Override
    public <T extends IBinarySerializable> void write(@Nullable T object) throws IOException {
        write(object != null);
        if (object == null)
            return;

        object.serialize(this);
    }

    /**
     * Serialize an object.
     *
     * <p>A boolean is written indicating if the object is null (0 = null) and if not
     * null the object is serialized using an {@link ObjectOutputStream}.</p>
     *
     * @param object  The object to serialize. Can be null.
     *
     * @throws IOException
     */
    @Override
    public <T extends Serializable> void write(@Nullable T object) throws IOException {

        // write null flag
        write(object != null);
        if (object == null)
            return;

        // clear boolean buffer
        writeBooleans();

        ObjectOutputStream objectStream = new ObjectOutputStream(this);
        objectStream.writeObject(object);
    }

    @Override
    public void close() throws IOException {

        flush();

        _stream.close();
    }

    @Override
    public void flush() throws IOException {

        // write buffered booleans
        writeBooleans();

        _stream.flush();
    }

    private void writeBooleans() throws IOException {
        if (_booleanCount > 0) {
            _stream.write(_booleanBuffer, 0, 1);
            _booleanBuffer[0] = 0;
            _bytesWritten++;
            _booleanCount = 0;
        }
    }
}
