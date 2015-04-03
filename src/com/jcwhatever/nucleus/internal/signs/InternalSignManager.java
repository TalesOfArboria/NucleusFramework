package com.jcwhatever.nucleus.internal.signs;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.events.signs.SignInteractEvent;
import com.jcwhatever.nucleus.internal.NucMsg;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.CollectionUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.Scheduler;
import com.jcwhatever.nucleus.utils.SignUtils;
import com.jcwhatever.nucleus.managed.signs.ISignContainer;
import com.jcwhatever.nucleus.managed.signs.ISignManager;
import com.jcwhatever.nucleus.managed.signs.SignHandler;
import com.jcwhatever.nucleus.managed.signs.SignHandler.SignBreakResult;
import com.jcwhatever.nucleus.managed.signs.SignHandler.SignChangeResult;
import com.jcwhatever.nucleus.managed.signs.SignHandler.SignClickResult;
import com.jcwhatever.nucleus.managed.signs.SignHandler.SignHandlerRegistration;
import com.jcwhatever.nucleus.utils.text.TextUtils;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

/**
 * Manages handled signs.
 */
public class InternalSignManager implements ISignManager {

    private static final Pattern PATTERN_HEADER_STRIPPER = Pattern.compile("[\\[\\]]");
    private static final SignHandlerRegistration REGISTRATION = new SignHandlerRegistration();

    /**
     * Get the data node name of a sign based on its location.
     *
     * @param signLocation  The location of the sign.
     */
    public static String getSignNodeName(Location signLocation) {
        PreCon.notNull(signLocation);

        String worldName = signLocation.getWorld() != null
                ? '_' + signLocation.getWorld().getName()
                : "";

        return "sign" +
                signLocation.getBlockX() + '_' +
                signLocation.getBlockY() + '_' +
                signLocation.getBlockZ() +
                worldName;
    }

    // keyed to sign name
    private final Map<String, SignHandler> _handlerMap = new HashMap<>(30);

    /**
     * Constructor.
     */
    public InternalSignManager() {

        Bukkit.getPluginManager().registerEvents(new BukkitListener(this), Nucleus.getPlugin());
    }

    @Override
    public boolean registerHandler(SignHandler signHandler) {
        PreCon.notNull(signHandler);

        SignHandler current = _handlerMap.get(signHandler.getSearchName());
        if (current != null) {
            NucMsg.warning(
                    "Failed to register sign handler. A sign named '{0}' is already " +
                            "registered by plugin '{1}'.",
                    current.getName(), current.getPlugin().getName());

            return false;
        }

        if (signHandler.getName() == null || signHandler.getName().isEmpty()) {
            throw new IllegalArgumentException("Failed to register sign handler because it has " +
                    "no name.");
        }

        if (!TextUtils.isValidName(signHandler.getName())) {
            throw new IllegalArgumentException("Failed to register sign handler because it has " +
                    "an invalid name: " + signHandler.getName());
        }

        if (signHandler.getPlugin() == null) {
            throw new IllegalArgumentException("Failed to register sign handler because it " +
                    "did not return an owning plugin.");
        }

        if (signHandler.getUsage() == null) {
            throw new IllegalArgumentException("Failed to register sign because it's usage " +
                    "returns null.");
        }

        if (signHandler.getUsage().length != 4) {
            throw new IllegalArgumentException("Failed to register sign because it's usage " +
                    "array has an incorrect number of elements.");
        }

        signHandler.onRegister(REGISTRATION);
        _handlerMap.put(signHandler.getSearchName(), signHandler);

        loadSigns(signHandler);
        return true;
    }

    @Override
    public boolean unregisterHandler(String name) {
        PreCon.notNull(name);

        SignHandler current = _handlerMap.remove(name.toLowerCase());
        if (current == null)
            return false;

        return true;
    }

    @Override
    public SignHandler getSignHandler(String name) {
        PreCon.notNull(name);

        return _handlerMap.get(name.toLowerCase());
    }

    @Override
    public Collection<SignHandler> getSignHandlers() {
        return Collections.unmodifiableCollection(_handlerMap.values());
    }

    @Override
    public List<ISignContainer> getSigns(String signHandlerName) {
        PreCon.notNullOrEmpty(signHandlerName);

        SignHandler handler = _handlerMap.get(signHandlerName.toLowerCase());
        if (handler == null)
            return CollectionUtils.unmodifiableList();

        IDataNode handlerNode = getHandlerNode(handler);

        List<ISignContainer> signs = new ArrayList<>(handlerNode.size());

        for (IDataNode signNode : handlerNode) {

            Location location = signNode.getLocation("location");
            if (location == null)
                continue;

            ISignContainer container = new InternalSignContainer(location, signNode);

            signs.add(container);
        }

        return Collections.unmodifiableList(signs);
    }
    @Override
    @Nullable
    public String[] getSavedLines(Sign sign) {
        PreCon.notNull(sign);

        String handlerName = getSignHandlerName(sign.getLine(0));
        SignHandler handler = _handlerMap.get(handlerName);
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

    @Override
    public boolean restoreSign (String signHandlerName, Location location) {
        PreCon.notNullOrEmpty(signHandlerName);
        PreCon.notNull(location);

        SignHandler handler = _handlerMap.get(signHandlerName.toLowerCase());
        if (handler == null) {
            NucMsg.warning(
                    "Failed to restore sign because a sign handler named '{0}' was not found for it.",
                    signHandlerName);
            return false;
        }

        String signName = getSignNodeName(location);
        IDataNode handlerNode = getHandlerNode(handler);
        if (!handlerNode.hasNode(signName))
            return false;

        IDataNode signNode = handlerNode.getNode(signName);

        final Location loc = signNode.getLocation("location");
        if (loc == null) {
            NucMsg.warning(handler.getPlugin(),
                    "Failed to restore sign because it's missing its location config property.");
            return false;
        }

        final Material type = signNode.getEnum("type", null, Material.class);
        if (type == null) {
            NucMsg.warning(handler.getPlugin(),
                    "Failed to restore sign because it's missing its type config property.");
            return false;
        }

        final BlockFace facing = signNode.getEnum("direction", null, BlockFace.class);
        if (facing == null) {
            NucMsg.warning(handler.getPlugin(),
                    "Failed to restore sign because it's missing its direction config property.");
            return false;
        }

        final String line0 = signNode.getString("line0");
        final String line1 = signNode.getString("line1");
        final String line2 = signNode.getString("line2");
        final String line3 = signNode.getString("line3");

        loc.getBlock().setType(type);

        Scheduler.runTaskLater(Nucleus.getPlugin(), new Runnable() {

            @Override
            public void run() {

                final BlockState blockState = loc.getBlock().getState();
                blockState.setType(type);
                blockState.setData(SignUtils.createData(type, facing));

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

    @Override
    public boolean restoreSigns (String signHandlerName) {
        PreCon.notNullOrEmpty(signHandlerName);

        SignHandler handler = _handlerMap.get(signHandlerName.toLowerCase());
        if (handler == null) {
            NucMsg.warning(
                    "Failed to restore signs because a sign handler named '{0}' was not found.",
                    signHandlerName);
            return false;
        }

        IDataNode handlerNode = getHandlerNode(handler);
        final LinkedList<SignInfo> signInfo = new LinkedList<>();

        for (IDataNode signNode : handlerNode) {

            Location loc = signNode.getLocation("location");
            if (loc == null) {
                NucMsg.warning(handler.getPlugin(),
                        "Failed to restore sign because it's missing its location config property.");
                continue;
            }

            Material type = signNode.getEnum("type", null, Material.class);
            if (type == null) {
                NucMsg.warning(handler.getPlugin(),
                        "Failed to restore sign because it's missing its type config property.");
                continue;
            }

            BlockFace facing = signNode.getEnum("direction", null, BlockFace.class);
            if (facing == null) {
                NucMsg.warning(handler.getPlugin(),
                        "Failed to restore sign because it's missing its direction config property.");
                continue;
            }

            String line0 = signNode.getString("line0");
            String line1 = signNode.getString("line1");
            String line2 = signNode.getString("line2");
            String line3 = signNode.getString("line3");

            BlockState blockState = loc.getBlock().getState();
            blockState.setType(type);
            blockState.setData(SignUtils.createData(type, facing));
            blockState.update(true);

            signInfo.push(new SignInfo(loc, line0, line1, line2, line3));
        }

        Scheduler.runTaskLater(Nucleus.getPlugin(), new Runnable() {

            @Override
            public void run() {

                while (!signInfo.isEmpty()) {
                    SignInfo s = signInfo.pop();

                    BlockState state = s.location.getBlock().getState();
                    Sign sign = (Sign) state;

                    SignUtils.setLines(sign, s.lines);
                    sign.update(true);
                }
            }

        });

        return true;
    }

    /**
     * Invoke when a sign is changed/created.
     *
     * @param sign   The changed sign.
     * @param event  The sign change event.
     *
     * @return True if a handler is found for the sign and the event was handled.
     */
    boolean signChange(Sign sign, SignChangeEvent event) {

        String signName = getSignHandlerName(event.getLine(0));
        SignHandler handler = _handlerMap.get(signName);
        if (handler == null)
            return false;

        event.setLine(0, ChatColor.translateAlternateColorCodes('&', event.getLine(0)));
        event.setLine(1, ChatColor.translateAlternateColorCodes('&', event.getLine(1)));
        event.setLine(2, ChatColor.translateAlternateColorCodes('&', event.getLine(2)));
        event.setLine(3, ChatColor.translateAlternateColorCodes('&', event.getLine(3)));

        IDataNode signNode = getSignNode(handler, sign.getLocation());

        SignChangeResult result = REGISTRATION.signChange(
                handler,
                event.getPlayer(),
                new InternalSignContainer(sign.getLocation(), signNode, event));

        if (result == null)
            throw new RuntimeException("A SignEventResult must be returned from SignHandler#onSignChange.");

        boolean isAllowed = result == SignChangeResult.VALID;

        String prefix = isAllowed ? handler.getHeaderPrefix() : "#" + ChatColor.RED;
        String header = prefix + handler.getDisplayName();

        event.setLine(0, header);

        if (isAllowed) {
            Location loc = sign.getLocation();
            signNode.set("location", loc);
            signNode.set("line0", header);
            signNode.set("line1", event.getLine(1));
            signNode.set("line2", event.getLine(2));
            signNode.set("line3", event.getLine(3));
            signNode.set("type", sign.getType().name());
            signNode.set("direction", SignUtils.getFacing(sign).name());
            signNode.save();
        }

        return true;
    }

    /**
     * Invoke when a sign is clicked.
     *
     * @param event  The sign interact event.
     *
     * @return  True if a handler is found for the sign.
     */
    boolean signClick(SignInteractEvent event) {

        String signName = getSignHandlerName(event.getSign().getLine(0));
        SignHandler handler = _handlerMap.get(signName);
        if (handler == null)
            return false;

        IDataNode signNode = getSignNode(handler, event.getSign().getLocation());

        SignClickResult result = REGISTRATION.signClick(
                handler,
                event.getPlayer(),
                new InternalSignContainer(event.getSign().getLocation(), signNode));

        if (result == null)
            throw new RuntimeException("A SignClickResult must be returned from SignHandler#onSignClick.");

        if (result == SignClickResult.HANDLED) {
            event.setUseItemInHand(Event.Result.DENY);
            event.setCancelled(true);
        }

        return true;
    }

    /**
     * Invoke when a sign is broken.
     *
     * @param sign   The sign that was broken.
     * @param event  The sign break event.
     *
     * @return  True if a handler is found for the sign.
     */
    boolean signBreak(Sign sign, BlockBreakEvent event) {

        String signName = getSignHandlerName(sign.getLine(0));
        SignHandler handler = _handlerMap.get(signName);
        if (handler == null)
            return false;

        IDataNode signNode = getSignNode(handler, sign.getLocation());

        SignBreakResult result = REGISTRATION.signBreak(
                handler,
                event.getPlayer(),
                new InternalSignContainer(sign.getLocation(), signNode));

        if (result == null)
            throw new RuntimeException("A SignEventResult must be returned from SignHandler#onSignBreak.");

        if (result == SignBreakResult.ALLOW) {
            signNode.remove();
            signNode.save();
        }
        else {
            event.setCancelled(true);
        }

        return true;
    }

    // load signs for the specified handler from
    // config and pass into handler.
    private void loadSigns(SignHandler handler) {

        IDataNode handlerNode = getHandlerNode(handler);

        for (IDataNode signNode : handlerNode) {

            Location location = signNode.getLocation("location");
            if (location == null)
                continue;

            Sign sign = SignUtils.getSign(location.getBlock());
            if (sign == null)
                continue;

            REGISTRATION.signLoad(handler, new InternalSignContainer(sign.getLocation(), signNode));
        }
    }

    // get a data node for a sign, assumes sign is
    // already validated to the specified handler
    private IDataNode getSignNode(SignHandler handler, Location signLocation) {

        IDataNode handlerNode = getHandlerNode(handler);
        return handlerNode.getNode(getSignNodeName(signLocation));
    }

    private IDataNode getHandlerNode(SignHandler handler) {
        IDataNode pluginNode = REGISTRATION.getDataNode(handler);
        return pluginNode.getNode(handler.getName());
    }

    // Get the sign handler name from the
    // first line of a sign.
    @Nullable
    private String getSignHandlerName(String line0) {

        String header = ChatColor.stripColor(line0);

        if (header.isEmpty())
            return null;

        Matcher stripMatcher = PATTERN_HEADER_STRIPPER.matcher(header);
        header = stripMatcher.replaceAll("").trim().toLowerCase();

        Matcher spaceMatcher = TextUtils.PATTERN_SPACE.matcher(header);
        return spaceMatcher.replaceAll("_");
    }

    static class SignInfo {

        final String[] lines;
        final Location location;

        SignInfo(Location location, String...lines) {
            this.location = location;
            this.lines = lines;
        }
    }
}
