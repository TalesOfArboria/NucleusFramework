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

import com.jcwhatever.nucleus.managed.resourcepacks.sounds.types.IResourceSound;
import com.jcwhatever.nucleus.utils.observer.future.IFutureResult;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Interface for the global resource sound manager.
 */
public interface ISoundManager {

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
    IResourceSound get(String soundPath);

    /**
     * Get the resource sounds being played to the specified player.
     *
     * @param player  The player to check.
     */
    Collection<IResourceSound> getPlaying(Player player);

    /**
     * Get the resource sounds being played to the specified player.
     *
     * @param player  The player to check.
     * @param output  The output collection to add results to.
     *
     * @return  The output collection.
     */
    <T extends Collection<IResourceSound>> T getPlaying(Player player, T output);

    /**
     * Get information about the resource sounds being played
     * for the specified player.
     *
     * @param player  The player to check.
     */
    Collection<ISoundContext> getContexts(Player player);

    /**
     * Get information about the resource sounds being played
     * for the specified player.
     *
     * @param player  The player to check.
     * @param output  The output collection to add results to.
     *
     * @return  The output collection.
     */
    <T extends Collection<ISoundContext>> T getContexts(Player player, T output);

    /**
     * Play a resource sound to a player at the players location.
     *
     * @param plugin  The requesting plugin.
     * @param player  The player who will hear the sound.
     * @param sound   The resource sound to play.
     *
     * @return  A future used to run a success callback when the sound is finished playing.
     */
    IFutureResult<ISoundContext> playSound(Plugin plugin, Player player, IResourceSound sound);

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
    IFutureResult<ISoundContext> playSound(Plugin plugin, Player player,
                                    IResourceSound sound, SoundSettings settings);

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
    IFutureResult<ISoundContext> playSound(Plugin plugin, Player player,
                                    IResourceSound sound, SoundSettings settings,
                                    @Nullable Collection<Player> transcriptViewers);

    /**
     * Play a sound effect by client side sound name.
     *
     * <p>The sound is played at the players location.</p>
     *
     * <p>The sound does not have to be a resource pack sound. The sound name is
     * sent directly to the client.</p>
     *
     * @param clientSoundName  The client side sound name.
     * @param player           The player to play the sound to.
     */
    void playEffect(String clientSoundName, Player player);

    /**
     * Play a sound effect by client side sound name.
     *
     * <p>The sound is played at the players location.</p>
     *
     * <p>The sound does not have to be a resource pack sound. The sound name is
     * sent directly to the client.</p>
     *
     * @param clientSoundName  The client side sound name.
     * @param players          The collection of players to play the sound to.
     */
    void playEffect(String clientSoundName, Collection<Player> players);

    /**
     * Play a sound effect by client side sound name.
     *
     * <p>The sound does not have to be a resource pack sound. The sound name is
     * sent directly to the client.</p>
     *
     * @param clientSoundName  The client side sound name.
     * @param player           The player to play the sound to.
     * @param location         The location the sound is played at.
     */
    void playEffect(String clientSoundName, Player player, Location location);

    /**
     * Play a sound effect by client side sound name.
     *
     * <p>The sound does not have to be a resource pack sound. The sound name is
     * sent directly to the client.</p>
     *
     * @param clientSoundName  The client side sound name.
     * @param players          The collection of players to play the sound to.
     * @param location         The location the sound is played at.
     */
    void playEffect(String clientSoundName, Collection<Player> players, Location location);

    /**
     * Play a sound effect by client side sound name.
     *
     * <p>The sound is played at the players location.</p>
     *
     * <p>The sound does not have to be a resource pack sound. The sound name is
     * sent directly to the client.</p>
     *
     * @param clientSoundName  The client side sound name.
     * @param player           The player to play the sound to.
     * @param volume           The volume of the sound.
     * @param pitch            The sound pitch.
     */
    void playEffect(String clientSoundName, Player player, float volume, float pitch);

    /**
     * Play a sound effect by client side sound name.
     *
     * <p>The sound is played at the players location.</p>
     *
     * <p>The sound does not have to be a resource pack sound. The sound name is
     * sent directly to the client.</p>
     *
     * @param clientSoundName  The client side sound name.
     * @param players          The collection of players to play the sound to.
     * @param volume           The volume of the sound.
     * @param pitch            The sound pitch.
     */
    void playEffect(String clientSoundName, Collection<Player> players,
                    float volume, float pitch);

    /**
     * Play a sound effect by client side sound name.
     *
     * <p>The sound does not have to be a resource pack sound. The sound name is
     * sent directly to the client.</p>
     *
     * @param clientSoundName  The client side sound name.
     * @param player           The player to play the sound to.
     * @param location         The location the sound is played at.
     * @param volume           The volume of the sound.
     * @param pitch            The sound pitch.
     */
    void playEffect(String clientSoundName, Player player, Location location,
                    float volume, float pitch);

    /**
     * Play a sound effect by client side sound name.
     *
     * <p>The sound does not have to be a resource pack sound. The sound name is
     * sent directly to the client.</p>
     *
     * @param clientSoundName  The client side sound name.
     * @param players          The collection of players to play the sound to.
     * @param location         The location the sound is played at.
     * @param volume           The volume of the sound.
     * @param pitch            The sound pitch.
     */
    void playEffect(String clientSoundName, Collection<Player> players,
                    Location location, float volume, float pitch);
}
