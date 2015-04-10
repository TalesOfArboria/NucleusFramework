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

import java.io.IOException;

/**
 * Mixin used by {@link BasicByteWriter} to serialize an object and by
 * {@link BasicByteReader} to subsequently deserialize data from a stream.
 *
 * <p>The implementer must be able to serialize itself into the stream using the
 * provided instance of {@link BasicByteWriter} and deserialize data into an empty
 * instance of itself using the {@link BasicByteReader}.</p>
 *
 * <p>For de-serialization, the implementer is required to have an empty constructor.
 * The constructor does not have to be public.</p>
 */
public interface IBinarySerializable {

    /**
     * Serialize the object into a {@link BasicByteWriter} stream.
     *
     * @param writer  The writer.
     */
    void serialize(IByteWriter writer) throws IOException;

    /**
     * Deserialize information from the reader into the {@link IBinarySerializable} object.
     *
     * @param reader  The reader.
     */
    void deserialize(IByteReader reader)
            throws IOException, ClassNotFoundException, InstantiationException;
}
