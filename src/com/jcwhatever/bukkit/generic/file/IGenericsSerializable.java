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
 *     For de-serialization, the implementer is required to have an empty public constructor.
 * </p>
 */
public interface IGenericsSerializable {

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
    public void deserializeFromBytes(GenericsByteReader reader) throws IOException, ClassNotFoundException;
}
