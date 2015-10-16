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

package com.jcwhatever.nucleus.internal.managed.resourcepacks;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.collections.players.PlayerMap;
import com.jcwhatever.nucleus.events.respacks.MissingRequiredResourcePackEvent;
import com.jcwhatever.nucleus.events.respacks.MissingRequiredResourcePackEvent.Action;
import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.internal.NucMsg;
import com.jcwhatever.nucleus.managed.entity.meta.EntityMeta;
import com.jcwhatever.nucleus.managed.entity.meta.IEntityMetaContext;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.managed.resourcepacks.IPlayerResourcePacks;
import com.jcwhatever.nucleus.managed.resourcepacks.IResourcePack;
import com.jcwhatever.nucleus.managed.resourcepacks.IResourcePackManager;
import com.jcwhatever.nucleus.managed.resourcepacks.ResourcePackStatus;
import com.jcwhatever.nucleus.managed.scheduler.Scheduler;
import com.jcwhatever.nucleus.managed.teleport.Teleporter;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.file.FileUtils;
import com.jcwhatever.nucleus.utils.managers.NamedInsensitiveDataManager;
import com.jcwhatever.nucleus.utils.observer.future.FutureResultSubscriber;
import com.jcwhatever.nucleus.utils.observer.future.Result;
import com.jcwhatever.nucleus.utils.validate.IValidator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

import javax.annotation.Nullable;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Internal implementation of {@link IResourcePackManager}.
 */
public class InternalResourcePackManager extends NamedInsensitiveDataManager<IResourcePack>
        implements IResourcePackManager, Listener {

    @Localizable static final String _RESOURCE_PACK_REQUIRED =
            "{RED}Server resource pack required to enter {0: world name}.";

    private static final Pattern PATTERN_UNESCAPE = Pattern.compile("\\:", Pattern.LITERAL);
    private static final IEntityMetaContext META = EntityMeta.getContext(Nucleus.getPlugin());
    private static final String HANDLING_REQUIRED_META_NAME =
            InternalResourcePackManager.class.getName() + ":HandleRequired";

    private final Map<UUID, PlayerResourcePacks> _playerMap =
            new PlayerMap<PlayerResourcePacks>(Nucleus.getPlugin(), 35);

    private ResourcePack _defaultPack;
    private IDataNode _worldNodes;

    /**
     * Constructor.
     */
    public InternalResourcePackManager(IDataNode dataNode) {
        super(dataNode.getNode("packs"), true);

        _worldNodes = dataNode.getNode("worlds");

        loadDefault();

        Bukkit.getPluginManager().registerEvents(this, Nucleus.getPlugin());
    }

    @Override
    @Nullable
    public IResourcePack getDefault() {
        return _defaultPack;
    }

    @Nullable
    @Override
    public IResourcePack getWorld(World world) {
        PreCon.notNull(world);

        if (!_worldNodes.hasNode(world.getName()))
            return null;

        IDataNode node = _worldNodes.getNode(world.getName());

        String packName = node.getString("pack");
        if (packName == null)
            return null;

        return get(packName);
    }

    @Override
    public void setWorld(World world, @Nullable IResourcePack pack) {
        PreCon.notNull(world);

        IDataNode node = _worldNodes.getNode(world.getName());

        node.set("pack", pack == null ? null : pack.getName());
        node.save();

        IResourcePack current = getWorld(world);
        List<Player> players = world.getPlayers();

        for (Player player : players) {

            IPlayerResourcePacks packs = get(player);
            if (pack != null) {
                packs.next(pack, current);
            }
            else {
                packs.remove(current);
            }
        }
    }

    @Override
    public boolean isRequired(World world) {
        PreCon.notNull(world);

        if (!_worldNodes.hasNode(world.getName()))
            return false;

        IDataNode node = _worldNodes.getNode(world.getName());
        return node.getBoolean("required");
    }

    @Override
    public void setRequired(World world, boolean isRequired) {
        PreCon.notNull(world);

        IDataNode node = _worldNodes.getNode(world.getName());
        node.set("required", isRequired);
        node.save();
    }

    @Override
    public IResourcePack add(String name, String url) {
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(url);

        if (contains(name)) {
            return get(name);
        }

        ResourcePack pack = new ResourcePack(name, url, this);
        add(pack);

        return pack;
    }

    @Override
    @Nullable
    public IResourcePack get(String name) {
        PreCon.notNull(name);

        if (name.isEmpty())
            return _defaultPack;

        return super.get(name);
    }

    @Override
    public PlayerResourcePacks get(Player player) {
        PreCon.notNull(player);

        PlayerResourcePacks packs = _playerMap.get(player.getUniqueId());
        if (packs == null) {
            packs = new PlayerResourcePacks(player, getDefault());
            _playerMap.put(player.getUniqueId(), packs);
        }

        return packs;
    }

    @Override
    public boolean remove(String name) {
        PreCon.notNull(name);

        return !name.equalsIgnoreCase("_default") && super.remove(name);
    }

    @Nullable
    @Override
    protected IResourcePack load(String name, IDataNode itemNode) {
        return new ResourcePack(name, itemNode.getString("url"), this);
    }

    @Override
    protected void save(IResourcePack item, IDataNode itemNode) {
        itemNode.set("url", item.getUrl());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onJoin(PlayerJoinEvent event) {

        final Player player = event.getPlayer();
        final World world = player.getWorld();

        final IResourcePack pack = getWorld(world);
        if (pack == null)
            return;

        pack.apply(player);

        if (!isRequired(world) || META.has(player, HANDLING_REQUIRED_META_NAME))
            return;

        META.set(player, HANDLING_REQUIRED_META_NAME, this);

        PlayerResourcePacks packs = get(player);
        packs.getFinalStatus().onResult(new FutureResultSubscriber<IPlayerResourcePacks>() {
            @Override
            public void on(Result<IPlayerResourcePacks> result) {
                IPlayerResourcePacks packs = result.getResult();
                if (packs != null && packs.getStatus() == ResourcePackStatus.SUCCESS)
                    return;

                Scheduler.runTaskSync(Nucleus.getPlugin(), new Runnable() {
                    @Override
                    public void run() {

                        CharSequence message = NucLang.get(_RESOURCE_PACK_REQUIRED, world.getName()).toString();
                        MissingRequiredResourcePackEvent event = new MissingRequiredResourcePackEvent(
                                player, world, pack, new Location(null, 0, 0, 0), Action.KICK, message);
                        Nucleus.getEventManager().callBukkit(this, event);
                        if (event.isCancelled())
                            return;

                        handleRequiredEvent(event);
                    }
                });
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onChangeWorld(PlayerChangedWorldEvent event) {

        final Player player = event.getPlayer();
        final World world = player.getWorld();

        IPlayerResourcePacks playerPacks = get(player);

        final IResourcePack newPack = getWorld(player.getWorld());

        IResourcePack fromPack = getWorld(event.getFrom());
        if (fromPack != null) {
            playerPacks.remove(fromPack);
        }

        if (newPack != null) {
            newPack.apply(player);

            if (isRequired(world) && !META.has(player, HANDLING_REQUIRED_META_NAME)) {

                META.set(player, HANDLING_REQUIRED_META_NAME, this);

                PlayerResourcePacks packs = get(player);
                final World prevWorld = event.getFrom();
                packs.getFinalStatus().onResult(new FutureResultSubscriber<IPlayerResourcePacks>() {
                    @Override
                    public void on(Result<IPlayerResourcePacks> result) {
                        IPlayerResourcePacks packs = result.getResult();
                        if (packs != null && packs.getStatus() == ResourcePackStatus.SUCCESS)
                            return;

                        CharSequence message = NucLang.get(_RESOURCE_PACK_REQUIRED, world.getName());
                        MissingRequiredResourcePackEvent event = new MissingRequiredResourcePackEvent(
                                player, world, newPack, prevWorld.getSpawnLocation(), Action.RELOCATE, message);

                        Nucleus.getEventManager().callBukkit(this, event);
                        if (event.isCancelled())
                            return;

                        handleRequiredEvent(event);
                    }
                });
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onResourcePackStatus(final PlayerResourcePackStatusEvent event) {
        Scheduler.runTaskSync(Nucleus.getPlugin(), new Runnable() {
            @Override
            public void run() {
                handleStatusEvent(event.getPlayer(), event.getStatus());
            }
        });
    }

    private void handleStatusEvent(Player player, PlayerResourcePackStatusEvent.Status bukkitStatus) {
        PlayerResourcePacks packs = get(player);
        if (packs == null)
            return;

        final ResourcePackStatus status = ResourcePackStatus.fromBukkit(bukkitStatus);
        ResourcePackStatus current = packs.getStatus();

        if ((status == ResourcePackStatus.ACCEPTED
                || status == ResourcePackStatus.DECLINED)
                && current != ResourcePackStatus.PENDING
                && current != ResourcePackStatus.NO_RESOURCE) {
            return;
        }

        if ((status == ResourcePackStatus.FAILED
                || status == ResourcePackStatus.SUCCESS)
                && (current != ResourcePackStatus.ACCEPTED
                && current != ResourcePackStatus.DECLINED)) {
            return;
        }

        if (status == ResourcePackStatus.SUCCESS
                && current != ResourcePackStatus.ACCEPTED) {
            return;
        }

        packs.setStatus(status);
    }

    private void handleRequiredEvent(MissingRequiredResourcePackEvent event) {

        Player player = event.getPlayer();
        CharSequence message = event.getMessage();

        switch (event.getAction()) {
            case RELOCATE:
                Teleporter.teleport(player, event.getRelocation());
                // fall through
            case IGNORE:
                if (message != null) {
                    NucMsg.tell(player, message);
                }
                break;
            case KICK:
                if (message == null) {
                    message = NucLang.get(_RESOURCE_PACK_REQUIRED, event.getWorld().getName());
                }
                player.kickPlayer(message.toString());
                break;
        }

        META.set(player, HANDLING_REQUIRED_META_NAME, null);
    }

    private void loadDefault() {

        if (Nucleus.getPlugin().isTesting()) {
            _defaultPack = new ResourcePack("_default", "http://respack.test.com", this);
            _map.put("_default", _defaultPack);
            return;
        }

        File file = new File("server.properties");

        String resourcePack = FileUtils.scanTextFile(file, StandardCharsets.UTF_8, new IValidator<String>() {
            @Override
            public boolean isValid(String element) {
                return element.startsWith("resource-pack=");
            }
        });

        if (resourcePack == null || resourcePack.isEmpty())
            return;

        resourcePack = PATTERN_UNESCAPE.matcher(
                resourcePack.substring("resourcePack=".length() + 1)).replaceAll(":").trim();

        _defaultPack = new ResourcePack("_default", resourcePack, this);
        _map.put("_default", _defaultPack);
    }
}
