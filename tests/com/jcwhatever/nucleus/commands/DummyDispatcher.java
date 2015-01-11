package com.jcwhatever.nucleus.commands;

import com.jcwhatever.dummy.DummyPlugin;

/*
 * 
 */
public class DummyDispatcher extends CommandDispatcher {
    /**
     * Constructor.
     */
    public DummyDispatcher() {
        super(new DummyPlugin("dummy"));
    }

    @Override
    protected void registerCommands() {
        registerCommand(DummyCommand.class);
    }
}
