package com.jcwhatever.bukkit.generic.scripting;

import com.jcwhatever.bukkit.generic.scripting.api.IScriptApi;
import com.jcwhatever.bukkit.generic.scripting.api.ScriptApiBukkitEvents;
import com.jcwhatever.bukkit.generic.scripting.api.ScriptApiEconomy;
import com.jcwhatever.bukkit.generic.scripting.api.ScriptApiInclude;
import com.jcwhatever.bukkit.generic.scripting.api.ScriptApiInventory;
import com.jcwhatever.bukkit.generic.scripting.api.ScriptApiItemBank;
import com.jcwhatever.bukkit.generic.scripting.api.ScriptApiJail;
import com.jcwhatever.bukkit.generic.scripting.api.ScriptApiLoader;
import com.jcwhatever.bukkit.generic.scripting.api.ScriptApiMsg;
import com.jcwhatever.bukkit.generic.scripting.api.ScriptApiPermissions;
import com.jcwhatever.bukkit.generic.scripting.api.ScriptApiSounds;
import com.jcwhatever.bukkit.generic.utils.FileUtils;
import com.jcwhatever.bukkit.generic.utils.FileUtils.DirectoryTraversal;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.TextUtils;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Script utilities.
 */
public class ScriptHelper {

    private ScriptHelper() {}

    private static final Pattern PATTERN_LEADING_DOT = Pattern.compile("^\\.");

    private static ScriptEngineManager _globalScriptEngineManager;

    /**
     * Get the global script engine manager.
     */
    public static ScriptEngineManager getGlobalEngineManager() {
        if (_globalScriptEngineManager == null)
            _globalScriptEngineManager = new ScriptEngineManager();

        return _globalScriptEngineManager;
    }

    /**
     * Get new instances of the default Generics API
     *
     * @param plugin  The owning plugin.
     */
    public static List<IScriptApi> getDefaultApi(Plugin plugin, @Nullable GenericsScriptManager manager) {
        List<IScriptApi> api = new ArrayList<>(15);

        api.add(new ScriptApiEconomy(plugin));
        api.add(new ScriptApiBukkitEvents(plugin));
        api.add(new ScriptApiInclude(plugin, manager));
        api.add(new ScriptApiInventory(plugin));
        api.add(new ScriptApiItemBank(plugin));
        api.add(new ScriptApiJail(plugin));
        api.add(new ScriptApiMsg(plugin));
        api.add(new ScriptApiPermissions(plugin));
        api.add(new ScriptApiSounds(plugin));
        api.add(new ScriptApiLoader(plugin));

        return api;
    }

    /**
     * Get a script engine for the script.
     *
     * @param script  The script to get a script engine for.
     *
     * @return  Null if a script engine could not be found for the script type.
     */
    @Nullable
    public static ScriptEngine getNewScriptEngine(IScript script) {
        return getGlobalEngineManager().getEngineByExtension(script.getType());
    }

    /**
     * Load scripts from a script folder.
     *
     * @param engineManager      The engine manager used to determine if a file type is a script.
     * @param scriptFolder       The folder to find scripts in.
     * @param traversal          The type of directory traversal used to find script files.
     * @param scriptConstructor  A script constructor to create new script instances.
     * @param <T>                Script instance type.
     */
    public static <T extends IScript> List<T> loadScripts(ScriptEngineManager engineManager, File scriptFolder,
                                           DirectoryTraversal traversal, ScriptConstructor<T> scriptConstructor) {

        PreCon.notNull(scriptFolder);
        PreCon.isValid(scriptFolder.isDirectory());

        if (!scriptFolder.exists())
            return null;

        List<File> scriptFiles = FileUtils.getFiles(scriptFolder, traversal);
        List<T> result = new ArrayList<>(scriptFiles.size());

        for (File file : scriptFiles) {

            String type = getScriptType(file);
            if (engineManager.getEngineByExtension(FileUtils.getFileExtension(file)) == null)
                continue;

            String name = getScriptName(scriptFolder, file);

            try {

                BufferedReader reader = new BufferedReader(new FileReader(file));

                StringBuilder buffer = new StringBuilder(2000);
                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                    buffer.append('\n');
                }

                reader.close();

                T script = scriptConstructor.construct(name, type, buffer.toString());
                if (script != null)
                    result.add(script);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * Load a single script into a new script instance.
     *
     * @param scriptFolder       The folder to find scripts in.
     * @param scriptFile         The script file.
     * @param scriptConstructor  A script constructor used to create new script instances.
     * @param <T>                Script instance type.
     *
     * @return Null if the file is not found or there is an error opening or reading from it.
     */
    @Nullable
    public static <T extends IScript> T loadScript(File scriptFolder, File scriptFile, ScriptConstructor<T> scriptConstructor) {
        PreCon.notNull(scriptFile);

        if (!scriptFile.exists())
            return null;

        try {

            BufferedReader reader = new BufferedReader(new FileReader(scriptFile));

            StringBuilder buffer = new StringBuilder(2000);
            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line);
                buffer.append('\n');
            }

            reader.close();

            String scriptName = getScriptName(scriptFolder, scriptFile);

            String scriptType = getScriptType(scriptFile);

            return scriptConstructor.construct(scriptName, scriptType, buffer.toString());

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get a name for a script based on the scriptFolder and file name.
     *
     * @param scriptFolder  The base folder where scripts are kept.
     * @param file          The script file.
     */
    public static String getScriptName(File scriptFolder, File file) {
        PreCon.notNull(scriptFolder);
        PreCon.isValid(scriptFolder.isDirectory());
        PreCon.notNull(file);
        PreCon.isValid(!file.isDirectory());

        String baseCacheName = FileUtils.getRelative(scriptFolder, file.getParentFile());

        Matcher pathMatcher = TextUtils.PATTERN_FILEPATH_SLASH.matcher(baseCacheName);
        baseCacheName = pathMatcher.replaceAll(".");

        String result = baseCacheName + '.' + FileUtils.getNameWithoutExtension(file);

        Matcher leadingDotMatcher = PATTERN_LEADING_DOT.matcher(result);
        return leadingDotMatcher.replaceAll("");

    }

    /**
     * Get the script type from a file.
     *
     * <p>Returns the file extension.</p>
     *
     * @param file  The script file.
     */
    public static String getScriptType(File file) {
        PreCon.notNull(file);
        PreCon.isValid(!file.isDirectory());

        return FileUtils.getFileExtension(file);
    }

    /**
     * Interface for a script instance constructor.
     *
     * @param <T>  The type of {@code IScript} that is constructed.
     */
    public static interface ScriptConstructor<T extends IScript> {
        public T construct(String name, String type, String script);
    }

}
