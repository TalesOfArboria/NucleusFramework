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


package com.jcwhatever.bukkit.generic.sounds;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.collections.LifespanEndAction;
import com.jcwhatever.bukkit.generic.collections.TimedArrayList;
import com.jcwhatever.bukkit.generic.events.bukkit.sounds.PlayResourceSoundEvent;
import com.jcwhatever.bukkit.generic.sounds.Playing.Future;
import com.jcwhatever.bukkit.generic.storage.DataStorage;
import com.jcwhatever.bukkit.generic.storage.DataStorage.DataPath;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.Utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Manages resource sounds globally.
 */
public class SoundManager {

    private SoundManager() {}

    private static Map<UUID, TimedArrayList<Playing>> _playing = new HashMap<>(100);
    private static Map<String, ResourceSound> _sounds;

    static {
        load();
    }

    /**
     * Get all resource sounds.
     */
    public static List<ResourceSound> getSounds() {
        return new ArrayList<>(_sounds.values());
    }

    /**
     * Get a resource sound by name.
     *
     * @param name  The name of the sound.
     */
    @Nullable
    public static ResourceSound getSound(String name) {
        PreCon.notNullOrEmpty(name);

        return _sounds.get(name.toLowerCase());
    }

    /**
     * Get resource sounds by type.
     *
     * @param type  The type to look for.
     */
    public static Set<ResourceSound> getSounds(Class<? extends ResourceSound> type) {
        PreCon.notNull(type);

        Set<ResourceSound> results = new HashSet<>(_sounds.size());

        for (ResourceSound sound : _sounds.values()) {

            if (type.isInstance(sound))
                results.add(sound);
        }

        return results;
    }

    /**
     * Get the resource sounds being played to the specified player.
     *
     * @param p  The player to check.
     */
    public static Set<ResourceSound> getSounds(Player p) {

        List<Playing> playing = _playing.get(p.getUniqueId());

        if (playing == null || playing.isEmpty())
            return new HashSet<>(0);

        HashSet<ResourceSound> result = new HashSet<>(playing.size());

        for (Playing play : playing) {
            result.add(play.getResourceSound());
        }

        return result;
    }

    /**
     * Get information about the resource sounds being played
     * for the specified player.
     *
     * @param p  The player to check.
     */
    public static List<Playing> getPlaying(Player p) {

        List<Playing> playing = _playing.get(p.getUniqueId());

        if (playing == null || playing.isEmpty())
            return new ArrayList<>(0);

        return new ArrayList<>(playing);
    }

    /**
     * Play a resource sound to a player at the players location.
     *
     * @param plugin  The requesting plugin.
     * @param p       The player who will hear the sound.
     * @param sound   The resource sound to play.
     * @param volume  The volume of the sound.
     *
     * @return  A future used to run a callback when the sound is finished playing.
     */
    public static Future playSound(Plugin plugin, Player p, ResourceSound sound, float volume) {
        return playSound(plugin, p, sound, null, volume, null);
    }

    /**
     * Play a resource sound to a player.
     *
     * @param plugin    The requesting plugin.
     * @param p         The player who will hear the sound.
     * @param sound     The resource sound to play.
     * @param location  The location to play the sound at.
     * @param volume    The volume of the sound.
     *
     * @return  A future used to run a callback when the sound is finished playing.
     */
    public static Future playSound(Plugin plugin, final Player p, ResourceSound sound,
                                   @Nullable Location location, float volume) {
        return playSound(plugin, p, sound, location, volume, null);
    }

    /**
     * Play a resource sound to a player.
     *
     * @param plugin             The requesting plugin.
     * @param p                  The player who will hear the sound.
     * @param sound              The resource sound to play.
     * @param location           The location to play the sound at.
     * @param volume             The volume of the sound.
     * @param transcriptViewers  Players who will see the sound transcript, if any.
     *
     * @return  A future used to run a callback when the sound is finished playing.
     */
    public static Future playSound(Plugin plugin, final Player p, ResourceSound sound,
                                   @Nullable Location location, float volume,
                                   @Nullable Collection<Player> transcriptViewers) {

        // substitute players location if no sound is provided.
        if (location == null)
            location = p.getLocation();


        Playing playing = new Playing(p, sound, location, volume);

        // run event
        PlayResourceSoundEvent event = new PlayResourceSoundEvent(p, sound, location, volume);
        GenericsLib.getEventManager().callBukkit(event);

        // see if the event was cancelled
        if (event.isCancelled())
            return playing.setFinished();

        // run play sound command
        String cmd = getPlaySoundCommand(event.getResourceSound().getName(), event.getPlayer(),
                event.getLocation(), event.getVolume());
        Utils.executeAsConsole(cmd);

        // get timed list to store playing object in.
        TimedArrayList<Playing> currentPlaying = _playing.get(p.getUniqueId());

        if (currentPlaying == null) {
            // create timed list for player and add callback to set a
            // Playing item as finished when the items time expires.
            currentPlaying = new TimedArrayList<>(3);
            currentPlaying.addOnLifespanEnd(new LifespanEndAction<Playing>() {
                @Override
                public void onEnd(Playing item) {
                    item.setFinished();
                }
            });

            _playing.put(p.getUniqueId(), currentPlaying);
        }

        // add playing sound to timed list, will expire when the song ends.
        currentPlaying.add(playing, sound.getDurationTicks());

        // display transcript to players if the sound is a voice
        // sound and transcript viewers are provided.
        if (sound instanceof VoiceSound &&
                transcriptViewers != null &&
                !transcriptViewers.isEmpty()) {

            Transcript transcript = ((VoiceSound) sound).getTranscript();
            if (transcript != null) {
                transcript.tell(plugin, transcriptViewers);
            }
        }

        return playing.getFuture();
    }

    /*
     * Get the command to run to make a player hear a sound.
     */
    private static String getPlaySoundCommand(String soundName, Player p, Location loc, float volume) {
        return "playsound " + soundName + ' ' + p.getName() + ' ' +
                loc.getBlockX() + ' ' + loc.getBlockY() + ' ' + loc.getBlockZ() + ' ' + volume;
    }

    /*
     * Load resource sounds
     */
    private static void load() {

        IDataNode config = DataStorage.getStorage(GenericsLib.getLib(), new DataPath("resource-sounds"));
        config.load();

        Set<String> soundNames = config.getSubNodeNames();

        _sounds = new HashMap<>(soundNames.size());

        for (String soundName : soundNames) {

            IDataNode soundNode = config.getNode(soundName);

            ResourceSound sound = getNewSound(soundNode);

            _sounds.put(sound.getName().toLowerCase(), sound);
        }
    }

    /*
     * Create a new resource sound instance
     */
    private static ResourceSound getNewSound(IDataNode node) {

        String type = node.getString("type");

        if (type == null || type.isEmpty()) {
            int diskId = node.getInteger("disk-id", -1);
            type = diskId == -1
                    ? "music"
                    : "disc";
        }

        switch(type.toLowerCase()) {

            case "music":
                return new MusicSound(node);

            case "disc":
                return new MusicDiskSound(node);

            case "effect":
                return new EffectSound(node);

            case "voice":
                return new VoiceSound(node);

            default:
                throw new RuntimeException("Invalid sound type in resource sounds: " + type);
        }
    }

}
