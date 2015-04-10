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
     * @return  The bytes or null if written as null.
     *
     * @throws IOException
     */
    @Nullable
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
     * @return  The {@link BigDecimal} or null if written as null.
     *
     * @throws IOException
     */
    @Nullable
    BigDecimal getBigDecimal() throws IOException;

    /**
     * Read the next group of bytes as a {@link BigInteger}.
     *
     * @return  The {@link BigInteger} or null if written as null.
     *
     * @throws IOException
     */
    @Nullable
    BigInteger getBigInteger() throws IOException;

    /**
     * Get the next group of bytes as a string.
     *
     * @return  The string or null if written as null.
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
     * @return  The String or null if written as null.
     *
     * @throws IOException
     */
    @Nullable
    String getString(Charset charset) throws IOException;

    /**
     * Get the next group of bytes as a UTF-8 string that is expected to be
     * no more than 255 bytes in length.
     *
     * @return  The string or null if written as null.
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
     * @return  The enum or null if written as null.
     *
     * @throws IOException
     */
    @Nullable
    <T extends Enum<T>> T getEnum(@Nullable Class<T> enumClass) throws IOException;

    /**
     * Get the next 16 bytes as a {@link UUID}.
     *
     * @return  The {@link UUID} or null if written as null.
     *
     * @throws IOException
     */
    @Nullable
    UUID getUUID() throws IOException;

    /**
     * Get the next group of bytes as a {@link SyncLocation}.
     *
     * @return  The {@link SyncLocation} or null if written as null.
     *
     * @throws IOException
     */
    @Nullable
    SyncLocation getLocation() throws IOException;

    /**
     * Get the next group of bytes as a {@link SyncLocation}.
     *
     * @param output  The output {@link SyncLocation} to put values into.
     *
     * @return  The output {@link SyncLocation} or null if written as null.
     *
     * @throws IOException
     */
    @Nullable
    SyncLocation getLocation(SyncLocation output) throws IOException;

    /**
     * Get the next group of bytes as an {@link EulerAngle}.
     *
     * @return  The {@link EulerAngle} or null if written as null.
     *
     * @throws IOException
     */
    @Nullable
    EulerAngle getEulerAngle() throws IOException;

    /**
     * Get the next group of bytes as an {@link EulerAngle}.
     *
     * @param output  The {@link EulerAngle} to put values into.
     *
     * @return  The output {@link EulerAngle} or null if written as null.
     *
     * @throws IOException
     */
    @Nullable
    EulerAngle getEulerAngle(EulerAngle output) throws IOException;

    /**
     * Get the next group of bytes as a {@link Vector}.
     *
     * @return  The {@link Vector} or null if written as null.
     *
     * @throws IOException
     */
    @Nullable
    Vector getVector() throws IOException;

    /**
     * Get the next group of bytes as a {@link Vector}.
     *
     * @param output  The output {@link Vector} to put values into.
     *
     * @return  The output {@link Vector} or null if written as null.
     *
     * @throws IOException
     */
    @Nullable
    Vector getVector(Vector output) throws IOException;

    /**
     * Get the next group of bytes as an {@link ItemStack}.
     *
     * @return  The {@link ItemStack} or null if written as null.
     *
     * @throws IOException
     */
    @Nullable
    ItemStack getItemStack() throws IOException;

    /**
     * Get an {@link IByteSerializable} object.
     *
     * @param objectClass  The object class.
     *
     * @return  The {@link IByteSerializable} object or null if written as null.
     *
     * @param <T>  The object type.
     *
     * @throws Exception
     */
    @Nullable
    <T extends IByteSerializable> T deserialize(Class<T> objectClass)
            throws IOException, InstantiationException;

    /**
     * Deserialize a {@link Serializable} object from the next set of bytes.
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
    <T extends Serializable> T deserializeObject(Class<T> objectClass)
            throws IOException, ClassNotFoundException;
}
