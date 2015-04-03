package com.jcwhatever.nucleus.managed.sounds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.jcwhatever.bukkit.v1_8_R2.BukkitTester;
import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.NucleusTest;
import com.jcwhatever.nucleus.internal.sounds.InternalSoundManager;
import com.jcwhatever.nucleus.managed.sounds.types.EffectSound;
import com.jcwhatever.nucleus.managed.sounds.types.MusicSound;
import com.jcwhatever.nucleus.managed.sounds.types.ResourceSound;
import com.jcwhatever.nucleus.managed.sounds.types.VoiceSound;
import com.jcwhatever.nucleus.storage.YamlDataNode;
import com.jcwhatever.nucleus.utils.file.FileUtils;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Collection;

/**
 * Tests {@link ISoundManager}.
 */
public class SoundManagerTest {

    private Plugin _plugin = BukkitTester.mockPlugin("dummy");
    private Player _player = BukkitTester.login("dummy");
    private SoundSettings _settings = new SoundSettings();

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

        ((InternalSoundManager)Nucleus.getSoundManager()).load(dataNode);
    }

    /**
     * Make sure {@link ISoundManager#getSounds()} works correctly.
     */
    @Test
    public void testGetSounds() throws Exception {

        Collection<ResourceSound> sounds = Nucleus.getSoundManager().getSounds();
        assertNotNull(sounds);
        assertEquals(6, sounds.size());
    }

    /**
     * Make sure {@link ISoundManager#getSound} works correctly.
     */
    @Test
    public void testGetSound() throws Exception {

        ResourceSound sound =Nucleus.getSoundManager().getSound("music1");
        assertNotNull(sound);

        assertTrue(sound instanceof MusicSound);
        assertEquals("music1", sound.getName());

        sound = Nucleus.getSoundManager().getSound("voice2");
        assertNotNull(sound);

        assertTrue(sound instanceof VoiceSound);
        assertEquals("voice2", sound.getName());

        sound = Nucleus.getSoundManager().getSound("effect1");
        assertNotNull(sound);

        assertTrue(sound instanceof EffectSound);
        assertEquals("effect1", sound.getName());
    }

    /**
     * Make sure {@link ISoundManager#getSounds(Player)} works correctly.
     * Also tests {@link ISoundManager#playSound} and {@link ISoundManager#getSounds}.
     */
    @Test
    public void testGetSounds1() throws Exception {

        // establish that there are no sounds playing to the player
        Collection<ResourceSound> sounds = Nucleus.getSoundManager().getSounds(_player);
        assertEquals(0, sounds.size());

        ResourceSound sound = Nucleus.getSoundManager().getSound("voice1");
        assertNotNull(sound);

        // play sound and make sure it is returned
        Nucleus.getSoundManager().playSound(_plugin, _player, sound, _settings);

        sounds = Nucleus.getSoundManager().getSounds(_player);
        assertEquals(1, sounds.size());

        // wait for sound to end
        BukkitTester.pause(25);

        // make sure the sound ended
        sounds = Nucleus.getSoundManager().getSounds(_player);
        assertEquals(0, sounds.size());
    }

    /**
     * Make sure {@link ISoundManager#getSounds(Class)} works correctly.
     */
    @Test
    public void testGetSounds2() throws Exception {
        Collection<MusicSound> music = Nucleus.getSoundManager().getSounds(MusicSound.class);
        assertEquals(2, music.size());

        Collection<VoiceSound> voice = Nucleus.getSoundManager().getSounds(VoiceSound.class);
        assertEquals(2, voice.size());

        Collection<EffectSound> effects = Nucleus.getSoundManager().getSounds(EffectSound.class);
        assertEquals(2, effects.size());
    }

    /**
     * Make sure {@link ISoundManager#getContexts} works correctly.
     * Also tests {@link ISoundManager#getSound} and {@link ISoundManager#getContexts}.
     */
    @Test
    public void testGetContexts() throws Exception {

        Collection<ISoundContext> playing = Nucleus.getSoundManager().getContexts(_player);
        assertEquals(0, playing.size());

        ResourceSound sound = Nucleus.getSoundManager().getSound("effect1");
        assertNotNull(sound);

        Nucleus.getSoundManager().playSound(_plugin, _player, sound, _settings);

        // play sound and check if it is returned
        playing = Nucleus.getSoundManager().getContexts(_player);
        assertEquals(1, playing.size());

        BukkitTester.pause(25); // wait for sound to end

        // make sure sound ended
        playing = Nucleus.getSoundManager().getContexts(_player);
        assertEquals(0, playing.size());
    }
}