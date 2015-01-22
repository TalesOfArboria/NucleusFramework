package com.jcwhatever.nucleus.collections.observer.agent;

import com.jcwhatever.nucleus.collections.java.MapRunnable;
import com.jcwhatever.nucleus.utils.observer.ISubscriberAgent;
import com.jcwhatever.nucleus.utils.observer.SubscriberAgent;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class AgentHashMapTest {

    @Test
    public void test() {

        AgentMap<String, ISubscriberAgent> map = new AgentHashMap<String, ISubscriberAgent>() {

            Map<String, ISubscriberAgent> internalMap = new HashMap<>(10);

            @Override
            protected Map<String, ISubscriberAgent> map() {
                return internalMap;
            }
        };

        ISubscriberAgent agent1 = new SubscriberAgent() {};
        ISubscriberAgent agent2 = new SubscriberAgent() {};
        ISubscriberAgent agent3 = new SubscriberAgent() {};

        MapRunnable<ISubscriberAgent> test = new MapRunnable<>(map, agent1, agent2, agent3);

        test.run();
    }

}