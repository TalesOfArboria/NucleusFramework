package com.jcwhatever.nucleus.utils.observer.update;

import com.jcwhatever.nucleus.utils.observer.SubscriberTest;

import org.junit.Test;

public class UpdateSubscriberTest {


    @Test
    public void basicTest() throws Exception {

        UpdateSubscriber<String> subscriber = new UpdateSubscriber<String>() {
            @Override
            public void onUpdate(IUpdateAgent agent, String argument) {

            }
        };

        SubscriberTest subscriberTest = new SubscriberTest(subscriber);
        subscriberTest.test();
    }

}