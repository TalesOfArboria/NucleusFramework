package com.jcwhatever.bukkit.generic.file;

import java.io.IOException;

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
