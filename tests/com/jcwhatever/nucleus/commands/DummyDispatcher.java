package com.jcwhatever.nucleus.commands;

import com.jcwhatever.bukkit.MockPlugin;

/*
 * 
 */
public class DummyDispatcher extends CommandDispatcher {
    /**
     * Constructor.
     */
    public DummyDispatcher() {
        super(new MockPlugin("dummy"));
    }

    @Override
    protected void registerCommands() {
        registerCommand(DummyCommand.class);
    }
}
