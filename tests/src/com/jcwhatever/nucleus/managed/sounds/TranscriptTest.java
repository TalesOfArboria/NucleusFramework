package com.jcwhatever.nucleus.managed.sounds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.jcwhatever.v1_8_R3.BukkitTester;
import com.jcwhatever.nucleus.NucleusTest;
import com.jcwhatever.nucleus.managed.sounds.Transcript.Paragraph;
import com.jcwhatever.nucleus.storage.YamlDataNode;
import com.jcwhatever.nucleus.utils.file.FileUtils;
import com.jcwhatever.nucleus.utils.observer.update.UpdateSubscriber;

import org.bukkit.plugin.Plugin;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Tests {@link Transcript}.
 */
public class TranscriptTest {

    private Plugin _plugin = BukkitTester.mockPlugin("dummy");

    private String _testText = "This is a paragraph1{p:1}Text at 1 seconds.";
    private String[] _testTexts = new String[] {
            "This is a paragraph1",
            "Text at 1 seconds.",
            null
    };

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
    }

    /**
     * Make sure {@link Transcript#getRawTranscript} returns the correct value.
     */
    @Test
    public void testGetRawTranscript() throws Exception {

        Transcript transcript = new Transcript(_testText);

        assertEquals(_testText, transcript.getRawTranscript());
    }

    /**
     * Make sure {@link Transcript#getParagraphs} returns the correct values.
     */
    @Test
    public void testGetParagraphs() throws Exception {
        Transcript transcript = new Transcript(_testText);

        List<Paragraph> paragraphs = transcript.getParagraphs();

        assertEquals(2, paragraphs.size());

        assertEquals(_testTexts[0], paragraphs.get(0).getText());
        assertEquals(_testTexts[1], paragraphs.get(1).getText());

        assertEquals(0, paragraphs.get(0).getStartTimeSeconds());
        assertEquals(1, paragraphs.get(1).getStartTimeSeconds());
    }

    /**
     * Make sure {@link Transcript#run} works correctly.
     */
    @Test
    public void testRun() throws Exception {

        Transcript transcript = new Transcript(_testText);

        final Set<String> textRan = new HashSet<>(3);

        long startedAt = System.currentTimeMillis();

        transcript.run(_plugin, new UpdateSubscriber<String>() {

            int count = 0;

            @Override
            public void on(String text) {

                assertEquals(_testTexts[count], text);
                textRan.add(text);
                count++;

            }
        });

        // wait for transcript to finish running.
        long timeout = System.currentTimeMillis() + 1500;
        while (!textRan.contains(null) && timeout > System.currentTimeMillis()) {

            BukkitTester.heartBeat();

            Thread.sleep(10);
        }

        long elapsed = System.currentTimeMillis() - startedAt;

        //System.out.println("Elapsed time: " + elapsed);

        assertTrue(elapsed >= 925); // should be about 1 second elapsed

        // make sure all expected text was received by the subscriber
        assertEquals(3, textRan.size());
    }
}