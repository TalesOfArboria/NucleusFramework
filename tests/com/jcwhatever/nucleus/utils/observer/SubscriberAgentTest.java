package com.jcwhatever.nucleus.utils.observer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class SubscriberAgentTest {

    SubscriberAgent _agent;

    public SubscriberAgentTest() {}

    public SubscriberAgentTest(SubscriberAgent agent) {
        _agent = agent;
    }

    @Test
    public void test() throws Exception {

        if (_agent == null)
            _agent = new SubscriberAgent() {};

        assertNotNull(_agent.getSubscribers());
        assertEquals(0, _agent.getSubscribers().size());


        Subscriber subscriber = new Subscriber() {};

        _agent.register(subscriber);

        assertEquals(1, _agent.getSubscribers().size());
        assertEquals(1, subscriber.getAgents().size());

        _agent.unregister(subscriber);

        assertEquals(0, _agent.getSubscribers().size());
        assertEquals(0, subscriber.getAgents().size());

        _agent.addSubscriber(subscriber);

        assertEquals(1, _agent.getSubscribers().size());
        assertEquals(0, subscriber.getAgents().size());

        _agent.removeSubscriber(subscriber);

        assertEquals(0, _agent.getSubscribers().size());
        assertEquals(0, subscriber.getAgents().size());
    }
}