package com.jcwhatever.nucleus.utils.observer.result;

import static org.junit.Assert.assertEquals;

import com.jcwhatever.nucleus.utils.observer.SubscriberAgentTest;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ResultAgentTest {


    @Test
    public void basicTest() throws Exception {

        ResultAgent<String> agent = new ResultAgent<>();

        SubscriberAgentTest test = new SubscriberAgentTest(agent);
        test.test();


        final List<String> resultList = new ArrayList<>(5);


        ResultSubscriber<String> subscriber = new ResultSubscriber<String>() {

            @Override
            public void onResult(Result<String> result) {
                resultList.add("RESULT");
            }

            @Override
            public void onSuccess(Result<String> result) {
                resultList.add("SUCCESS");
            }

            @Override
            public void onCancel(Result<String> result) {
                resultList.add("CANCEL");
            }

            @Override
            public void onError(Result<String> result) {
                resultList.add("ERROR");
            }

        };

        agent.register(subscriber);

        agent.sendResult(new Result<String>(1.0D, 1.0D, "success"));

        // 1 RESULT, 1 SUCCESS
        assertEquals(2, resultList.size());
        assertEquals("SUCCESS", resultList.get(0));
        assertEquals("RESULT", resultList.get(1));


        resultList.clear();
        agent.sendResult(new Result<String>(0.5D, 0.0D, "result/incomplete"));
        assertEquals(1, resultList.size());
        assertEquals("RESULT", resultList.get(0));


        resultList.clear();
        agent.sendResult(new Result<String>(1.0D, 0.5D, "completed/failed"));
        assertEquals(2, resultList.size());
        assertEquals("ERROR", resultList.get(0));
        assertEquals("RESULT", resultList.get(1));


        resultList.clear();
        agent.sendResult(new Result<String>(1.0D, 1.0D, "completed/success"));
        assertEquals(2, resultList.size());
        assertEquals("SUCCESS", resultList.get(0));
        assertEquals("RESULT", resultList.get(1));

        resultList.clear();
        agent.sendResult(new Result<String>(1.0D, -1.0D, "completed/cancelled"));
        assertEquals(2, resultList.size());
        assertEquals("CANCEL", resultList.get(0));
        assertEquals("RESULT", resultList.get(1));
    }

}