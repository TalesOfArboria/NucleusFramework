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

package com.jcwhatever.nucleus.utils.nms;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.NucleusPlugin;
import com.jcwhatever.nucleus.internal.NucMsg;
import com.jcwhatever.nucleus.internal.managed.nms.InternalNmsManager;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

/**
 * NMS utilities.
 */
public final class NmsUtils {

    private NmsUtils() {}

    static Pattern PATTERN_VERSION = Pattern.compile(".*\\.(v\\d+_\\d+_\\w*\\d+)(\\.[\\.a-zA-Z0-9_]*)?");

    private static String _version; // package version

    static {
        loadPackageVersion();
    }

    /**
     * Get the detected NMS package version.
     */
    public static String getNmsVersion() {
        return _version;
    }

    /**
     * Determine if one of the provided NMS package versions is compatible with the
     * current NMS package version.
     *
     * @param versions  The versions to check.
     */
    public static boolean isVersionCompatible(String... versions) {
        for (String version : versions) {
            if (version.equals(getNmsVersion())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Enforce the compatible NMS versions on the specified plugin.
     *
     * <p>If the current NMS version is not compatible, the plugin is
     * disabled.</p>
     *
     * <p>Server operators can override this in the config of plugins
     * that extend {@link NucleusPlugin}.</p>
     *
     * @param plugin              The plugin to check.
     * @param compatibleVersions  The NMS versions the plugin is compatible with.
     *
     * @return  True if the plugin is compatible.
     */
    public static boolean enforceNmsVersion(Plugin plugin, String... compatibleVersions) {

        if (!isVersionCompatible(compatibleVersions)) {

            if (plugin instanceof NucleusPlugin) {

                // allow server administrator the option to disable NMS version enforcement
                boolean enforceNmsVersion = ((NucleusPlugin) plugin).getDataNode()
                        .getBoolean("enforce-nms-version", true);

                if (!enforceNmsVersion) {
                    NucMsg.warning(plugin, "Plugin {0} is not compatible with NMS version {1}.",
                            plugin.getName(), getNmsVersion());

                    NucMsg.warning(plugin, "enforce-nms-version has been set to " +
                            "false in the plugins config.");
                    return true;
                }
            }

            // disable incompatible plugin
            Bukkit.getPluginManager().disablePlugin(plugin);

            NucMsg.warning(plugin, "Disabling {0} because it's not compatible with NMS version {1}",
                    plugin.getName(), getNmsVersion());
            return false;
        }

        return true;
    }

    /**
     * Get NucleusFrameworks internal action bar handler.
     *
     * @return  The action bar handler or null if a handler for the current version of
     * Minecraft does not exist.
     */
    @Nullable
    public static INmsActionBarHandler getActionBarHandler() {
        return Nucleus.getNmsManager().getHandler(InternalNmsManager.ACTION_BAR);
    }

    /**
     * Get NucleusFrameworks internal list header/footer handler.
     *
     * @return  The list header/footer handler or null if a handler for the current version of
     * Minecraft does not exist.
     */
    @Nullable
    public static INmsListHeaderFooterHandler getListHeaderFooterHandler() {
        return Nucleus.getNmsManager().getHandler(InternalNmsManager.LIST_HEADER_FOOTER);
    }

    /**
     * Get NucleusFrameworks internal particle effect handler.
     *
     * @return  The particle effect handler or null if a handler for the current version of
     * Minecraft does not exist.
     */
    public static INmsParticleEffectHandler getParticleEffectHandler() {
        return Nucleus.getNmsManager().getHandler(InternalNmsManager.PARTICLE_EFFECT);
    }

    /**
     * Get NucleusFramework's internal sound effect handler.
     *
     * @return  The sound effect handler or null if a handler for the current version of
     * Minecraft does not exist.
     */
    @Nullable
    public static INmsSoundEffectHandler getSoundEffectHandler() {
        return Nucleus.getNmsManager().getHandler(InternalNmsManager.SOUND_EFFECT);
    }

    /**
     * Get NucleusFramework's internal title handler.
     *
     * @return  The title handler or null if a handler for the current version of
     * Minecraft does not exist.
     */
    @Nullable
    public static INmsTitleHandler getTitleHandler() {
        return Nucleus.getNmsManager().getHandler(InternalNmsManager.TITLES);
    }

    // load the the craft package version from NucleusFramework's config
    // or detect the version.
    private static void loadPackageVersion() {
        _version = Nucleus.getPlugin().getDataNode().getString("package-version");
        if (_version == null) {

            Class<? extends Server> serverClass = Bukkit.getServer().getClass();

            Matcher versionMatcher = PATTERN_VERSION.matcher(serverClass.getName());

            if (versionMatcher.matches()) {
                _version = versionMatcher.group(1);

                NucMsg.info("Reflection: CraftBukkit version found: {0}", _version);
            }
        }
        else {
            NucMsg.info("Reflection: Using craft version from NucleusFramework config: {0}", _version);
        }

        if (_version == null) {
            NucMsg.severe("Failed to find CraftBukkit version for reflection purposes.");
        }
    }
}
