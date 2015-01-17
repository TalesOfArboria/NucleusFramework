package com.jcwhatever.nucleus.utils.observer.update;

import com.jcwhatever.nucleus.utils.observer.SubscriberRunnable;

import org.junit.Test;

public class UpdateSubscriberTest {


    @Test
    public void basicTest() throws Exception {

        UpdateSubscriber<String> subscriber = new UpdateSubscriber<String>() {
            @Override
            public void on(String argument) {

            }
        };

        SubscriberRunnable subscriberTest = new SubscriberRunnable(subscriber);
        subscriberTest.run();
    }

}