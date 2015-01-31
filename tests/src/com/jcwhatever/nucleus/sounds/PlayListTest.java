package com.jcwhatever.nucleus.sounds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.jcwhatever.bukkit.v1_8_R1.BukkitTester;
import com.jcwhatever.nucleus.NucleusTest;
import com.jcwhatever.nucleus.sounds.PlayList.PlayerSoundQueue;
import com.jcwhatever.nucleus.storage.YamlDataNode;
import com.jcwhatever.nucleus.utils.ArrayUtils;
import com.jcwhatever.nucleus.utils.file.FileUtils;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class PlayListTest {

    private Plugin _plugin = BukkitTester.mockPlugin("dummy");
    private Player _player = BukkitTester.login("dummy");

    /**
     * Make sure Nucleus and Bukkit are initialized.
     */
    @BeforeClass
    public static void init() {
        NucleusTest.init();

        Plugin plugin = BukkitTester.mockPlugin("dummy");
        String yml = FileUtils.scanTextFile(SoundManagerTest.class, "/resource-sounds.yml", StandardCharsets.UTF_8);

        YamlDataNode dataNode = new YamlDataNode(plugin, yml);
        dataNode.load();

        SoundManager.load(dataNode);
    }

    @Test
    public void testSize() throws Exception {

        List<ResourceSound> sounds =  SoundManager.getSounds();

        PlayList playList = new PlayList(_plugin, sounds);

        assertEquals(6, playList.size());
    }

    @Test
    public void testAddSound() throws Exception {

        ResourceSound sound1 = SoundManager.getSound("music1");
        ResourceSound sound2 = SoundManager.getSound("music2");

        PlayList playList = new PlayList(_plugin);
        assertEquals(0, playList.size());

        playList.addSound(sound1);
        assertEquals(1, playList.size());

        playList.addSound(sound2);
        assertEquals(2, playList.size());
    }

    @Test
    public void testRemoveSound() throws Exception {

        ResourceSound sound1 = SoundManager.getSound("music1");
        ResourceSound sound2 = SoundManager.getSound("music2");

        PlayList playList = new PlayList(_plugin);
        assertEquals(0, playList.size());

        playList.addSound(sound1);
        playList.addSound(sound2);
        assertEquals(2, playList.size());

        playList.removeSound(sound1);
        assertEquals(1, playList.size());

        playList.removeSound(sound1);
        assertEquals(1, playList.size());

        playList.removeSound(sound2);
        assertEquals(0, playList.size());
    }

    @Test
    public void testAddSounds() throws Exception {

        List<ResourceSound> sounds =  SoundManager.getSounds();

        PlayList playList = new PlayList(_plugin);

        playList.addSounds(sounds);
        assertEquals(6, playList.size());
    }

    @Test
    public void testClearSounds() throws Exception {
        List<ResourceSound> sounds =  SoundManager.getSounds();

        PlayList playList = new PlayList(_plugin);

        playList.addSounds(sounds);
        assertEquals(6, playList.size());

        playList.clearSounds();
        assertEquals(0, playList.size());
    }

    @Test
    public void testGetSounds() throws Exception {
        List<ResourceSound> sounds =  SoundManager.getSounds();

        PlayList playList = new PlayList(_plugin);

        playList.addSounds(sounds);
        assertEquals(6, playList.size());

        sounds = playList.getSounds();
        assertEquals(6, sounds.size());
    }

    @Test
    public void testAddPlayer() throws Exception {

        SoundSettings settings = new SoundSettings();

        PlayList playList = new PlayList(_plugin, SoundManager.getSounds());
        assertEquals(null, playList.getSoundQueue(_player));

        // add player
        assertEquals(true, playList.addPlayer(_player, settings));
        assertNotEquals(null, playList.getSoundQueue(_player));
    }

    @Test
    public void testRemovePlayer() throws Exception {

        SoundSettings settings = new SoundSettings();

        PlayList playList = new PlayList(_plugin, SoundManager.getSounds());
        assertEquals(null, playList.getSoundQueue(_player));

        // add player
        assertEquals(true, playList.addPlayer(_player, settings));
        assertNotEquals(null, playList.getSoundQueue(_player));

        // remove player
        assertEquals(true, playList.removePlayer(_player));

        // Still returns sound queue until the audio is finished
        assertNotEquals(null, playList.getSoundQueue(_player));

        // wait for audio to end
        BukkitTester.pause(70);

        // sound queue should no longer be available
        assertEquals(null, playList.getSoundQueue(_player));
    }

    @Test
    public void testSoundQueueNoLoop() throws Exception {

        SoundSettings settings = new SoundSettings();

        List<ResourceSound> sounds = ArrayUtils.asList(
                SoundManager.getSound("music1"),
                SoundManager.getSound("music2"),
                SoundManager.getSound("voice1")
        );

        PlayList playList = new PlayList(_plugin, sounds);
        playList.setLoop(false);

        assertEquals(null, playList.getSoundQueue(_player));

        // add player
        assertEquals(true, playList.addPlayer(_player, settings));

        PlayerSoundQueue queue  = playList.getSoundQueue(_player);

        assertTrue(queue != null);
        assertEquals(_player, queue.getPlayer());

        assertEquals(sounds.get(0), queue.getCurrent());

        BukkitTester.pause(30);

        assertEquals(sounds.get(1), queue.getCurrent());

        BukkitTester.pause(22);

        assertEquals(sounds.get(2), queue.getCurrent());

        BukkitTester.pause(22);

        assertEquals(null, queue.getCurrent());

        BukkitTester.pause(30);

        playList.removePlayer(_player);
    }

    @Test
    public void testSoundQueueLoop() throws Exception {

        SoundSettings settings = new SoundSettings();

        List<ResourceSound> sounds = ArrayUtils.asList(
                SoundManager.getSound("music1"),
                SoundManager.getSound("music2"),
                SoundManager.getSound("voice1")
        );

        PlayList playList = new PlayList(_plugin, sounds);
        playList.setLoop(true);

        assertEquals(null, playList.getSoundQueue(_player));

        // add player
        assertEquals(true, playList.addPlayer(_player, settings));

        PlayerSoundQueue queue  = playList.getSoundQueue(_player);

        assertTrue(queue != null);
        assertEquals(_player, queue.getPlayer());

        assertEquals(sounds.get(0), queue.getCurrent());

        BukkitTester.pause(30);

        assertEquals(sounds.get(1), queue.getCurrent());

        BukkitTester.pause(22);

        assertEquals(sounds.get(2), queue.getCurrent());

        BukkitTester.pause(22);

        assertEquals(sounds.get(0), queue.getCurrent());

        BukkitTester.pause(22);

        assertEquals(sounds.get(1), queue.getCurrent());

        playList.removePlayer(_player);

        BukkitTester.pause(30);
    }

    @Test
    public void testSoundQueueNoLoopNoSounds() throws Exception {

        SoundSettings settings = new SoundSettings();

        PlayList playList = new PlayList(_plugin);
        playList.setLoop(false);

        assertEquals(null, playList.getSoundQueue(_player));

        // add player
        assertEquals(false, playList.addPlayer(_player, settings));

        PlayerSoundQueue queue  = playList.getSoundQueue(_player);

        assertTrue(queue == null);

        playList.removePlayer(_player);

        BukkitTester.pause(30);
    }

    @Test
    public void testSoundQueueLoopNoSounds() throws Exception {

        SoundSettings settings = new SoundSettings();

        PlayList playList = new PlayList(_plugin);
        playList.setLoop(true);

        assertEquals(null, playList.getSoundQueue(_player));

        // add player
        assertEquals(false, playList.addPlayer(_player, settings));

        PlayerSoundQueue queue  = playList.getSoundQueue(_player);

        assertTrue(queue == null);

        playList.removePlayer(_player);

        BukkitTester.pause(30);
    }
}