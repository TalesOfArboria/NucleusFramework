/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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

import java.io.IOException;

/**
 * Mixin used by {@code GenericsByteWriter} to serialize an
 * object and by {@code GenericsByteReader} tp subsequently
 * deserialize data from the stream.
 *
 * <p>
 *     The implementer must be able to serialize itself into the stream using the
 *     provided instance of {@code GenericsByteWriter} and deserialize data into
 *     an empty instance of itself using the {@code GenericsByteReader}.
 * </p>
 * <p>
 *     For de-serialization, the implementer is required to have an empty constructor.
 *     The constructor does not have to be public.
 * </p>
 */
public interface IBinarySerializable {

    /**
     * Serialize the object into a generics byte writer.
     *
     * @param writer  The writer.
     */
    public void serializeToBytes(GenericsByteWriter writer) throws IOException;

    /**
     * Deserialize information from the reader into
     * the object.
     *
     * @param reader  The reader.
     */
    public void deserializeFromBytes(GenericsByteReader reader)
            throws IOException, ClassNotFoundException, InstantiationException;
}
