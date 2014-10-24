package com.jcwhatever.bukkit.generic.sounds;

import com.jcwhatever.bukkit.generic.storage.IDataNode;

/**
 * A resource sound that represents a sound effect.
 */
public class EffectSound extends ResourceSound {

    /**
     * Constructor.
     *
     * @param dataNode  The sound effect data node.
     */
    EffectSound(IDataNode dataNode) {
        super(dataNode);
    }
}
