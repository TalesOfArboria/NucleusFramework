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

import com.jcwhatever.nucleus.utils.coords.SyncLocation;

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
 * Interface for a byte reader.
 */
public interface IByteReader {

    /**
     * Get the number of bytes read so far.
     */
    long getBytesRead();

    /**
     * Skip over a number of bytes without returning them.
     *
     * @param byteDistance  The number of bytes to skip.
     *
     * @return  The number of bytes skipped.
     *
     * @throws IOException
     */
    long skip(long byteDistance) throws IOException;

    /**
     * Get a boolean.
     *
     * @throws IOException
     */
    boolean getBoolean() throws IOException;

    /**
     * Get the next byte.
     *
     * @throws IOException
     */
    byte getByte() throws IOException;

    /**
     * Get the next byte array.
     *
     * <p>Gets an array by first reading an integer (4 bytes) which
     * indicates the length of the array, then reads the number of bytes indicated.</p>
     *
     * <p>If the number of bytes indicated is 0, then an empty byte array is returned.</p>
     *
     * @throws IOException
     */
    byte[] getBytes() throws IOException;

    /**
     * Read the next 2 bytes and return them as a short.
     *
     * @throws IOException
     */
    short getShort() throws IOException;

    /**
     * Read the next 4 bytes and return them as an integer.
     *
     * @throws IOException
     */
    int getInteger() throws IOException;

    /**
     * Read the next 8 bytes an return them as a long value.
     *
     * @throws IOException
     */
    long getLong() throws IOException;

    /**
     * Read the next group of bytes as a {@link BigDecimal}.
     *
     * @throws IOException
     */
    @Nullable
    BigDecimal getBigDecimal() throws IOException;

    /**
     * Read the next group of bytes as a {@link BigInteger}.
     *
     * @throws IOException
     */
    @Nullable
    BigInteger getBigInteger() throws IOException;

    /**
     * Get the next group of bytes as a string.
     *
     * @throws IOException
     */
    @Nullable
    String getString() throws IOException;

    /**
     * Get the next group of bytes as a string.
     *
     * @param charset  The character set encoding to use.
     *
     * @throws IOException
     */
    @Nullable
    String getString(Charset charset) throws IOException;

    /**
     * Get the next group of bytes as a UTF-8 string that is expected to be
     * no more than 255 bytes in length.
     *
     * @throws IOException
     */
    @Nullable
    String getSmallString() throws IOException;

    /**
     * Get the next group of bytes as a float value.
     *
     * @throws IOException
     */
    float getFloat() throws IOException;

    /**
     * Get the next group of bytes as a double value.
     *
     * @throws IOException
     */
    double getDouble() throws IOException;

    /**
     * Get the next group of bytes as an enum.
     *
     * @param enumClass  The enum class.
     *
     * @param <T>  The enum type.
     *
     * @throws IOException
     */
    <T extends Enum<T>> T getEnum(Class<T> enumClass) throws IOException;

    /**
     * Get the next 16 bytes as a UUID.
     *
     * @throws IOException
     */
    UUID getUUID() throws IOException;

    /**
     * Get the next group of bytes as a location.
     *
     * @throws IOException
     */
    SyncLocation getLocation() throws IOException;

    /**
     * Get the next group of bytes as an EulerAngle.
     *
     * <p>The angle is read as x, y and z value as doubles.
     * (See {@link #getDouble})</p>
     *
     * @throws IOException
     */
    EulerAngle getEulerAngle() throws IOException;

    /**
     * Get the next group of bytes as a Vector.
     *
     * @throws IOException
     */
    Vector getVector() throws IOException;

    /**
     * Get the next group of bytes as an item stack.
     *
     * @throws IOException
     */
    @Nullable
    ItemStack getItemStack() throws IOException;

    /**
     * Get an {@link IBinarySerializable} object.
     *
     * @param objectClass  The object class.
     *
     * @param <T>  The object type.
     *
     * @throws Exception
     */
    @Nullable
    <T extends IBinarySerializable> T getBinarySerializable(Class<T> objectClass)
            throws IOException, InstantiationException;

    /**
     * Deserialize an object from the next set of bytes.
     *
     * @param objectClass  The object class.
     *
     * @param <T>  The object type.
     *
     * @return The deserialized object or null if the object was written as null.
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Nullable
    <T extends Serializable> T getObject(Class<T> objectClass)
            throws IOException, ClassNotFoundException;
}
