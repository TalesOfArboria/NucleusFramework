/*
 * This file is part of NucleusFramework for Bukkit, licensed under the MIT License (MIT).
 *
 * Copyright (c) JCThePants (www.jcwhatever.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.jcwhatever.nucleus.internal.managed.commands;

import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.ICommand;
import com.jcwhatever.nucleus.managed.commands.ICommandOwner;
import com.jcwhatever.nucleus.managed.commands.IRegisteredCommand;
import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * A collection of commands.
 */
class CommandCollection implements ICommandOwner, IPluginOwned, Iterable<IRegisteredCommand> {

    // keyed to command name
    private final Plugin _plugin;
    private final Map<String, CommandContainer> _commandMap;
    private final Map<Class<? extends ICommand>, IRegisteredCommand> _classMap;
    private final ICommandContainerFactory _factory;

    private List<IRegisteredCommand> _sortedCommands;

    public interface ICommandContainerFactory {
        CommandContainer create(Plugin plugin, ICommand command);
    }

    /**
     * Constructor.
     */
    public CommandCollection(Plugin plugin, ICommandContainerFactory factory) {
        PreCon.notNull(plugin);
        PreCon.notNull(factory);

        _plugin = plugin;
        _factory = factory;
        _commandMap = new HashMap<>(20);
        _classMap = new HashMap<>(20);
    }

    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Get the number of commands.
     */
    public int size() {
        return _classMap.size();
    }

    /**
     * Determine if the collection is empty.
     */
    public boolean isEmpty() {
        return _classMap.isEmpty();
    }

    /**
     * Determine if the collection has a command that can be called by the
     * specified name.
     *
     * @param name  The command name.
     */
    public boolean has(String name) {
        PreCon.notNull(name);

        return _commandMap.containsKey(name);
    }

    /**
     * Determine if the collection has a command of the specified type class.
     *
     * @param commandClass  The command class.
     */
    public boolean has(Class<? extends ICommand> commandClass) {
        PreCon.notNull(commandClass);

        return _classMap.containsKey(commandClass);
    }

    @Override
    @Nullable
    public CommandContainer getCommand(String name) {
        PreCon.notNull(name);

        return _commandMap.get(name);
    }

    /**
     * Get a command from the collection that is of the specified type class.
     *
     * @param commandClass  The command class.
     *
     * @return  Null if not found.
     */
    @Nullable
    public CommandContainer getCommand(Class<? extends ICommand> commandClass) {
        PreCon.notNull(commandClass);

        return (CommandContainer)_classMap.get(commandClass);
    }

    /**
     * Get a command using the first argument of a string array as the commands
     * call name.
     *
     * @param commands  The string array.
     *
     * @return  Null if the command was not found.
     */
    @Nullable
    public CommandContainer fromFirst(String[] commands) {
        PreCon.notNull(commands);

        if (commands.length == 0)
            return null;

        return this.getCommand(commands[0]);
    }

    /**
     * Get all the call names used by the commands in the collection.
     */
    @Override
    public List<String> getCommandNames() {
        return new ArrayList<>(_commandMap.keySet());
    }

    /**
     * Add a command to the collection.
     *
     * @param commandClass  The command class.
     *
     * @return  The primary call name of the command or null if the command could not be added.
     */
    @Nullable
    public String addCommand(Class<? extends ICommand> commandClass) {

        // instantiate command
        ICommand instance;

        try {
            Constructor<? extends ICommand> constructor = commandClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            instance = constructor.newInstance();
        }
        catch (InstantiationException | IllegalAccessException |
                NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }

        return addCommand(_factory.create(_plugin, instance));
    }

    /**
     * Add a command to the collection.
     *
     * @param container  The command to add.
     *
     * @return  The primary call name of the command or null if the command could not be added.
     */
    @Nullable
    public String addCommand(CommandContainer container) {
        PreCon.notNull(container);

        if (container.getDispatcher() != null) {
            throw new RuntimeException("The command is already initialized and cannot be added.");
        }

        // make sure command has required command info annotation
        CommandInfo info = container.getCommand().getClass().getAnnotation(CommandInfo.class);
        if (info == null) {
            throw new RuntimeException(
                    "Could not find required CommandInfo annotation for command class: " +
                            container.getCommand().getClass().getName());
        }

        String[] commandNames = info.command();
        String primaryName = null;

        for (String commandName : commandNames) {

            if (_commandMap.containsKey(commandName))
                continue;

            if (primaryName == null) {
                primaryName = commandName;
                _classMap.put(container.getCommand().getClass(), container);

                // clear sorted commands cache
                _sortedCommands = null;
            }

            _commandMap.put(commandName, container);
        }

        return primaryName;
    }

    /**
     * Remove a command call name from the collection.
     *
     * <p>The command is removed if the call name is the only
     * call name it uses.</p>
     *
     * @param name  The call name to remove.
     *
     * @return  True if found and removed.
     */
    public boolean remove(String name) {
        PreCon.notNull(name);

        CommandContainer command = _commandMap.remove(name.toLowerCase());
        if (command == null)
            return false;

        Set<CommandContainer> commands = new HashSet<>(_commandMap.values());
        if (!commands.contains(command)) {
            _classMap.remove(command.getCommand().getClass());
        }
        return true;
    }

    /**
     * Remove the command that can be called by the specified name and all other
     * call names used by it.
     *
     * <p>Effectively removes the command.</p>
     *
     * @param name  The call name.
     *
     * @return  True if found and removed.
     */
    public boolean removeAll(String name) {
        PreCon.notNull(name);

        CommandContainer command = _commandMap.remove(name);
        if (command == null)
            return false;

        removeAll(command);
        return true;
    }

    /**
     * Remove a command from the collection.
     *
     * @param container  The command to remove.
     *
     * @return  True if found and removed.
     */
    public boolean removeAll(CommandContainer container) {
        PreCon.notNull(container);

        Iterator<Entry<String, CommandContainer>> iterator = _commandMap.entrySet().iterator();

        boolean hasChanged = false;

        while (iterator.hasNext()) {
            Entry<String, CommandContainer> entry = iterator.next();

            if (entry.getValue().equals(container)) {
                iterator.remove();
                hasChanged = true;
            }
        }

        _classMap.remove(container.getCommand().getClass());
        return hasChanged;
    }

    /**
     * Remove a command from the collection.
     *
     * @param commandClass  The command class.
     *
     * @return  True if the command was found and removed.
     */
    public boolean removeAll(Class<? extends ICommand> commandClass) {
        PreCon.notNull(commandClass);

        Iterator<Entry<String, CommandContainer>> iterator = _commandMap.entrySet().iterator();

        boolean hasChanged = false;

        while (iterator.hasNext()) {
            Entry<String, CommandContainer> entry = iterator.next();

            Class<? extends ICommand> clazz = entry.getValue().getCommand().getClass();

            if (clazz.equals(commandClass)) {

                if (!hasChanged)
                    _classMap.remove(clazz);

                iterator.remove();
                hasChanged = true;
            }
        }

        return hasChanged;
    }

    @Override
    public List<IRegisteredCommand> getCommands() {
        if (_sortedCommands == null) {

            _sortedCommands = new ArrayList<>(_classMap.values());

            Collections.sort(_sortedCommands);
        }

        return new ArrayList<>(_sortedCommands);
    }

    @Override
    public boolean registerCommand(Class<? extends ICommand> commandClass) {
        return addCommand(commandClass) != null;
    }

    @Override
    public boolean unregisterCommand(Class<? extends ICommand> commandClass) {
        return removeAll(commandClass);
    }

    @Override
    public Iterator<IRegisteredCommand> iterator() {
        return new Iterator<IRegisteredCommand>() {

            Iterator<IRegisteredCommand> iter = new ArrayList<>(_classMap.values()).iterator();
            CommandContainer current;

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public CommandContainer next() {
                return current = (CommandContainer)iter.next();
            }

            @Override
            public void remove() {
                iter.remove();
                CommandCollection.this.removeAll(current);
            }
        };
    }

    public Object[] toArray() {
        return _classMap.values().toArray();
    }

    public <T> T[] toArray(T[] a) {
        //noinspection SuspiciousToArrayCall
        return _classMap.values().toArray(a);
    }
}
