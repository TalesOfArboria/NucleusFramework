package com.jcwhatever.nucleus.commands;

import com.jcwhatever.nucleus.commands.arguments._ArgumentsTestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/*
 * 
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        _ArgumentsTestSuite.class,
        UsageGeneratorTest.class
})
public class _CommandsTestSuite {
}
