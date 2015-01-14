package com.jcwhatever.nucleus.utils.observer.event;

import static org.junit.Assert.assertEquals;

import com.jcwhatever.nucleus.utils.observer.SubscriberTest;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class EventSubscriberTest {

    @Test
    public void basicTest() throws Exception {

        final List<String> callResults = new ArrayList<>(10);

        EventSubscriber<String> subscriber = new EventSubscriber<String>() {

            @Override
            public void onEvent(@Nullable Object caller, String event) {

                assertEquals(EventSubscriberTest.this, caller);

                callResults.add(event);
            }
        };

        SubscriberTest subscriberTest = new SubscriberTest(subscriber);
        subscriberTest.test();

        EventAgent agent = new EventAgent() {};

        assertEquals(0, subscriber.getAgents().size());

        subscriber.register(agent);

        // ensure agent is added to subscriber
        assertEquals(1, subscriber.getAgents().size());

        // ensure subscriber added itself to agent
        assertEquals(1, agent.getSubscribers().size());

        // test event call
        agent.call(this, "testEvent");

        // ensure the event was called on the subscriber
        assertEquals(1, callResults.size());
        assertEquals("testEvent", callResults.get(0));

        callResults.clear();

        // test unregister
        subscriber.unregister(agent);

        // ensure the agent was removed from the subscriber
        assertEquals(0, subscriber.getAgents().size());

        // ensure subscriber removed itself from the agent
        assertEquals(0, agent.getSubscribers().size());

        // test event call
        agent.call(this, "testEvent");

        // ensure the event was NOT called on the subscriber
        assertEquals(0, callResults.size());
    }
}