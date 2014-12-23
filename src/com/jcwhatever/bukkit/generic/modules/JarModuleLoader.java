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


package com.jcwhatever.bukkit.generic.modules;

import com.jcwhatever.bukkit.generic.utils.FileUtils;
import com.jcwhatever.bukkit.generic.utils.FileUtils.DirectoryTraversal;
import com.jcwhatever.bukkit.generic.utils.IEntryValidator;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
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
public abstract class JarModuleLoader<T> {

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
     * @param moduleClass  The super type of a module class.
     */
    public JarModuleLoader(Class<T> moduleClass) {
        PreCon.notNull(moduleClass);

        _moduleClass = moduleClass;
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
     * Get the directory traversal used to
     * find jar files.
     */
    public abstract DirectoryTraversal getDirectoryTraversal();

    /**
     * Get loaded modules.
     */
    public List<T> getModules() {
        return new ArrayList<>(_modules.values());
    }

    /**
     * Get a module by the name specified in its
     * {@code IModuleInfo} data object.
     */
    @Nullable
    public T getModule(String moduleName) {
        PreCon.notNullOrEmpty(moduleName);

        return _modules.get(moduleName.toLowerCase());
    }

    /**
     * Get a modules {@code IModuleInfo} data object.
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
     * Get all module classes from jar files
     * in the module folder and instantiate them.
     */
    public void loadModules() {

        File folder = getModuleFolder();
        if (!folder.exists())
            return;

        if (!folder.isDirectory())
            throw new RuntimeException("Module folder must be a folder.");

        List<File> files = FileUtils.getFiles(getModuleFolder(), getDirectoryTraversal(),
                new IEntryValidator<File>() {

                    @Override
                    public boolean isValid(File entry) {
                        return entry.getName().endsWith(".jar");
                    }
                });

        List<Class<T>> moduleClasses = getModuleClasses(files);

        if (moduleClasses.isEmpty())
            return;

        for (Class<T> clazz : moduleClasses) {

            // create instance of module
            T instance;

            try {
                instance = instantiateModule(clazz);
            } catch (InstantiationException e) {
                e.printStackTrace();
                continue;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                continue;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                continue;
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                continue;
            }

            if (instance == null)
                continue;

            IModuleInfo moduleInfo = createModuleInfo(instance);
            if (moduleInfo == null)
                continue;

            addModule(moduleInfo, instance);
        }
    }

    /**
     * Called after a module is loaded to add it to the
     * appropriate collections.
     */
    protected void addModule(IModuleInfo info, T instance) {
        _moduleInfo.put(instance, info);
        _modules.put(info.getSearchName(), instance);
    }

    /**
     * Called to remove a module from the
     * appropriate collections.
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
     * Get all module classes from jar files in
     * the modules folder.
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

                case DIRECT:
                    Class<T> module;
                    try {
                        module = getModuleClass(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                        continue;
                    }

                    if (module != null)
                        results.add(module);

                    break;

                case SEARCH:
                    List<Class<T>> modules;
                    // get module classes from jar file
                    try {
                        modules = searchModuleClasses(file);
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
     * @param file         The jar file to search in.
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    protected List<Class<T>> searchModuleClasses(File file) throws IOException {

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

        return moduleClasses;
    }


    /**
     * Find a specific module class in the specified jar file.
     *
     * @param file  The jar file to search in.
     *
     * @return  Null if class not found.
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Nullable
    protected Class<T> getModuleClass(File file) throws IOException {
        PreCon.notNull(file);

        JarFile jarFile = new JarFile(file);

        // validate jar file
        if (!isValidJarFile(jarFile))
            return null;

        String className = getModuleClassName(jarFile);
        if (className == null)
            return null;

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
     * Called to validate a jar file.
     *
     * <p>Intended to be overridden if needed.</p>
     *
     * @param jarFile  The jar file to check.
     *
     * @return  True if valid, false to deny.
     */
    protected boolean isValidJarFile(@SuppressWarnings("unused") JarFile jarFile) {
        return true;
    }

    /**
     * Called to validate a class name before loading the class.
     *
     * <p>Intended to be overridden if needed.</p>
     *
     * @param className  The class name to validate.
     *
     * @return  True if valid, false to deny.
     */
    protected boolean isValidClassName(@SuppressWarnings("unused") String className) {
        return true;
    }

    /**
     * Called to valid a module type before instantiation.
     *
     * <p>Intended to be overridden if needed.</p>
     *
     * @param type  The type to validate
     *
     * @return  True if valid, false to deny.
     */
    protected boolean isValidType(@SuppressWarnings("unused") Class<T> type) {
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
     * <p>Only called when the {@code ClassLoadMethod} is {@code DIRECT}.</p>
     *
     * @param jarFile  The jar file being loaded.
     *
     * @return  The name of the class to load or null to cancel.
     */
    protected abstract String getModuleClassName(JarFile jarFile);

    /**
     * Create a new instance of {@code IModuleInfo}
     * and fill with information about the specified
     * module.
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
     *
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    @Nullable
    public abstract T instantiateModule(Class<T> clazz)
            throws InstantiationException, IllegalAccessException,
            NoSuchMethodException, InvocationTargetException;
}
