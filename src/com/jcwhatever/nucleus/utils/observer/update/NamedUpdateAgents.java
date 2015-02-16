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

package com.jcwhatever.nucleus.utils.observer.update;

import com.jcwhatever.nucleus.collections.observer.agent.AgentHashMap;
import com.jcwhatever.nucleus.collections.observer.agent.AgentMap;
import com.jcwhatever.nucleus.utils.PreCon;

import java.util.ArrayList;
import java.util.List;

/**
 * Used for types that need to keep multiple update agents for multiple
 * update contexts.
 *
 * <p>{@link NamedUpdateAgents} uses a lazy initialization approach to
 * prevent using resources if they are not needed. Calling the {@link #getAgent}
 * method always returns a result. If the agent is not present, a new one is created.</p>
 *
 * <p>Use {@link #hasAgent} to determine if the agent has been created yet.</p>
 *
 */
public class NamedUpdateAgents {

    volatile AgentMap<String, UpdateAgent<?>> _agents;
    private volatile String _recentName;
    private volatile UpdateAgent<?> _recent;

    /**
     * Get the number of instantiated agents.
     */
    public int size() {
        if (_agents == null)
            return 0;

        return _agents.size();
    }

    /**
     * Determine if there are any initialized agents.
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Determine if the tracker contains an agent
     * by a certain name.
     *
     * @param name The name of the agent.
     */
    public boolean hasAgent(String name) {
        PreCon.notNull(name);

        if (_agents == null)
            return false;

        if (name.equals(_recentName)) {

            if (_recent.isDisposed()) {
                _recentName = null;
                _recent = null;
            }
            else {
                return true;
            }
        }

        UpdateAgent<?> agent = _agents.get(name);
        if (agent != null) {
            _recentName = name;
            _recent = agent;
        }

        return agent != null;
    }


    /**
     * Get an existing agent or create a new one.
     *
     * @param name  The name of the agent.
     *
     * @param <T>  The agent value type.
     */
    public <T> UpdateAgent<T> getAgent(String name) {
        PreCon.notNull(name);

        if (name.equals(_recentName)) {

            if (_recent.isDisposed()) {
                _recentName = null;
                _recent = null;
            }
            else {
                @SuppressWarnings("unchecked")
                UpdateAgent<T> recent = (UpdateAgent<T>) _recent;
                return recent;
            }
        }

        UpdateAgent<T> agent = null;

        if (_agents == null) {
            _agents = new AgentHashMap<>(5);
        }
        else {
            @SuppressWarnings("unchecked")
            UpdateAgent<T> get = (UpdateAgent<T>)_agents.get(name);
            agent = get;
        }

        if (agent == null) {
            agent = new UpdateAgent<>(3);
            _agents.set(name, agent);
        }

        return agent;
    }

    /**
     * Update an agents subscribers.
     *
     * @param name      The name of the agent.
     * @param argument  The update argument.
     *
     * @param <T>  The argument type.
     */
    public <T> void update(String name, T argument) {
        PreCon.notNull(name);

        if (!hasAgent(name))
            return;

        @SuppressWarnings("unchecked")
        UpdateAgent<T> agent = (UpdateAgent<T>)_recent;

        agent.update(argument);
    }

    /**
     * Dispose all agents.
     *
     * <p>The {@link NamedUpdateAgents} instance can still be used.</p>
     */
    public void disposeAgents() {
        if (_agents == null)
            return;

        List<UpdateAgent<?>> agents;

        //noinspection SynchronizeOnNonFinalField
        synchronized (_agents) {
            agents = new ArrayList<>(_agents.values());
        }

        for (UpdateAgent<?> agent : agents) {
            agent.dispose();
        }

        _recentName = null;
        _recent = null;
        _agents.dispose();
        _agents = null;
    }
}
