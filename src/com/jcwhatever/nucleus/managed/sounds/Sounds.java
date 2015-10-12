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

package com.jcwhatever.nucleus.managed.sounds;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.managed.resourcepacks.sounds.types.IResourceSound;
import com.jcwhatever.nucleus.utils.observer.future.IFutureResult;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Static convenience methods for accessing the global sound manager.
 */
public final class Sounds {

    private Sounds() {}

    /**
     * Get a resource pack sound by sound path.
     *
     * @param soundPath  The path of the sound. The path is the name of the
     *                   resource pack followed by a period then the name of the sound.
     *                   i.e. "resourcePackName.soundName"
     *
     * @return  The resource sound or null if not found.
     */
    @Nullable
    public static IResourceSound get(String soundPath) {
        return manager().get(soundPath);
    }

    /**
     * Get the resource sounds being played to the specified player.
     *
     * @param player  The player to check.
     */
    public static Collection<IResourceSound> getSounds(Player player) {
        return manager().getPlaying(player);
    }

    /**
     * Get the resource sounds being played to the specified player.
     *
     * @param player  The player to check.
     * @param output  The output collection to add results to.
     *
     * @return  The output collection.
     */
    public static <T extends Collection<IResourceSound>> T getSounds(Player player, T output) {
        return manager().getPlaying(player, output);
    }

    /**
     * Get information about the resource sounds being played
     * for the specified player.
     *
     * @param player  The player to check.
     */
    public static Collection<ISoundContext> getContexts(Player player) {
        return manager().getContexts(player);
    }

    /**
     * Get information about the resource sounds being played
     * for the specified player.
     *
     * @param player  The player to check.
     * @param output  The output collection to add results to.
     *
     * @return  The output collection.
     */
    public static <T extends Collection<ISoundContext>> T getContexts(Player player, T output) {
        return manager().getContexts(player, output);
    }

    /**
     * Play a resource sound to a player at the players location.
     *
     * @param plugin  The requesting plugin.
     * @param player  The player who will hear the sound.
     * @param sound   The resource sound to play.
     *
     * @return  A future used to run a success callback when the sound is finished playing.
     */
    public static IFutureResult<ISoundContext> playSound(Plugin plugin, Player player, IResourceSound sound) {
        return manager().playSound(plugin, player, sound);
    }

    /**
     * Play a resource sound to a player.
     *
     * @param plugin    The requesting plugin.
     * @param player    The player who will hear the sound.
     * @param sound     The resource sound to play.
     * @param settings  The settings to use.
     *
     * @return  A future used to run a success callback when the sound is finished playing.
     */
    public static IFutureResult<ISoundContext> playSound(Plugin plugin, Player player,
                                           IResourceSound sound, SoundSettings settings) {
        return manager().playSound(plugin, player, sound, settings);
    }

    /**
     * Play a resource sound to a player.
     *
     * @param plugin             The requesting plugin.
     * @param player             The player who will hear the sound.
     * @param sound              The resource sound to play.
     * @param settings           The settings to use to play the sound. The settings are cloned
     *                           within the method. If the setting has no locations, the players
     *                           location is used.
     * @param transcriptViewers  Players who will see the sound transcript, if any.
     *
     * @return  A future used to run a callback when the sound is finished playing.
     */
    public static IFutureResult<ISoundContext> playSound(Plugin plugin, Player player,
                                           IResourceSound sound, SoundSettings settings,
                                           @Nullable Collection<Player> transcriptViewers) {
        return manager().playSound(plugin, player, sound, settings, transcriptViewers);
    }

    /**
     * Play a sound effect by client side sound name.
     *
     * <p>The sound is played at the players location.</p>
     *
     * <p>The sound does not have to be pre-defined. The sound name is
     * sent directly to the client.</p>
     *
     * @param clientSoundName  The client side sound name.
     * @param player           The player to play the sound to.
     */
    public static void playEffect(String clientSoundName, Player player) {
        manager().playEffect(clientSoundName, player);
    }

    /**
     * Play a sound effect by client side sound name.
     *
     * <p>The sound is played at the players location.</p>
     *
     * <p>The sound does not have to be pre-defined. The sound name is
     * sent directly to the client.</p>
     *
     * @param clientSoundName  The client side sound name.
     * @param players          The collection of players to play the sound to.
     */
    public static void playEffect(String clientSoundName, Collection<Player> players) {
        manager().playEffect(clientSoundName, players);
    }

    /**
     * Play a sound effect by client side sound name.
     *
     * <p>The sound does not have to be pre-defined. The sound name is
     * sent directly to the client.</p>
     *
     * @param clientSoundName  The client side sound name.
     * @param player           The player to play the sound to.
     * @param location         The location the sound is played at.
     */
    public static void playEffect(String clientSoundName, Player player, Location location) {
        manager().playEffect(clientSoundName, player, location);
    }

    /**
     * Play a sound effect by client side sound name.
     *
     * <p>The sound does not have to be pre-defined. The sound name is
     * sent directly to the client.</p>
     *
     * @param clientSoundName  The client side sound name.
     * @param players          The collection of players to play the sound to.
     * @param location         The location the sound is played at.
     */
    public static void playEffect(String clientSoundName, Collection<Player> players, Location location) {
        manager().playEffect(clientSoundName, players, location);
    }

    /**
     * Play a sound effect by client side sound name.
     *
     * <p>The sound is played at the players location.</p>
     *
     * <p>The sound does not have to be pre-defined. The sound name is
     * sent directly to the client.</p>
     *
     * @param clientSoundName  The client side sound name.
     * @param player           The player to play the sound to.
     * @param volume           The volume of the sound.
     * @param pitch            The sound pitch.
     */
    public static void playEffect(String clientSoundName, Player player, float volume, float pitch) {
        manager().playEffect(clientSoundName, player, volume, pitch);
    }

    /**
     * Play a sound effect by client side sound name.
     *
     * <p>The sound is played at the players location.</p>
     *
     * <p>The sound does not have to be pre-defined. The sound name is
     * sent directly to the client.</p>
     *
     * @param clientSoundName  The client side sound name.
     * @param players          The collection of players to play the sound to.
     * @param volume           The volume of the sound.
     * @param pitch            The sound pitch.
     */
    public static void playEffect(String clientSoundName, Collection<Player> players,
                    float volume, float pitch) {
        manager().playEffect(clientSoundName, players, volume, pitch);
    }

    /**
     * Play a sound effect by client side sound name.
     *
     * <p>The sound does not have to be pre-defined. The sound name is
     * sent directly to the client.</p>
     *
     * @param clientSoundName  The client side sound name.
     * @param player           The player to play the sound to.
     * @param location         The location the sound is played at.
     * @param volume           The volume of the sound.
     * @param pitch            The sound pitch.
     */
    public static void playEffect(String clientSoundName, Player player, Location location,
                    float volume, float pitch) {
        manager().playEffect(clientSoundName, player, location, volume, pitch);
    }

    /**
     * Play a sound effect by client side sound name.
     *
     * <p>The sound does not have to be pre-defined. The sound name is
     * sent directly to the client.</p>
     *
     * @param clientSoundName  The client side sound name.
     * @param players          The collection of players to play the sound to.
     * @param location         The location the sound is played at.
     * @param volume           The volume of the sound.
     * @param pitch            The sound pitch.
     */
    public static void playEffect(String clientSoundName, Collection<Player> players,
                    Location location, float volume, float pitch) {
        manager().playEffect(clientSoundName, players, location, volume, pitch);
    }

    private static ISoundManager manager() {
        return Nucleus.getSoundManager();
    }
}
