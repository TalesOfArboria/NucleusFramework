package com.jcwhatever.nucleus.utils.observer.result;

import static org.junit.Assert.assertEquals;

import com.jcwhatever.nucleus.utils.observer.result.FutureResultAgent.Future;
import com.jcwhatever.nucleus.utils.observer.update.IUpdateAgent;
import com.jcwhatever.nucleus.utils.observer.update.UpdateSubscriber;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class FutureResultAgentTest {

    @Test
    public void basicTest() {

        FutureResultAgent<String> agent = new FutureResultAgent<>();


        final List<String> updates = new ArrayList<>(10);

        Future<String> future = agent.getFuture();
        future.onResult(new UpdateSubscriber<Result<String>>() {
            @Override
            public void onUpdate(IUpdateAgent agent, Result<String> argument) {
                updates.add("RESULT");
            }
        })
        .onSuccess(new UpdateSubscriber<Result<String>>() {
            @Override
            public void onUpdate(IUpdateAgent agent, Result<String> argument) {
                updates.add("SUCCESS");
            }
        })
        .onCancel(new UpdateSubscriber<Result<String>>() {
            @Override
            public void onUpdate(IUpdateAgent agent, Result<String> argument) {
                updates.add("CANCEL");
            }
        })
        .onError(new UpdateSubscriber<Result<String>>() {
            @Override
            public void onUpdate(IUpdateAgent agent, Result<String> argument) {
                updates.add("ERROR");
            }
        })
        ;


        agent.sendResult(new Result<String>(1.0D, 1.0D, "test"));

        assertEquals(2, updates.size());
        assertEquals("SUCCESS", updates.get(0));
        assertEquals("RESULT", updates.get(1));

        updates.clear();
        agent.sendResult(new Result<String>(1.0D, 0.0D, "test"));

        assertEquals(2, updates.size());
        assertEquals("ERROR", updates.get(0));
        assertEquals("RESULT", updates.get(1));

        updates.clear();
        agent.sendResult(new Result<String>(1.0D, -1.0D, "test"));

        assertEquals(2, updates.size());
        assertEquals("CANCEL", updates.get(0));
        assertEquals("RESULT", updates.get(1));

        updates.clear();
        agent.sendResult(new Result<String>(0.0D, 0.0D, "test"));

        assertEquals(1, updates.size());
        assertEquals("RESULT", updates.get(0));
    }

}