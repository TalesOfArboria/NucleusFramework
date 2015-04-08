package com.jcwhatever.nucleus.utils.observer.event;

import static org.junit.Assert.assertEquals;

import com.jcwhatever.nucleus.mixins.ICancellable;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;

public class EventAgentTest {

    @Test
    public void basicTest() {

        final List<String> callResults = new ArrayList<>(10);

        EventAgent agent = new EventAgent();

        EventSubscriber<String> subscriber = getSubscriber(callResults);

        assertEquals(0, agent.getSubscribers().size());

        agent.addSubscriber(subscriber);

        // ensure subscriber is added to agent
        assertEquals(1, agent.getSubscribers().size());

        // ensure agent added itself to subscriber
        assertEquals(1, subscriber.getAgents().size());

        // test event call
        agent.call(this, "testEvent");

        // ensure the event was called on the subscriber
        assertEquals(1, callResults.size());

        callResults.clear();

        // test unregister
        agent.removeSubscriber(subscriber);

        // ensure subscriber is removed from agent
        assertEquals(0, agent.getSubscribers().size());

        // ensure the agent removed itself from the subscriber
        assertEquals(0, subscriber.getAgents().size());

        // test event call
        agent.call(this, "testEvent");

        // ensure the event was NOT called on the subscriber
        assertEquals(0, callResults.size());
    }


    @Test
    public void testEventOrder() {

        final List<String> callResults = new ArrayList<>(10);

        EventAgent agent = new EventAgent() {};

        EventSubscriber<String> first = getSubscriber(callResults)
                .setPriority(EventSubscriberPriority.FIRST);

        EventSubscriber<String> high = getSubscriber(callResults)
                .setPriority(EventSubscriberPriority.HIGH);

        EventSubscriber<String> normal = getSubscriber(callResults)
                .setPriority(EventSubscriberPriority.NORMAL);

        EventSubscriber<String> low = getSubscriber(callResults)
                .setPriority(EventSubscriberPriority.LOW);

        EventSubscriber<String> last = getSubscriber(callResults)
                .setPriority(EventSubscriberPriority.LAST);

        EventSubscriber<String> watcher = getSubscriber(callResults)
                .setPriority(EventSubscriberPriority.WATCHER);


        // add events out of order to ensure they are sorted to the correct order
        agent.addSubscriber(normal);
        agent.addSubscriber(high);
        agent.addSubscriber(low);
        agent.addSubscriber(watcher);
        agent.addSubscriber(last);
        agent.addSubscriber(first);

        agent.call(this, "testEvent");

        assertEquals(6, callResults.size());
        assertEquals("FIRST", callResults.get(0));
        assertEquals("HIGH", callResults.get(1));
        assertEquals("NORMAL", callResults.get(2));
        assertEquals("LOW", callResults.get(3));
        assertEquals("LAST", callResults.get(4));
        assertEquals("WATCHER", callResults.get(5));
    }

    @Test
    public void testEventCancel() {

        final List<String> callResults = new ArrayList<>(10);

        EventAgent agent = new EventAgent() {};

        EventSubscriber<CancelEvent> first = getCancellingSubscriber(callResults)
                .setPriority(EventSubscriberPriority.FIRST);

        EventSubscriber<CancelEvent> last = getSubscriber(callResults)
                .setPriority(EventSubscriberPriority.HIGH);

        EventSubscriber<CancelEvent> watcher = getSubscriber(callResults)
                .setPriority(EventSubscriberPriority.WATCHER);


        agent.addSubscriber(first);
        agent.addSubscriber(last);
        agent.addSubscriber(watcher);

        CancelEvent event = new CancelEvent();
        assertEquals(false, event.isCancelled());

        agent.call(this, event);

        // first should have cancelled event, high is not run, watcher is always run.
        assertEquals(2, callResults.size());

        // reset
        callResults.clear();
        event.setCancelled(false);
        assertEquals(false, event.isCancelled());

        last.setCancelIgnored(true);

        agent.call(this, event);

        // first should have cancelled event, high ignores cancel, watcher is always run.
        assertEquals(3, callResults.size());

    }


    @Test
    public void testEventUncancel() {

        final List<String> callResults = new ArrayList<>(10);

        EventAgent agent = new EventAgent() {};

        EventSubscriber<CancelEvent> first = getCancellingSubscriber(callResults)
                .setPriority(EventSubscriberPriority.FIRST);

        EventSubscriber<CancelEvent> normal = getSubscriber(callResults)
                .setPriority(EventSubscriberPriority.NORMAL);

        EventSubscriber<CancelEvent> last = getUncancellingSubscriber(callResults);
                last.setCancelIgnored(true)
                .setPriority(EventSubscriberPriority.HIGH);

        EventSubscriber<CancelEvent> watcher = getSubscriber(callResults)
                .setPriority(EventSubscriberPriority.WATCHER);

        agent.addSubscriber(first);
        agent.addSubscriber(normal);
        agent.addSubscriber(last);
        agent.addSubscriber(watcher);

        CancelEvent event = new CancelEvent();
        assertEquals(false, event.isCancelled());

        agent.call(this, event);

        // 1. first should have cancelled event,
        // 2. normal was skipped,
        // 3. last ignored cancelled and uncancelled it.
        // 4. normal was run
        // 5. watcher is always run
        assertEquals(4, callResults.size());
    }




    private static class CancelEvent implements ICancellable {

        boolean isCancelled;

        @Override
        public boolean isCancelled() {
            return isCancelled;
        }

        @Override
        public void setCancelled(boolean isCancelled) {
            this.isCancelled = isCancelled;
        }
    }

    private <T> EventSubscriber<T> getSubscriber(final Collection<String> results) {
        return new EventSubscriber<T>() {

            @Override
            public void onEvent(@Nullable Object caller, T event) {

                assertEquals(EventAgentTest.this, caller);

                results.add(getPriority().name());
            }
        };
    }


    private EventSubscriber<CancelEvent> getCancellingSubscriber(final Collection<String> results) {
        return new EventSubscriber<CancelEvent>() {

            @Override
            public void onEvent(@Nullable Object caller, CancelEvent event) {

                assertEquals(EventAgentTest.this, caller);

                results.add(getPriority().name());

                event.setCancelled(true);
            }
        };
    }

    private <T extends ICancellable> EventSubscriber<T> getUncancellingSubscriber(final Collection<String> results) {
        return new EventSubscriber<T>() {

            @Override
            public void onEvent(@Nullable Object caller, T event) {

                assertEquals(EventAgentTest.this, caller);

                results.add(getPriority().name());

                event.setCancelled(false);
            }
        };
    }

}