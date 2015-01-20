package com.jcwhatever.nucleus.events.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.jcwhatever.bukkit.MockPlugin;
import com.jcwhatever.nucleus.NucleusTest;
import com.jcwhatever.nucleus.mixins.ICancellable;
import com.jcwhatever.nucleus.utils.observer.event.EventSubscriber;
import com.jcwhatever.nucleus.utils.observer.event.EventSubscriberPriority;

import org.bukkit.plugin.Plugin;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class EventManagerTest {

    Plugin plugin = new MockPlugin("dummy");

    List<String> callTracker = new ArrayList<>(3);

    @Test
    public void test() {

        NucleusTest.init();

        EventManager manager = new EventManager(plugin, null);

        manager.register(plugin, TestEvent.class, new TestEventSubscriber<TestEvent>(false, false));

        manager.call(this, new TestEvent());

        assertEquals(1, callTracker.size());

        callTracker.clear();

        TestEventSubscriber<TestCancellableEvent> sub1 = new TestEventSubscriber<>(true, false);
        sub1.setPriority(EventSubscriberPriority.FIRST);

        TestEventSubscriber<TestCancellableEvent> sub2 = new TestEventSubscriber<>(true, false);
        sub2.setPriority(EventSubscriberPriority.HIGH);

        TestEventSubscriber<TestCancellableEvent> sub3 = new TestEventSubscriber<>(true, false);
        sub3.setPriority(EventSubscriberPriority.NORMAL);

        TestEventSubscriber<TestCancellableEvent> sub4 = new TestEventSubscriber<>(true, false);
        sub4.setPriority(EventSubscriberPriority.LOW);

        TestEventSubscriber<TestCancellableEvent> sub5 = new TestEventSubscriber<>(true, false);
        sub5.setPriority(EventSubscriberPriority.LAST);

        TestEventSubscriber<TestCancellableEvent> sub6 = new TestEventSubscriber<>(true, false);
        sub6.setPriority(EventSubscriberPriority.WATCHER);

        manager.register(plugin, TestCancellableEvent.class, sub1);
        manager.register(plugin, TestCancellableEvent.class, sub2);
        manager.register(plugin, TestCancellableEvent.class, sub3);
        manager.register(plugin, TestCancellableEvent.class, sub4);
        manager.register(plugin, TestCancellableEvent.class, sub5);
        manager.register(plugin, TestCancellableEvent.class, sub6);

        manager.call(this, new TestCancellableEvent());

        assertEquals(6, callTracker.size());
        assertEquals("FIRST", callTracker.get(0));
        assertEquals("HIGH", callTracker.get(1));
        assertEquals("NORMAL", callTracker.get(2));
        assertEquals("LOW", callTracker.get(3));
        assertEquals("LAST", callTracker.get(4));
        assertEquals("WATCHER", callTracker.get(5));

    }

    public class TestEventSubscriber<E> extends EventSubscriber<E>{

        boolean isCancellable;
        boolean cancel;

        TestEventSubscriber(boolean isCancellable, boolean cancel) {

            this.isCancellable = isCancellable;
            this.cancel = cancel;
        }

        @Override
        public void onEvent(@Nullable Object caller, E event) {

            assertEquals(EventManagerTest.this, caller);
            assertNotNull(event);

            if (isCancellable) {
                assertTrue(event instanceof TestCancellableEvent);

                if (cancel)
                    ((ICancellable)event).setCancelled(true);
            }

            callTracker.add(getPriority().name());
        }
    }

    public static class TestEvent {

    }

    public static class TestCancellableEvent implements ICancellable {

        private boolean isCancelled;

        @Override
        public boolean isCancelled() {
            return isCancelled;
        }

        @Override
        public void setCancelled(boolean isCancelled) {
            this.isCancelled = isCancelled;
        }
    }

}