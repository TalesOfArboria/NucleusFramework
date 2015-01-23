package com.jcwhatever.nucleus.views;

import static org.junit.Assert.assertEquals;

import com.jcwhatever.bukkit.v1_8_R1.BukkitTester;

import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;
import org.junit.Test;

import javax.annotation.Nullable;

/**
 * Test {@link View} using mock implementation {@link MockView}.
 *
 * <p>More tests are performed by the super class {@link AbstractViewTest}.</p>
 */
public class ViewTest extends AbstractViewTest {

    /**
     * Constructor.
     */
    public ViewTest() {
        super(new IViewGenerator() {
            @Override
            public View generate(Plugin plugin) {
                return new MockView(plugin);
            }
        });
    }

    /**
     * Make sure when a view is opened the {@code #openView} method
     * is invoked and the correct {@code ViewOpenReason} constant
     * is used.
     */
    @Test
    public void testOpenView() {

        MockView view1 = new MockView(plugin);
        MockView view2 = new MockView(plugin);

        _session.next(view1);
        BukkitTester.pause(5);

        assertEquals(ViewOpenReason.FIRST, view1.onOpenReason);

        _session.next(view2);
        BukkitTester.pause(5);

        assertEquals(ViewOpenReason.NEXT, view2.onOpenReason);

        _session.previous();
        BukkitTester.pause(5);

        assertEquals(ViewOpenReason.PREV, view1.onOpenReason);

        _session.refresh();
        BukkitTester.pause(5);

        assertEquals(ViewOpenReason.REFRESH, view1.onOpenReason);
    }

    /**
     * Make sure when a view is closed the {@code #onClosed} method
     * is invoked and the correct {@code ViewCloseReason} constant
     * is used.
     */
    @Test
    public void testOnClose() {

        MockView view1 = new MockView(plugin);
        MockView view2 = new MockView(plugin);

        _session.next(view1);
        BukkitTester.pause(5);

        _session.next(view2);
        BukkitTester.pause(5);

        assertEquals(ViewCloseReason.NEXT, view1.onCloseReason);

        _session.previous();
        BukkitTester.pause(5);

        assertEquals(ViewCloseReason.PREV, view2.onCloseReason);

        _session.next(view2);
        BukkitTester.pause(5);

        _session.escaped();
        BukkitTester.pause(5);

        assertEquals(ViewCloseReason.ESCAPE, view2.onCloseReason);

        _session.refresh();
        BukkitTester.pause(5);

        assertEquals(ViewCloseReason.REFRESH, view1.onCloseReason);
    }

    @Nullable
    @Override
    protected Block getSourceBlock() {
        return null;
    }
}
