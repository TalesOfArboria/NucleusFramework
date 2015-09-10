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
import com.jcwhatever.nucleus.managed.resourcepacks.IPlayerResourcePacks;
import com.jcwhatever.nucleus.managed.resourcepacks.IResourcePack;
import com.jcwhatever.nucleus.managed.resourcepacks.IResourcePackManager;
import com.jcwhatever.nucleus.managed.resourcepacks.ResourcePackStatus;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.file.FileUtils;
import com.jcwhatever.nucleus.utils.managers.NamedInsensitiveDataManager;
import com.jcwhatever.nucleus.utils.validate.IValidator;
import org.bukkit.Bukkit;
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

    private static final Pattern PATTERN_UNESCAPE = Pattern.compile("\\:", Pattern.LITERAL);

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

        String packName = _worldNodes.getString(world.getName());
        if (packName == null)
            return null;

        return get(packName);
    }

    @Override
    public void setWorld(World world, @Nullable IResourcePack pack) {
        PreCon.notNull(world);

        _worldNodes.set(world.getName(), pack == null ? null : pack.getName());
        _worldNodes.save();

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
    public PlayerResourcePacks get(Player player) {
        PreCon.notNull(player);

        PlayerResourcePacks packs = _playerMap.get(player.getUniqueId());
        if (packs == null) {
            packs = new PlayerResourcePacks(player, getDefault());
            _playerMap.put(player.getUniqueId(), packs);
        }

        return packs;
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

        IResourcePack pack = getWorld(event.getPlayer().getWorld());
        if (pack == null)
            return;

        pack.apply(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onChangeWorld(PlayerChangedWorldEvent event) {

        IPlayerResourcePacks playerPacks = get(event.getPlayer());

        IResourcePack newPack = getWorld(event.getPlayer().getWorld());

        IResourcePack fromPack = getWorld(event.getFrom());
        if (fromPack != null) {
            playerPacks.remove(fromPack);
        }

        if (newPack != null)
            newPack.apply(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onResourcePackStatus(PlayerResourcePackStatusEvent event) {

        PlayerResourcePacks packs = get(event.getPlayer());
        if (packs == null)
            return;

        ResourcePackStatus status = ResourcePackStatus.fromBukkit(event.getStatus());
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

    private void loadDefault() {

        if (Nucleus.getPlugin().isTesting())
            return;

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
    }
}
