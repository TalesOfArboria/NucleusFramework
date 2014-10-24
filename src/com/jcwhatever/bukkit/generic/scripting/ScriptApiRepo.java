package com.jcwhatever.bukkit.generic.scripting;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.scripting.api.IScriptApi;
import com.jcwhatever.bukkit.generic.scripting.api.ScriptApiBukkitEvents;
import com.jcwhatever.bukkit.generic.scripting.api.ScriptApiEconomy;
import com.jcwhatever.bukkit.generic.scripting.api.ScriptApiInventory;
import com.jcwhatever.bukkit.generic.scripting.api.ScriptApiItemBank;
import com.jcwhatever.bukkit.generic.scripting.api.ScriptApiJail;
import com.jcwhatever.bukkit.generic.scripting.api.ScriptApiLoader;
import com.jcwhatever.bukkit.generic.scripting.api.ScriptApiMsg;
import com.jcwhatever.bukkit.generic.scripting.api.ScriptApiPermissions;
import com.jcwhatever.bukkit.generic.scripting.api.ScriptApiSounds;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import javax.annotation.Nullable;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Global repository of script API's that can be used
 * by any plugins script manager.
 */
public class ScriptApiRepo {

    // TODO instance caching per plugin

    private ScriptApiRepo() {}

    private static Map<String, ApiPackage>  _scriptApis = new HashMap<>(50);

    static {
        registerApiType(GenericsLib.getInstance(), ScriptApiBukkitEvents.class);
        registerApiType(GenericsLib.getInstance(), ScriptApiEconomy.class);
        registerApiType(GenericsLib.getInstance(), ScriptApiInventory.class);
        registerApiType(GenericsLib.getInstance(), ScriptApiItemBank.class);
        registerApiType(GenericsLib.getInstance(), ScriptApiJail.class);
        registerApiType(GenericsLib.getInstance(), ScriptApiLoader.class);
        registerApiType(GenericsLib.getInstance(), ScriptApiMsg.class);
        registerApiType(GenericsLib.getInstance(), ScriptApiPermissions.class);
        registerApiType(GenericsLib.getInstance(), ScriptApiSounds.class);
    }

    /**
     * Register a script api with the repository so
     * other plugins can use it.
     *
     * @param plugin    The api owning plugin.
     * @param apiClass  The api type to register.
     */
    public static boolean registerApiType(Plugin plugin, Class<? extends IScriptApi> apiClass) {
        PreCon.notNull(apiClass);

        IScriptApiInfo info = apiClass.getAnnotation(IScriptApiInfo.class);
        if (info == null)
            throw new RuntimeException("Cannot register scripting api because it has no IScriptApiInfo annotation.");


        String apiKey = getApiKey(plugin, info.variableName());

        if (_scriptApis.containsKey(apiKey))
            return false;

        Constructor<? extends IScriptApi> constructor;
        try {
            constructor = apiClass.getConstructor(Plugin.class);
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to register a script api because it does not have the correct constructor.");
        }

        _scriptApis.put(apiKey, new ApiPackage(apiClass, constructor));

        return true;
    }

    /**
     * Unregister a script api from the repository.
     *
     * <p>
     *     Plugins are expected to unregister their API types
     *     when they are disabled.
     * </p>
     *
     * @param plugin    The api owning plugin.
     * @param apiClass  The api type to remove.
     */
    public static boolean unregisterApiType(Plugin plugin, Class<? extends IScriptApi> apiClass) {
        PreCon.notNull(apiClass);

        IScriptApiInfo info = apiClass.getAnnotation(IScriptApiInfo.class);
        if (info == null)
            throw new RuntimeException("Cannot unregister scripting api because it has no IScriptApiInfo annotation.");

        String apiKey = getApiKey(plugin, info.variableName());

        return _scriptApis.remove(apiKey) != null;
    }

    /**
     * Get an api from the repository by the name of its owning plugin
     * and variable name and instantiate for the specified plugin.
     *
     * @param plugin            The plugin to instantiate the api for.
     * @param owningPluginName  The name of the api owning plugin.
     * @param variableName      Case sensitive variable name.
     */
    @Nullable
    public static IScriptApi getApi(Plugin plugin, String owningPluginName, String variableName) {
        PreCon.notNull(plugin);
        PreCon.notNullOrEmpty(owningPluginName);
        PreCon.notNullOrEmpty(variableName);

        String apiKey = getApiKey(owningPluginName, variableName);

        ApiPackage apiPackage = _scriptApis.get(apiKey);
        if (apiPackage == null)
            return null;

        return instantiate(plugin, apiPackage);
    }

    /**
     * Get an api from the repository by its
     * owning plugin and variable name and instantiate it
     * for the owning plugin.
     *
     * @param plugin        The api owning plugin and plugin to instantiate for.
     * @param variableName  Case sensitive variable name.
     */
    @Nullable
    public static IScriptApi getApi(Plugin plugin, String variableName) {
        PreCon.notNull(plugin);
        PreCon.notNullOrEmpty(variableName);

        String apiKey = getApiKey(plugin, variableName);

        ApiPackage apiPackage = _scriptApis.get(apiKey);
        if (apiPackage == null)
            return null;

        return instantiate(plugin, apiPackage);
    }

    // get api key from plugin and api variable name
    private static String getApiKey(Plugin plugin, String apiName) {
        return plugin.getName().toLowerCase() + ':' + apiName;
    }

    // get api key from plugin name and api variable name
    private static String getApiKey(String pluginName, String apiName) {
        return pluginName.toLowerCase() + ':' + apiName;
    }

    private static IScriptApi instantiate(Plugin plugin, ApiPackage apiPackage) {

        Class<? extends IScriptApi> apiClass = apiPackage.apiClass;
        Constructor<? extends IScriptApi> constructor = apiPackage.constructor;

        IScriptApiInfo info = apiClass.getAnnotation(IScriptApiInfo.class);
        if (info == null)
            throw new RuntimeException("Registered script api class does not have required IScriptApiInfo annotation.");

        try {
            return constructor.newInstance(plugin);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to instantiate a script api.");
        }
    }

    private static class ApiPackage {
        final Class<? extends IScriptApi> apiClass;
        final Constructor<? extends IScriptApi> constructor;

        ApiPackage(Class<? extends IScriptApi> apiClass, Constructor<? extends IScriptApi> constructor) {
            this.apiClass = apiClass;
            this.constructor = constructor;
        }
    }

}
