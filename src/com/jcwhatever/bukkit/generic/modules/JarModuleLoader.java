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

import com.jcwhatever.bukkit.generic.utils.EntryValidator;
import com.jcwhatever.bukkit.generic.utils.FileUtils;
import com.jcwhatever.bukkit.generic.utils.FileUtils.DirectoryTraversal;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
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
public class JarModuleLoader<T> {

    private final File _moduleFolder;
    private final Class<T> _moduleClass;
    private final DirectoryTraversal _directoryTraversal;

    private IModuleFactory<T> _moduleFactory;
    private IModuleInfoFactory<T> _moduleInfoFactory;
    private EntryValidator<Class<T>> _typeValidator;
    private EntryValidator<JarFile> _jarValidator;

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
     * @param settings     The settings to use.
     */
    public JarModuleLoader(Class<T> moduleClass, JarModuleLoaderSettings<T> settings) {
        PreCon.notNull(moduleClass);
        PreCon.notNull(settings);

        settings.seal();

        _moduleClass = moduleClass;
        _moduleFolder = settings.getModuleFolder();
        _directoryTraversal = settings.getDirectoryTraversal();

        _moduleFactory = settings.getModuleFactory() != null
                ? settings.getModuleFactory()
                : getDefaultModuleFactory();

        _moduleInfoFactory = settings.getModuleInfoFactory() != null
                ? settings.getModuleInfoFactory()
                : getDefaultModuleInfoFactory();

        _jarValidator = settings.getJarValidator() != null
                ? settings.getJarValidator()
                : getDefaultJarValidator();

        _typeValidator = settings.getTypeValidator() != null
                ? settings.getTypeValidator()
                : getDefaultTypeValidator();
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
    public File getModuleFolder() {
        return _moduleFolder;
    }

    /**
     * Get the directory traversal used to
     * find jar files.
     */
    public DirectoryTraversal getDirectoryTraversal() {
        return _directoryTraversal;
    }

    /**
     * Get the module factory used to instantiate
     * module instances.
     */
    public IModuleFactory<T> getModuleFactory() {
        return _moduleFactory;
    }

    /**
     * Get the module info factory used to instantiate
     * {@code IModuleInfo} instances.
     */
    public IModuleInfoFactory<T> getModuleInfoFactory() {
        return _moduleInfoFactory;
    }

    /**
     * Get the jar validator being used to validate
     * jar files.
     */
    public EntryValidator<JarFile> getJarValidator() {
        return _jarValidator;
    }

    /**
     * Get the type validator being used to validate
     * a class type.
     */
    public EntryValidator<Class<T>> getTypeValidator() {
        return _typeValidator;
    }

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

        List<Class<T>> moduleClasses = getModuleClasses();

        if (moduleClasses.isEmpty())
            return;

        for (Class<T> clazz : moduleClasses) {

            // create instance of module
            T instance;

            try {
                instance = getModuleFactory().create(clazz);
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

            IModuleInfo moduleInfo = getModuleInfoFactory().create(instance);
            if (moduleInfo == null)
                continue;

            addModule(moduleInfo, instance);
        }
    }

    /**
     * Get a new instance of the default type validator.
     */
    public EntryValidator<Class<T>> getDefaultTypeValidator() {

        return new EntryValidator<Class<T>>() {
            @Override
            public boolean isValid(Class<T> entry) {
                return true;
            }
        };
    }

    /**
     * Get a new instance of the default jar validator.
     */
    public EntryValidator<JarFile> getDefaultJarValidator() {

        return new EntryValidator<JarFile>() {
            @Override
            public boolean isValid(JarFile entry) {
                return true;
            }
        };
    }

    /**
     * Get a new instance of the default module factory.
     */
    public IModuleFactory<T> getDefaultModuleFactory() {

        return new IModuleFactory<T>() {
            @Override
            public T create(Class<T> clazz)
                    throws IllegalAccessException, InstantiationException {
                return clazz.newInstance();
            }
        };
    }

    /**
     * Get a new instance of the default module info factory.
     */
    public IModuleInfoFactory<T> getDefaultModuleInfoFactory() {

        return new IModuleInfoFactory<T>() {
            @Override
            public IModuleInfo create(T module) {
                return new SimpleModuleInfo(module.getClass().getCanonicalName());
            }
        };
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
    protected List<Class<T>> getModuleClasses() {

        List<File> files = FileUtils.getFiles(getModuleFolder(), getDirectoryTraversal(),
                new EntryValidator<File>() {

                    @Override
                    public boolean isValid(File entry) {
                        return entry.getName().endsWith(".jar");
                    }
                });

        if (files.isEmpty())
            return new ArrayList<>(0);

        List<Class<T>> results = new ArrayList<>(30);

        for (File file : files) {

            List<Class<T>> modules;

            // get module classes from jar file
            try {
                modules = getModuleClasses(file);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            results.addAll(modules);
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
    protected List<Class<T>> getModuleClasses(File file) throws IOException {

        List<Class<T>> moduleClasses = _moduleClassMap.get(file.getAbsolutePath());
        if (moduleClasses != null)
            return moduleClasses;

        JarFile jarFile = new JarFile(file);

        // validate jar file
        if (!_jarValidator.isValid(jarFile))
            return new ArrayList<>(0);

        Enumeration<JarEntry> entries = jarFile.entries();

        URL[] urls = { new URL("jar:file:" + file + "!/") };

        URLClassLoader classLoader = URLClassLoader.newInstance(urls, getClass().getClassLoader());

        // set to cache class names, if a module is found
        // these are added to _loadedClasses set.
        Set<String> classNames = new HashSet<>(10);

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

            Class<?> c;
            try {
                c = classLoader.loadClass(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                continue;
            }

            classNames.add(className);

            // check if type is a module
            if (getModuleClass().isAssignableFrom(c)) {

                @SuppressWarnings("unchecked")
                Class<T> clazz = (Class<T>)c;

                // validate type
                if (_typeValidator.isValid(clazz)) {
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
        _loadedClasses.addAll(classNames);

        return moduleClasses;
    }

}
