package com.jcwhatever.nucleus.utils.observer.update;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class UpdateAgentTest {


    @Test
    public void basicTest() {

        final List<String> updateResults = new ArrayList<>(10);

        final UpdateAgent<String> agent = new UpdateAgent<>();

        UpdateSubscriber<String> subscriber = new UpdateSubscriber<String>() {
            @Override
            public void onUpdate(IUpdateAgent sourceAgent, String argument) {

                assertEquals(agent, sourceAgent);

                updateResults.add(argument);
            }
        };

        assertEquals(0, agent.getSubscribers().size());

        agent.register(subscriber);

        // ensure subscriber is added to agent
        assertEquals(1, agent.getSubscribers().size());

        // ensure agent added itself to subscriber
        assertEquals(1, subscriber.getAgents().size());

        // test update
        agent.update("testUpdate");

        // ensure the event was called on the subscriber
        assertEquals(1, updateResults.size());

        updateResults.clear();

        // test unregister
        agent.unregister(subscriber);

        // ensure subscriber is removed from agent
        assertEquals(0, agent.getSubscribers().size());

        // ensure the agent removed itself from the subscriber
        assertEquals(0, subscriber.getAgents().size());

        // test update
        agent.update("testUpdate");

        // ensure the event was NOT called on the subscriber
        assertEquals(0, updateResults.size());
    }


}