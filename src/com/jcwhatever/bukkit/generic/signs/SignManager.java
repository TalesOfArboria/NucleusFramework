/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.generic.signs;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.collections.TimedMap;
import com.jcwhatever.bukkit.generic.events.bukkit.SignInteractEvent;
import com.jcwhatever.bukkit.generic.messaging.Messenger;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.Scheduler;
import com.jcwhatever.bukkit.generic.utils.SignUtils;
import com.jcwhatever.bukkit.generic.utils.TextUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

/**
 * Manages handled signs.
 */
public class SignManager {

    private static final Pattern PATTERN_HEADER_STRIPPER = Pattern.compile("[\\[\\]]");

    // keyed to sign name
    private static final Map<String, SignHandler> _signHandlerMap = new HashMap<>(30);
    private static final Map<SignManager, Void> _managers = new WeakHashMap<>(30);
    private static BukkitSignEventListener _listener;

    /**
     * Get all {@code SignManager} instances.
     */
    public static List<SignManager> getManagers() {
        return new ArrayList<>(_managers.keySet());
    }

    /**
     * Get the data node name of a sign based on its location.
     *
     * @param signLocation  The location of the sign.
     */
    public static String getSignNodeName(Location signLocation) {
        PreCon.notNull(signLocation);

        return "sign" + signLocation.getBlockX() + signLocation.getBlockY() + signLocation.getBlockZ();
    }

    private final Plugin _plugin;
    private final IDataNode _dataNode;
    private final Map<String, SignHandler> _localHandlerMap = new HashMap<>(10);
    private final Map<Location, IDataNode> _signNodes = new TimedMap<>(30, 20 * 60);

    /**
     * Constructor.
     *
     * @param plugin    The owning plugin.
     * @param dataNode  The config data node.
     */
    public SignManager(Plugin plugin, IDataNode dataNode) {
        PreCon.notNull(plugin);
        PreCon.notNull(dataNode);

        _plugin = plugin;
        _dataNode = dataNode;

        _managers.put(this, null);

        if (_listener == null) {
            _listener = new BukkitSignEventListener();
            Bukkit.getPluginManager().registerEvents(_listener, GenericsLib.getLib());
        }
    }

    /**
     * Get the owning plugin.
     */
    public Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Get the managers data node.
     */
    public IDataNode getDataNode() {
        return _dataNode;
    }

    /**
     * Register a sign handler.
     *
     * @param signHandler  The sign handler to register.
     *
     * @return  True if registered, false if a sign handler with the same name is already registered or there is
     * a problem with the sign handler.
     */
    public boolean registerSignType(SignHandler signHandler) {
        SignHandler current = _signHandlerMap.get(signHandler.getSearchName());
        if (current != null) {
            Messenger.warning(_plugin,
                    "Failed to register sign handler. A sign named '{0}' is already registered by plugin '{1}'.",
                    current.getName(), current.getPlugin().getName());

            return false;
        }

        if (signHandler.getName() == null || signHandler.getName().isEmpty()) {
            throw new RuntimeException("Failed to register sign handler because it has no name.");
        }

        if (!TextUtils.isValidName(signHandler.getName())) {
            throw new RuntimeException("Failed to register sign handler because it has an invalid name: " + signHandler.getName());
        }

        if (signHandler.getPlugin() == null) {
            throw new RuntimeException("Failed to register sign handler because it did not return an owning plugin.");
        }

        if (!signHandler.getPlugin().equals(_plugin)) {
            throw new RuntimeException("Failed to register sign handler because its owning plugin is not the same as the sign manager plugin.");
        }

        if (signHandler.getUsage() == null) {
            throw new RuntimeException("Failed to register sign because it's usage returns null.");
        }

        if (signHandler.getUsage().length != 4) {
            throw new RuntimeException("Failed to register sign because it's usage array has an incorrect number of elements.");
        }

        _signHandlerMap.put(signHandler.getSearchName(), signHandler);
        _localHandlerMap.put(signHandler.getSearchName(), signHandler);
        return true;
    }

    /**
     * Get a sign handler by name.
     *
     * @param name  The name.
     */
    public SignHandler getSignHandler(String name) {
        return _localHandlerMap.get(name.toLowerCase());
    }

    /**
     * Get all sign handlers.
     */
    public List<SignHandler> getSignHandlers() {
        return new ArrayList<>(_localHandlerMap.values());
    }

    /**
     * Get all signs saved to the config handled by the specified
     * sign handler.
     *
     * @param signHandlerName  The name of the sign handler.
     *
     * @return Null if failed to find handler.
     */
    @Nullable
    public List<SignContainer> getSigns(String signHandlerName) {
        PreCon.notNullOrEmpty(signHandlerName);

        SignHandler handler = _localHandlerMap.get(signHandlerName.toLowerCase());
        if (handler == null)
            return null;

        IDataNode handlerNode = _dataNode.getNode(handler.getName());

        Set<String> signNames = handlerNode.getSubNodeNames();

        List<SignContainer> signs = new ArrayList<>(signNames.size());

        for (String signName : signNames) {

            IDataNode signNode = handlerNode.getNode(signName);

            Location location = signNode.getLocation("location");
            if (location == null)
                continue;

            SignContainer container = new SignContainer(_plugin, location, signNode);

            signs.add(container);
        }

        return signs;
    }

    /**
     * Get the text lines for a handled sign from the config.
     *
     * @param sign  The sign to check.
     *
     * @return  Null if no handler or sign is found in config.
     */
    @Nullable
    public String[] getSavedLines(Sign sign) {
        PreCon.notNull(sign);

        String handlerName = getSignHandlerName(sign.getLine(0));
        SignHandler handler = _localHandlerMap.get(handlerName);
        if (handler == null)
            return null;

        IDataNode signNode = getSignNode(handler, sign.getLocation());
        return new String[] {
                signNode.getString("line0", ""),
                signNode.getString("line1", ""),
                signNode.getString("line2", ""),
                signNode.getString("line3", "")
        };
    }

    /**
     * Restore a sign from the specified sign handler at the specified location
     * using config settings.
     *
     * @param signHandlerName  The name of the sign handler.
     * @param location         The location of the sign.
     *
     * @return  True if completed.
     */
    public boolean restoreSign (String signHandlerName, Location location) {
        PreCon.notNullOrEmpty(signHandlerName);
        PreCon.notNull(location);

        SignHandler handler = _localHandlerMap.get(signHandlerName.toLowerCase());
        if (handler == null) {
            Messenger.warning(_plugin,
                    "Failed to restore sign because a sign handler named '{0}' was not found for it.",
                    signHandlerName);
            return false;
        }

        String signName = getSignNodeName(location);
        IDataNode handlerNode = _dataNode.getNode(handler.getName());
        if (!handlerNode.hasNode(signName))
            return false;

        IDataNode signNode = handlerNode.getNode(signName);

        final Location loc = signNode.getLocation("location");
        if (loc == null) {
            Messenger.warning(_plugin, "Failed to restore sign because it's missing its location config property.");
            return false;
        }

        final Material type = signNode.getEnum("type", null, Material.class);
        if (type == null) {
            Messenger.warning(_plugin, "Failed to restore sign because it's missing its type config property.");
            return false;
        }

        final BlockFace facing = signNode.getEnum("direction", null, BlockFace.class);
        if (facing == null) {
            Messenger.warning(_plugin, "Failed to restore sign because it's missing its direction config property.");
            return false;
        }

        final String line0 = signNode.getString("line0");
        final String line1 = signNode.getString("line1");
        final String line2 = signNode.getString("line2");
        final String line3 = signNode.getString("line3");

        loc.getBlock().setType(type);

        Scheduler.runTaskLater(_plugin, 1, new Runnable() {

            @Override
            public void run() {

                final BlockState blockState = loc.getBlock().getState();
                blockState.setType(type);
                blockState.setData(SignUtils.createSignData(type, facing));

                Sign sign = (Sign) blockState;

                sign.setLine(0, line0);
                sign.setLine(1, line1);
                sign.setLine(2, line2);
                sign.setLine(3, line3);
                sign.update(true);
            }
        });

        return true;
    }

    /**
     * Restore signs from the specified sign handler using config settings.
     *
     * @param signHandlerName  The name of the sign handler.
     *
     * @return  True if restore completed.
     */
    public boolean restoreSigns (String signHandlerName) {
        PreCon.notNullOrEmpty(signHandlerName);

        SignHandler handler = _localHandlerMap.get(signHandlerName.toLowerCase());
        if (handler == null) {
            Messenger.warning(_plugin,
                    "Failed to restore signs because a sign handler named '{0}' was not found.",
                    signHandlerName);
            return false;
        }

        IDataNode handlerNode = _dataNode.getNode(handler.getName());
        Set<String> signNames = handlerNode.getSubNodeNames();
        final Stack<SignInfo> signInfo = new Stack<SignInfo>();

        for (String signName : signNames) {

            IDataNode signNode = handlerNode.getNode(signName);

            Location loc = signNode.getLocation("location");
            if (loc == null) {
                Messenger.warning(_plugin, "Failed to restore sign because it's missing its location config property.");
                continue;
            }

            Material type = signNode.getEnum("type", null, Material.class);
            if (type == null) {
                Messenger.warning(_plugin, "Failed to restore sign because it's missing its type config property.");
                continue;
            }

            BlockFace facing = signNode.getEnum("direction", null, BlockFace.class);
            if (facing == null) {
                Messenger.warning(_plugin, "Failed to restore sign because it's missing its direction config property.");
                continue;
            }

            String line0 = signNode.getString("line0");
            String line1 = signNode.getString("line1");
            String line2 = signNode.getString("line2");
            String line3 = signNode.getString("line3");

            BlockState blockState = loc.getBlock().getState();
            blockState.setType(type);
            blockState.setData(SignUtils.createSignData(type, facing));
            blockState.update(true);

            signInfo.push(new SignInfo(loc, line0, line1, line2, line3));
        }

        Scheduler.runTaskLater(GenericsLib.getLib(), new Runnable() {

            @Override
            public void run() {

                while (!signInfo.isEmpty()) {
                    SignInfo s = signInfo.pop();

                    BlockState state = s.getLocation().getBlock().getState();
                    Sign sign = (Sign) state;

                    SignUtils.setLines(sign, s.getLines());
                    sign.update(true);
                }
            }

        });

        return true;
    }


    /**
     * Call when a sign is changed/created.
     *
     * @param sign   The changed sign.
     * @param event  The sign change event.
     *
     * @return True if a handler is found for the sign.
     */
    boolean signChange(Sign sign, SignChangeEvent event) {

        String signName = getSignHandlerName(event.getLine(0));
        SignHandler handler = _localHandlerMap.get(signName);
        if (handler == null)
            return false;

        event.setLine(0, ChatColor.translateAlternateColorCodes('&', event.getLine(0)));
        event.setLine(1, ChatColor.translateAlternateColorCodes('&', event.getLine(1)));
        event.setLine(2, ChatColor.translateAlternateColorCodes('&', event.getLine(2)));
        event.setLine(3, ChatColor.translateAlternateColorCodes('&', event.getLine(3)));

        IDataNode signNode = _dataNode.getNode(handler.getName() + '.' + getSignNodeName(sign.getLocation()));

        boolean isValid = handler.signChange(event.getPlayer(), new SignContainer(_plugin, sign.getLocation(), signNode, event));

        String prefix = isValid ? handler.getHeaderPrefix() : "#" + ChatColor.RED;
        String header = prefix + handler.getDisplayName();

        event.setLine(0, header);

        if (isValid) {
            Location loc = sign.getLocation();
            signNode.set("location", loc);
            signNode.set("line0", header);
            signNode.set("line1", event.getLine(1));
            signNode.set("line2", event.getLine(2));
            signNode.set("line3", event.getLine(3));
            signNode.set("type", sign.getType().name());
            signNode.set("direction", SignUtils.getSignFacing(sign).name());
            signNode.saveAsync(null);
        }

        return true;
    }

    /**
     * Call when a sign is clicked.
     *
     * @param event  The sign interact event.
     *
     * @return  True if a handler is found for the sign.
     */
    boolean signClick(SignInteractEvent event) {

        String signName = getSignHandlerName(event.getSign().getLine(0));
        SignHandler handler = _localHandlerMap.get(signName);
        if (handler == null)
            return false;

        IDataNode signNode = getSignNode(handler, event.getSign().getLocation());

        boolean isValidClick = handler.signClick(event.getPlayer(), new SignContainer(_plugin, event.getSign().getLocation(), signNode));

        if (isValidClick) {
            event.setUseItemInHand(Event.Result.DENY);
            event.setIsCancelled(true);
        }

        return true;
    }

    /**
     * Call when a sign is broken.
     *
     * @param sign   The sign that was broken.
     * @param event  The sign break event.
     *
     * @return  True if a handler is found for the sign.
     */
    boolean signBreak(Sign sign, BlockBreakEvent event) {

        String signName = getSignHandlerName(sign.getLine(0));
        SignHandler handler = _localHandlerMap.get(signName);
        if (handler == null)
            return false;

        IDataNode signNode = getSignNode(handler, sign.getLocation());

        boolean allowBreak = handler.signBreak(event.getPlayer(), new SignContainer(_plugin, sign.getLocation(), signNode));

        if (allowBreak) {
            signNode.remove();
            signNode.saveAsync(null);
        }
        else {
            event.setCancelled(true);
        }

        return true;
    }


    // load signs for the specified handler from
    // config and pass into handler.
    private void loadSigns(SignHandler handler) {

        IDataNode handlerNode = _dataNode.getNode(handler.getName());

        Set<String> signNames = handlerNode.getSubNodeNames();

        for (String signName : signNames) {

            IDataNode signNode = handlerNode.getNode(signName);

            Location location = signNode.getLocation("location");
            if (location == null)
                continue;

            Sign sign = SignUtils.getSign(location.getBlock());
            if (sign == null)
                continue;

            handler.onSignLoad(new SignContainer(_plugin, sign.getLocation(), signNode));
        }
    }


    // get a data node for a sign, assumes sign is
    // already validated to the specified handler
    private IDataNode getSignNode(SignHandler handler, Location signLocation) {

        IDataNode dataNode = _signNodes.get(signLocation);
        if (dataNode != null)
            return dataNode;

        dataNode = _dataNode.getNode(handler.getName() + '.' + getSignNodeName(signLocation));
        _signNodes.put(signLocation, dataNode);

        return dataNode;
    }

    // Get the sign handler name from the
    // first line of a sign.
    private String getSignHandlerName(String line0) {

        String header = ChatColor.stripColor(line0);

        if (header.isEmpty())
            return null;

        Matcher stripMatcher = PATTERN_HEADER_STRIPPER.matcher(header);
        header = stripMatcher.replaceAll("").trim().toLowerCase();

        Matcher spaceMatcher = TextUtils.PATTERN_SPACE.matcher(header);
        return spaceMatcher.replaceAll("_");
    }

}
