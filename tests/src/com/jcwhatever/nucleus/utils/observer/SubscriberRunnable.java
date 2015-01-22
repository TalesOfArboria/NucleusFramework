package com.jcwhatever.nucleus.utils.observer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SubscriberRunnable implements Runnable {

    private Subscriber _subscriber;

    public SubscriberRunnable() {}

    public SubscriberRunnable(Subscriber subscriber) {
        _subscriber = subscriber;
    }

    @Override
    public void run() {

        if (_subscriber == null)
            _subscriber =  new Subscriber() {};

        assertNotNull(_subscriber.getAgents());
        assertEquals(0, _subscriber.getAgents().size());

        SubscriberAgent agent = new SubscriberAgent() {};

        _subscriber.register(agent);

        assertEquals(1, agent.getSubscribers().size());
        assertEquals(1, _subscriber.getAgents().size());

        _subscriber.unregister(agent);

        assertEquals(0, agent.getSubscribers().size());
        assertEquals(0, _subscriber.getAgents().size());

        _subscriber.addAgent(agent);

        assertEquals(0, agent.getSubscribers().size());
        assertEquals(1, _subscriber.getAgents().size());

        _subscriber.removeAgent(agent);

        assertEquals(0, agent.getSubscribers().size());
        assertEquals(0, _subscriber.getAgents().size());
    }
}