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

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Interface for a byte writer.
 */
public interface IByteWriter {

    /**
     * Get the number of bytes written.
     */
    long getBytesWritten();

    /**
     * Write a boolean value.
     *
     * @param booleanValue  The boolean value.
     *
     * @throws IOException
     */
    void write(boolean booleanValue) throws IOException;

    /**
     * Write a byte.
     *
     * @param byteValue  The byte.
     *
     * @throws IOException
     */
    void write(byte byteValue) throws IOException;

    /**
     * Write a byte array.
     *
     * @param byteArray  The byte array.
     *
     * @throws IOException
     */
    void write(@Nullable byte[] byteArray) throws IOException;

    /**
     * Write a 16-bit number (2 bytes).
     *
     * @param shortValue  The short.
     *
     * @throws IOException
     */
    void write(short shortValue) throws IOException;

    /**
     * Write a 32-bit number (4 bytes).
     *
     * @param integerValue  The integer.
     *
     * @throws IOException
     */
    void write(int integerValue) throws IOException;

    /**
     * Write a 64 bit number (8 bytes).
     *
     * @param longValue  The long.
     *
     * @throws IOException
     */
    void write(long longValue) throws IOException;

    /**
     * Write a floating point number.
     *
     * @param floatValue  The floating point value.
     *
     * @throws IOException
     */
    void write(float floatValue) throws IOException;

    /**
     * Write a double number.
     *
     * @param doubleValue  The floating point double.
     *
     * @throws IOException
     */
    void write(double doubleValue) throws IOException;

    /**
     * Write a {@link BigDecimal} number.
     *
     * @param decimal  The big decimal.
     *
     * @throws IOException
     */
    void write(@Nullable BigDecimal decimal) throws IOException;

    /**
     * Write a {@link BigInteger} number.
     *
     * @param integer  The big integer.
     *
     * @throws IOException
     */
    void write(@Nullable BigInteger integer) throws IOException;

    /**
     * Write a text string.
     *
     * @param text  The text to write. Can be null.
     *
     * @throws IOException
     */
    void write(@Nullable String text) throws IOException;

    /**
     * Write a text string.
     *
     * @param text     The text to write. Can be null.
     * @param charset  The charset encoding to use.
     *
     * @throws IOException
     */
    void write(@Nullable String text, Charset charset) throws IOException;

    /**
     * Write a text string that is expected to be no more than 255 bytes in length
     * using UTF-8 encoding.
     *
     * @param text  The text to write. Can be null.
     *
     * @throws IOException
     */
    void writeSmallString(String text) throws IOException;

    /**
     * Write an enum.
     *
     * @param enumConstant  The enum constant.
     *
     * @param <T>  The enum type.
     *
     * @throws IOException
     */
    <T extends Enum<T>> void write(T enumConstant) throws IOException;

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
    void write(UUID uuid) throws IOException;

    /**
     * Write a {@link Location}.
     *
     * @param location  The location.
     *
     * @throws IOException
     */
    void write(Location location) throws IOException;

    /**
     * Write an {@link EulerAngle}.
     *
     * @param angle  The angle.
     *
     * @throws IOException
     */
    void write(EulerAngle angle) throws IOException;

    /**
     * Write an {@link Vector}.
     *
     * @param vector  The vector.
     *
     * @throws IOException
     */
    void write(Vector vector) throws IOException;

    /**
     * Write an {@link ItemStack}.
     *
     * @param itemStack  The item stack.
     *
     * @throws IOException
     */
    void write(@Nullable ItemStack itemStack) throws IOException;

    /**
     * Serialize an {@link IBinarySerializable} object.
     *
     * @param object  The object to serialize.
     *
     * @param <T>  The object type.
     *
     * @throws IOException
     */
    <T extends IBinarySerializable> void write(@Nullable T object) throws IOException;

    /**
     * Serialize an object.
     *
     * @param object  The object to serialize. Can be null.
     *
     * @throws IOException
     */
    <T extends Serializable> void write(@Nullable T object) throws IOException;
}
