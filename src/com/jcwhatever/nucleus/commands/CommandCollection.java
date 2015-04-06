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

package com.jcwhatever.nucleus.commands;

import com.jcwhatever.nucleus.utils.PreCon;

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
public class CommandCollection implements ICommandOwner, Iterable<AbstractCommand> {

    // keyed to command name
    private final Map<String, AbstractCommand> _commandMap;
    private final Map<Class<? extends AbstractCommand>, AbstractCommand> _classMap;

    private List<AbstractCommand> _sortedCommands;

    /**
     * Constructor.
     */
    public CommandCollection() {
        _commandMap = new HashMap<String, AbstractCommand>(20);
        _classMap = new HashMap<Class<? extends AbstractCommand>, AbstractCommand>(20);
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
    public boolean has(Class<? extends AbstractCommand> commandClass) {
        PreCon.notNull(commandClass);

        return _classMap.containsKey(commandClass);
    }

    /**
     * Get a command from the collection that can be called by the specified name.
     *
     * @param name  The name of the command.
     *
     * @return  Null if not found.
     */
    @Override
    @Nullable
    public AbstractCommand getCommand(String name) {
        PreCon.notNull(name);

        return _commandMap.get(name);
    }

    /**
     * Get a command from the collection that is of the specified type class.
     *
     * @param commandClass  The command class.
     *
     * @param <T>  The class type.
     *
     * @return  Null if not found.
     */
    @Nullable
    public <T extends AbstractCommand> T getCommand(Class<T> commandClass) {
        PreCon.notNull(commandClass);

        @SuppressWarnings("unchecked")
        T result = (T)_classMap.get(commandClass);

        return result;
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
    public AbstractCommand fromFirst(String[] commands) {
        PreCon.notNull(commands);

        if (commands.length == 0)
            return null;

        return getCommand(commands[0]);
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
    public String addCommand(Class<? extends AbstractCommand> commandClass) {

        // instantiate command
        AbstractCommand instance;

        try {
            Constructor<? extends AbstractCommand> constructor = commandClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            instance = constructor.newInstance();
        }
        catch (InstantiationException | IllegalAccessException |
                NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }

        return addCommand(instance);
    }

    /**
     * Add a command to the collection.
     *
     * @param command  The command to add.
     *
     * @return  The primary call name of the command or null if the command could not be added.
     */
    @Nullable
    public String addCommand(AbstractCommand command) {
        PreCon.notNull(command);

        if (command.getDispatcher() != null) {
            throw new RuntimeException("The command is already initialized and cannot be added.");
        }

        // make sure command has required command info annotation
        CommandInfo info = command.getClass().getAnnotation(CommandInfo.class);
        if (info == null) {
            throw new RuntimeException(
                    "Could not find required CommandInfo annotation for command class: " +
                            command.getClass().getName());
        }

        String[] commandNames = info.command();
        String primaryName = null;

        for (String commandName : commandNames) {

            if (_commandMap.containsKey(commandName))
                continue;

            if (primaryName == null) {
                primaryName = commandName;
                _classMap.put(command.getClass(), command);

                // clear sorted commands cache
                _sortedCommands = null;
            }

            _commandMap.put(commandName, command);
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

        AbstractCommand command = _commandMap.remove(name.toLowerCase());
        if (command == null)
            return false;

        Set<AbstractCommand> commands = new HashSet<>(_commandMap.values());
        if (!commands.contains(command)) {
            _classMap.remove(command.getClass());
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

        AbstractCommand command = _commandMap.remove(name);
        if (command == null)
            return false;

        removeAll(command);
        return true;
    }

    /**
     * Remove a command from the collection.
     *
     * @param command  The command to remove.
     *
     * @return  True if found and removed.
     */
    public boolean removeAll(AbstractCommand command) {
        PreCon.notNull(command);

        Iterator<Entry<String, AbstractCommand>> iterator = _commandMap.entrySet().iterator();

        boolean hasChanged = false;

        while (iterator.hasNext()) {
            Entry<String, AbstractCommand> entry = iterator.next();

            if (entry.getValue().equals(command)) {
                iterator.remove();
                hasChanged = true;
            }
        }

        _classMap.remove(command.getClass());
        return hasChanged;
    }

    /**
     * Remove a command from the collection.
     *
     * @param commandClass  The command class.
     *
     * @return  True if the command was found and removed.
     */
    public boolean removeAll(Class<? extends  AbstractCommand> commandClass) {
        PreCon.notNull(commandClass);

        Iterator<Entry<String, AbstractCommand>> iterator = _commandMap.entrySet().iterator();

        boolean hasChanged = false;

        while (iterator.hasNext()) {
            Entry<String, AbstractCommand> entry = iterator.next();

            if (entry.getValue().getClass().equals(commandClass)) {

                if (!hasChanged)
                    _classMap.remove(entry.getValue().getClass());

                iterator.remove();
                hasChanged = true;
            }
        }

        return hasChanged;
    }

    /**
     * Returns a new array list of the commands in the collection.
     */
    @Override
    public List<AbstractCommand> getCommands() {
        if (_sortedCommands == null) {

            _sortedCommands = new ArrayList<>(_classMap.values());

            Collections.sort(_sortedCommands);
        }
        return new ArrayList<>(_sortedCommands);
    }

    @Override
    public boolean registerCommand(Class<? extends AbstractCommand> commandClass) {
        return addCommand(commandClass) != null;
    }

    @Override
    public boolean unregisterCommand(Class<? extends AbstractCommand> commandClass) {
        return removeAll(commandClass);
    }

    @Override
    public Iterator<AbstractCommand> iterator() {
        return new Iterator<AbstractCommand>() {

            Iterator<AbstractCommand> iter = new ArrayList<>(_classMap.values()).iterator();
            AbstractCommand current;

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public AbstractCommand next() {
                return current = iter.next();
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
