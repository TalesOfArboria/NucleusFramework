package com.jcwhatever.nucleus.collections.observer.agent;

import static org.junit.Assert.assertEquals;

import com.jcwhatever.nucleus.collections.java.MapTest;
import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.utils.observer.ISubscriber;
import com.jcwhatever.nucleus.utils.observer.ISubscriberAgent;
import com.jcwhatever.nucleus.utils.observer.SubscriberAgent;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AgentMapTest {

    @Test
    public void testMap() {

        AgentMap<String, ISubscriberAgent> map = new AgentMap<String, ISubscriberAgent>() {

            Map<String, ISubscriberAgent> internalMap = new HashMap<>(10);

            @Override
            protected Map<String, ISubscriberAgent> map() {
                return internalMap;
            }
        };

        DisposableAgent agent1 = new DisposableAgent() {};
        DisposableAgent agent2 = new DisposableAgent() {};
        DisposableAgent agent3 = new DisposableAgent() {};

        new MapTest<>(map, agent1, agent2, agent3).run();

        map.clear();

        map.put("test", agent1);

        assertEquals(1, map.size());

        agent1.dispose();

        assertEquals(0, map.size());
    }

    private static class DisposableAgent extends SubscriberAgent implements IDisposable {

        @Override
        public boolean isDisposed() {
            return false;
        }

        @Override
        public void dispose() {

            Set<ISubscriber> subscribers = getSubscribers();

            for (ISubscriber subscriber : subscribers) {
                subscriber.unregister(this);
            }
        }
    }

}