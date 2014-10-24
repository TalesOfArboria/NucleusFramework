package com.jcwhatever.bukkit.generic.mixins;

import org.bukkit.entity.Player;

/**
 * Mixin defines an implementation as being a
 * {@code Player} wrapper.
 */
public interface IPlayerWrapper {

    /**
     * Get the encapsulated {@code Player} object.
     */
    Player getHandle();
}
