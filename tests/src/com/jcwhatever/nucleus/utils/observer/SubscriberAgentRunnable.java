package com.jcwhatever.nucleus.utils.observer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SubscriberAgentRunnable implements Runnable {

    SubscriberAgent _agent;

    public SubscriberAgentRunnable() {}

    public SubscriberAgentRunnable(SubscriberAgent agent) {
        _agent = agent;
    }

    @Override
    public void run() {

        if (_agent == null)
            _agent = new SubscriberAgent() {};

        assertNotNull(_agent.getSubscribers());
        assertEquals(0, _agent.getSubscribers().size());


        Subscriber subscriber = new Subscriber() {};

        _agent.addSubscriber(subscriber);

        assertEquals(1, _agent.getSubscribers().size());
        assertEquals(1, subscriber.getAgents().size());

        _agent.removeSubscriber(subscriber);

        assertEquals(0, _agent.getSubscribers().size());
        assertEquals(0, subscriber.getAgents().size());

        _agent.registerReference(subscriber);

        assertEquals(1, _agent.getSubscribers().size());
        assertEquals(0, subscriber.getAgents().size());

        _agent.unregisterReference(subscriber);

        assertEquals(0, _agent.getSubscribers().size());
        assertEquals(0, subscriber.getAgents().size());
    }
}