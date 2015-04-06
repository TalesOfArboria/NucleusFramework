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


package com.jcwhatever.nucleus.utils.modules;

import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.utils.validate.IValidator;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.file.FileUtils;
import com.jcwhatever.nucleus.utils.file.FileUtils.DirectoryTraversal;
import com.jcwhatever.nucleus.utils.file.FileUtils.ITextLineProducer;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.annotation.Nullable;

/**
 * Loads jar file modules.
 */
public abstract class JarModuleLoader<T> implements IPluginOwned {

    private final Plugin _plugin;
    private final Class<T> _moduleClass;

    private Set<String> _loadedClasses = new HashSet<>(1000);

    // keyed to jar file absolute path
    private Map<String, List<Class<T>>> _moduleClassMap = new HashMap<>(100);

    // keyed to name specified in jar module info
    private Map<String, T> _modules = new HashMap<>(20);

    private Map<T, IModuleInfo> _moduleInfo = new HashMap<>(20);

    /**
     * Constructor.
     *
     * @param plugin       The owning plugin.
     * @param moduleClass  The super type of a module class.
     */
    public JarModuleLoader(Plugin plugin, Class<T> moduleClass) {
        PreCon.notNull(plugin);
        PreCon.notNull(moduleClass);

        _plugin = plugin;
        _moduleClass = moduleClass;
    }

    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Get the module class or super class.
     */
    public Class<T> getModuleClass() {
        return _moduleClass;
    }

    /**
     * Get the module folder.
     */
    public abstract File getModuleFolder();

    /**
     * Get the directory traversal used to find jar files.
     */
    public abstract DirectoryTraversal getDirectoryTraversal();

    /**
     * Get loaded modules.
     */
    public List<T> getModules() {
        return new ArrayList<>(_modules.values());
    }

    /**
     * Get a module by the name specified in its {@link IModuleInfo} data object.
     */
    @Nullable
    public T getModule(String moduleName) {
        PreCon.notNullOrEmpty(moduleName);

        return _modules.get(moduleName.toLowerCase());
    }

    /**
     * Get a modules {@link IModuleInfo} data object.
     */
    @Nullable
    public <I extends IModuleInfo> I getModuleInfo(T module) {

        IModuleInfo moduleInfo = _moduleInfo.get(module);
        if (moduleInfo == null)
            return null;

        @SuppressWarnings("unchecked")
        I result = (I)_moduleInfo.get(module);

        return result;
    }

    /**
     * Get all module classes from jar files in the module folder and instantiate them.
     */
    public void loadModules() {

        File folder = getModuleFolder();
        if (!folder.exists())
            return;

        if (!folder.isDirectory())
            throw new RuntimeException("Module folder must be a folder.");

        List<File> files = FileUtils.getFiles(folder, getDirectoryTraversal(),
                new IValidator<File>() {

                    @Override
                    public boolean isValid(File element) {
                        return element.getName().endsWith(".jar");
                    }
                });

        List<Class<T>> moduleClasses = getModuleClasses(files);

        for (Class<T> clazz : moduleClasses) {

            try {
                // create instance of module
                T instance = instantiateModule(clazz);
                if (instance == null)
                    continue;

                IModuleInfo moduleInfo = createModuleInfo(instance);
                if (moduleInfo == null)
                    continue;

                addModule(moduleInfo, instance);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Invoked after a module is loaded to add it to the appropriate collections.
     */
    protected void addModule(IModuleInfo info, T instance) {
        _moduleInfo.put(instance, info);
        _modules.put(info.getSearchName(), instance);
    }

    /**
     * Invoked to remove a module from the appropriate collections.
     *
     * @param name  The search name of the module.
     */
    protected void removeModule(String name) {
        T module = _modules.remove(name);
        if (module != null) {
            _moduleInfo.remove(module);
        }
    }

    /**
     * Get all module classes from jar files in the modules folder.
     */
    protected List<Class<T>> getModuleClasses(Collection<File> files) {

        if (files.isEmpty())
            return new ArrayList<>(0);

        List<Class<T>> results = new ArrayList<>(30);

        for (File file : files) {

            ClassLoadMethod method = getLoadMethod(file);
            if (method == null)
                continue;

            switch (method) {

                case DIRECT_OR_SEARCH:
                    // fall through

                case DIRECT:

                    Class<T> module;
                    try {
                        module = getModuleClass(file, method);
                    } catch (IOException e) {
                        e.printStackTrace();
                        continue;
                    }

                    if (module != null) {
                        results.add(module);
                        break;
                    }
                    else if (method != ClassLoadMethod.DIRECT_OR_SEARCH) {
                        break;
                    }
                    // else fall through

                case SEARCH:
                    List<Class<T>> modules;
                    // get module classes from jar file
                    try {
                        modules = searchModuleClasses(file, method);
                    } catch (IOException e) {
                        e.printStackTrace();
                        continue;
                    }

                    results.addAll(modules);
                    break;

                default:
                    throw new AssertionError();
            }
        }

        return results;
    }

    /**
     * Find module classes in the specified jar file.
     *
     * @param file  The jar file to search in.
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    protected List<Class<T>> searchModuleClasses(File file, ClassLoadMethod loadMethod) throws IOException {

        List<Class<T>> moduleClasses = _moduleClassMap.get(file.getAbsolutePath());
        if (moduleClasses != null)
            return moduleClasses;

        JarFile jarFile = new JarFile(file);

        // validate jar file
        if (!isValidJarFile(jarFile))
            return new ArrayList<>(0);

        Enumeration<JarEntry> entries = jarFile.entries();

        URL[] urls = { new URL("jar:file:" + file + "!/") };

        // a search loader to be discarded when the search is complete
        URLClassLoader classLoader = URLClassLoader.newInstance(urls, getClass().getClassLoader());

        // set to cache class names, if a module is found
        // these are added to _loadedClasses set.
        Set<String> searchResults = new HashSet<>(10);

        moduleClasses = new ArrayList<>(10);

        while (entries.hasMoreElements()) {

            JarEntry entry = entries.nextElement();

            // make sure entry is a class file
            if (entry.isDirectory() || !entry.getName().endsWith(".class"))
                continue;

            // get the class name
            String className = entry.getName().substring(0,  entry.getName().length() - 6);
            className = className.replace('/', '.');

            // check if the class has already been loaded
            if (_loadedClasses.contains(className))
                continue;

            // validate the class before loading it
            if (!isValidClassName(className))
                continue;

            Class<?> c;
            try {
                c = classLoader.loadClass(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                continue;
            }

            searchResults.add(className);

            // check if type is a module
            if (getModuleClass().isAssignableFrom(c)) {

                @SuppressWarnings("unchecked")
                Class<T> clazz = (Class<T>)c;

                // validate type
                if (isValidType(clazz)) {
                    moduleClasses.add(clazz);
                }
            }
        }

        jarFile.close();

        // module class not found
        if (moduleClasses.isEmpty()) {
            return moduleClasses;
        }

        // cache module class
        _moduleClassMap.put(file.getAbsolutePath(), moduleClasses);

        // add class names to prevent reloading
        _loadedClasses.addAll(searchResults);

        if (moduleClasses.size() == 1 &&
                loadMethod == ClassLoadMethod.DIRECT_OR_SEARCH) {
            // save class name for direct load next time
            saveClassName(file, moduleClasses.get(0));
        }

        return moduleClasses;
    }

    /**
     * Find a specific module class in the specified jar file.
     *
     * @param file  The jar file to search in.
     *
     * @return  The class or null if not found.
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Nullable
    protected Class<T> getModuleClass(File file, ClassLoadMethod loadMethod) throws IOException {
        PreCon.notNull(file);

        JarFile jarFile = new JarFile(file);

        // validate jar file
        if (!isValidJarFile(jarFile))
            return null;

        String className = getModuleClassName(jarFile);
        if (className == null) {

            if (loadMethod == ClassLoadMethod.DIRECT_OR_SEARCH) {
                // see if a saved class path exists
                className = getSavedClassName(file);
            }

            if (className == null)
                return null;
        }

        return getModuleClass(jarFile, className);
    }

    /**
     * Find a specific module class in the specified jar file.
     *
     * @param jarFile    The jar file to search in.
     * @param className  The module class name.
     *
     * @return  Null if class not found.
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Nullable
    protected Class<T> getModuleClass(JarFile jarFile, String className) throws IOException {
        PreCon.notNull(jarFile);

        // validate the class before loading it
        if (!isValidClassName(className))
            return null;

        URL[] urls = { new URL("jar:file:" + jarFile.getName() + "!/") };

        URLClassLoader classLoader = URLClassLoader.newInstance(urls, getClass().getClassLoader());

        Class<?> c;
        try {
            c = classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        // check if type is a module
        if (!getModuleClass().isAssignableFrom(c)) {
            return null;
        }

        // add class names to prevent reloading
        _loadedClasses.add(className);

        @SuppressWarnings("unchecked")
        Class<T> result = (Class<T>)c;

        return result;
    }

    /**
     * Get a modules saved class name.
     *
     * @param file  The module jar file.
     *
     * @return  The class name or null if not found.
     */
    @Nullable
    protected String getSavedClassName(File file) {

        File saveFile = new File(file.getParent(), file.getName() + ".classpath");
        if (!saveFile.exists())
            return null;

        return FileUtils.scanTextFile(saveFile, StandardCharsets.UTF_8, new IValidator<String>() {

            int index = 0;
            @Override
            public boolean isValid(String element) {
                index++;
                return index == 1;
            }
        });
    }

    /**
     * Save a modules class name.
     *
     * @param file   The module jar file.
     * @param clazz  The module class.
     */
    protected void saveClassName(File file, final Class<? extends T> clazz) {

        File saveFile = new File(file.getParent(), file.getName() + ".classpath");

        FileUtils.writeTextFile(saveFile, StandardCharsets.UTF_8, 1, new ITextLineProducer() {

            @Nullable
            @Override
            public String nextLine() {
                return clazz.getCanonicalName();
            }
        });
    }

    /**
     * Invoked to validate a jar file.
     *
     * <p>Intended for optional override.</p>
     *
     * @param jarFile  The jar file to check.
     *
     * @return  True if valid, false to deny.
     */
    protected boolean isValidJarFile(JarFile jarFile) {
        return true;
    }

    /**
     * Invoked to validate a class name before loading the class.
     *
     * <p>Intended for optional override.</p>
     *
     * @param className  The class name to validate.
     *
     * @return  True if valid, false to deny.
     */
    protected boolean isValidClassName(String className) {
        return true;
    }

    /**
     * Invoked to valid a module type before instantiation.
     *
     * <p>Intended for optional override.</p>
     *
     * @param type  The type to validate
     *
     * @return  True if valid, false to deny.
     */
    protected boolean isValidType(Class<T> type) {
        return true;
    }

    /**
     * Get the class load method to use to load a jar file.
     *
     * @param file  The file that will be loaded.
     *
     * @return  The load method or null to cancel loading the file.
     */
    protected abstract ClassLoadMethod getLoadMethod(File file);

    /**
     * Get the name of the module class to load from the jar file.
     *
     * <p>Only invoked when the {@link ClassLoadMethod} is {@link ClassLoadMethod#DIRECT}
     * or {@link ClassLoadMethod#DIRECT_OR_SEARCH}.</p>
     *
     * @param jarFile  The jar file being loaded.
     *
     * @return  The name of the class to load or null to cancel.
     */
    protected abstract String getModuleClassName(JarFile jarFile);

    /**
     * Create a new instance of {@link IModuleInfo} and fill with information
     * about the specified module.
     *
     * @param moduleInstance  The module instance.
     *
     * @return  Null if failed or to cancel loading the module.
     */
    @Nullable
    protected abstract IModuleInfo createModuleInfo(T moduleInstance);

    /**
     * Create a new instance of a module.
     *
     * @param clazz   The module class.
     *
     * @return  Null if failed or to cancel loading of module.
     */
    @Nullable
    protected abstract T instantiateModule(Class<T> clazz);
}
