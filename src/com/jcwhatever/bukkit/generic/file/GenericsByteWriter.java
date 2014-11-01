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


package com.jcwhatever.bukkit.generic.file;

import com.jcwhatever.bukkit.generic.items.ItemStackHelper;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Write bytes to a stream. In order to read the stream
 * properly, {@code GenericsByteReader} needs to be used.
 */
public class GenericsByteWriter extends OutputStream {

    private final OutputStream _stream;
    private final byte[] _buffer = new byte[64];
    private long _bytesWritten = 0;

    private int _booleanCount = 0;
    private byte[] _booleanBuffer = new byte[1];
    private final byte[] _booleanFlags = new byte[] { 1, 2, 4, 8, 16, 32, 64 };


    /**
     * Cosntructor.
     *
     * @param outputStream  The output stream.
     */
    public GenericsByteWriter(OutputStream outputStream) {
        _stream = outputStream;
    }

    /**
     * Get the number of bytes written.
     */
    public long getBytesWritten() {
        return _bytesWritten;
    }

    /**
     * Write a boolean value.
     *
     * @param booleanValue  The boolean value
     *
     * @throws IOException
     */
    public void write(boolean booleanValue) throws IOException {

        if (_booleanCount == 7) {
            writeBooleans();
        }

        if (booleanValue) {
            _booleanBuffer[0] |= _booleanFlags[_booleanCount];
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
    public void write(byte byteValue) throws IOException {

        // write buffered booleans
        writeBooleans();

        _stream.write(byteValue);
        _bytesWritten++;
    }

    /**
     * Write a 32-bit number.
     *
     * @param integerValue  The integer.
     *
     * @throws IOException
     */
    @Override
    public void write(int integerValue) throws IOException {

        // write buffered booleans
        writeBooleans();

        _buffer[0] = (byte)(integerValue >> 24 & 255);
        _buffer[1] = (byte)(integerValue >> 16 & 255);
        _buffer[2] = (byte)(integerValue >> 8 & 255);
        _buffer[3] = (byte)(integerValue & 255);
        _stream.write(_buffer, 0, 4);
        _bytesWritten+=4;
    }

    /**
     * Write a 64 bit number.
     *
     * @param longValue  The long.
     *
     * @throws IOException
     */
    public void write(long longValue) throws IOException {

        // write buffered booleans
        writeBooleans();

        _buffer[0] = (byte)(longValue >> 56 & 255);
        _buffer[1] = (byte)(longValue >> 48 & 255);
        _buffer[2] = (byte)(longValue >> 40 & 255);
        _buffer[3] = (byte)(longValue >> 32 & 255);
        _buffer[4] = (byte)(longValue >> 24 & 255);
        _buffer[5] = (byte)(longValue >> 16 & 255);
        _buffer[6] = (byte)(longValue >> 8 & 255);
        _buffer[7] = (byte)(longValue & 255);
        _stream.write(_buffer, 0, 8);
        _bytesWritten+=8;
    }

    /**
     * Write a floating point number.
     *
     * @param floatValue  The floating point value.
     *
     * @throws IOException
     */
    public void write(float floatValue) throws IOException {
        write(String.valueOf(floatValue));
    }

    /**
     * Write a floating point double.
     *
     * @param doubleValue  The floating point double.
     *
     * @throws IOException
     */
    public void write(double doubleValue) throws IOException {
        write(String.valueOf(doubleValue));
    }

    /**
     * Write a text string.
     *
     * @param text  The text to write. Can be null.
     *
     * @throws IOException
     */
    public void write(@Nullable String text) throws IOException {

        // write buffered booleans
        writeBooleans();

        // handle null text
        if (text == null) {
            write(-1);
            return;
        }
        // handle empty text
        else if (text.length() == 0) {
            write(0);
            return;
        }

        byte[] bytes = text.getBytes("UTF-8");

        // write string byte length
        write(bytes.length);

        // write string bytes
        _stream.write(bytes);

        // record bytes written
        _bytesWritten+=bytes.length;
    }

    /**
     * Write an enum.
     *
     * @param enumConstant  The enum constant.
     *
     * @param <T>           The enum type.
     *
     * @throws IOException
     */
    public <T extends Enum<T>> void write(T enumConstant) throws IOException {
        PreCon.notNull(enumConstant);

        write(enumConstant.name());
    }

    /**
     * Write a {@code Location}.
     *
     * @param location  The location.
     *
     * @throws IOException
     */
    public void write(Location location) throws IOException {
        PreCon.notNull(location);

        write(location.getWorld().getName());
        write(location.getX());
        write(location.getY());
        write(location.getZ());
        write(location.getYaw());
        write(location.getPitch());
    }

    /**
     * Write an {@code ItemStack}.
     *
     * @param itemStack  The item stack.
     *
     * @throws IOException
     */
    public void write(@Nullable ItemStack itemStack) throws IOException {

        write(itemStack == null);
        if (itemStack == null)
            return;

        // write basic data
        write(itemStack.getType());
        write(itemStack.getData().getData());
        write(itemStack.getAmount());
        write((int)itemStack.getDurability());

        Map<Enchantment, Integer> enchantMap = itemStack.getEnchantments();

        // write enchantments
        write(enchantMap.size());
        for (Entry<Enchantment, Integer> enchantmentIntegerEntry : enchantMap.entrySet()) {
            Integer level = enchantmentIntegerEntry.getValue();

            write(enchantmentIntegerEntry.getKey().getName());
            write(level);
        }

        ItemMeta meta = itemStack.getItemMeta();

        // write display name
        write(meta.getDisplayName());

        // write lore
        List<String> lore = meta.getLore();
        if (lore == null) {
            write(0);
        }
        else {
            write(lore.size());

            for (String line : lore) {
                write(line);
            }
        }

        Color color = ItemStackHelper.getColor(itemStack);
        write(color != null);
        if (color != null) {
            write(color.asRGB());
        }
    }

    /**
     * Serialize an {@code IGenericsSerializable} object.
     *
     * @param object  The object to serialize.
     * @param <T>     The object type.
     *
     * @throws IOException
     */
    public <T extends IGenericsSerializable> void write(@Nullable T object) throws IOException {
        write(object == null);
        if (object == null)
            return;

        object.serializeToBytes(this);
    }

    /**
     * Serialize an object.
     *
     * @param object  The object to serialize. Can be null.
     *
     * @throws IOException
     */
    public <T extends Serializable> void write(@Nullable T object) throws IOException {

        // write null flag
        write(object == null);
        if (object == null)
            return;

        // clear boolean buffer
        writeBooleans();

        ObjectOutputStream objectStream = new ObjectOutputStream(this);
        objectStream.writeObject(object);
    }

    /**
     * Close the stream.
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {

        flush();

        _stream.close();
    }

    /**
     * Flush buffers to stream.
     *
     * @throws IOException
     */
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
