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
import com.jcwhatever.bukkit.generic.utils.PreCon;

import java.io.File;
import java.io.IOException;
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

/**
 * Aids in loading jar file classes that implement {@code IJarModule}.
 */
public class JarModuleLoader<T> {

    private ModuleFactory<T> _moduleFactory;
    private EntryValidator<Class<T>> _typeValidator;
    private EntryValidator<JarFile> _jarValidator;

    private Set<String> _loadedClasses = new HashSet<>(1000);
    private Map<String, List<Class<T>>> _moduleMap = new HashMap<>(100);

    /**
     * Constructor.
     *
     * <p>Initializes using default type and jar validator.</p>
     *
     * <p>Initializes using default module factory.</p>
     */
    public JarModuleLoader() {

        _moduleFactory = getDefaultModuleFactory();
        _typeValidator = getDefaultTypeValidator();
        _jarValidator = getDefaultJarValidator();
    }

    /**
     * Constructor.
     *
     * @param moduleFactory  The module factory used to instantiate module instances.
     */
    public JarModuleLoader(ModuleFactory<T> moduleFactory) {
        PreCon.notNull(moduleFactory);

        _moduleFactory = moduleFactory;
        _typeValidator = getDefaultTypeValidator();
        _jarValidator = getDefaultJarValidator();
    }

    /**
     * Constructor.
     *
     * @param jarValidator   The jar file validator to use.
     * @param typeValidator  The type validator to use.
     */
    public JarModuleLoader(EntryValidator<JarFile> jarValidator, EntryValidator<Class<T>> typeValidator) {
        PreCon.notNull(jarValidator);
        PreCon.notNull(typeValidator);

        _moduleFactory = getDefaultModuleFactory();
        _jarValidator = jarValidator;
        _typeValidator = typeValidator;
    }

    /**
     * Constructor.
     *
     * @param moduleFactory  The module factory used to instantiate module instances.
     * @param jarValidator   The jar file validator to use.
     * @param typeValidator  The type validator to use.
     */
    public JarModuleLoader(ModuleFactory<T> moduleFactory,
                           EntryValidator<JarFile> jarValidator, EntryValidator<Class<T>> typeValidator) {
        PreCon.notNull(moduleFactory);
        PreCon.notNull(jarValidator);
        PreCon.notNull(typeValidator);

        _moduleFactory = getDefaultModuleFactory();
        _jarValidator = jarValidator;
        _typeValidator = typeValidator;
    }

    /**
     * Get the module factory used to instantiate
     * module instances.
     */
    public ModuleFactory<T> getModuleFactory() {
        return _moduleFactory;
    }

    /**
     * Set the module factory used to instantiate
     * module instances.
     *
     * @param moduleFactory  The module factory to use.
     */
    public void setModuleFactory(ModuleFactory<T> moduleFactory) {
        _moduleFactory = moduleFactory;
    }

    /**
     * Get the jar validator being used to validate
     * jar files.
     */
    public EntryValidator<JarFile> getJarValidator() {
        return _jarValidator;
    }

    /**
     * Set the jar validator used to validate jar files.
     *
     * @param validator  The jar validator.
     */
    public void setJarValidator(EntryValidator<JarFile> validator) {
        _jarValidator = validator;
    }

    /**
     * Get the type validator being used to validate
     * a class type.
     */
    public EntryValidator<Class<T>> getTypeValidator() {
        return _typeValidator;
    }

    /**
     * Set the type validator used to validate the
     * jar type.
     *
     * @param validator  The type validator.
     */
    public void setTypeValidator(EntryValidator<Class<T>> validator) {
        _typeValidator = validator;
    }

    /**
     * Get all {@code IJarModule} classes from jar files
     * in a directory.
     *
     * @param moduleClass  The class to search for.
     * @param directory    The directory to search for jar files in.
     */
    public List<Class<T>> getModules(Class<T> moduleClass, File directory) {
        PreCon.notNull(directory);
        PreCon.isValid(directory.exists(), "directory argument points to a location that does not exist.");
        PreCon.isValid(directory.isDirectory(), "directory argument must be a directory, not a file.");

        List<Class<T>> results = new ArrayList<>(30);
        File[] files = directory.listFiles();
        if (files == null)
            return results;

        for (File file : files) {

            // make sure file is a jar file
            if (file.isDirectory() || !file.getName().endsWith(".jar"))
                continue;

            List<Class<T>> modules;

            // get module classes from jar file
            try {
                modules = getModule(moduleClass, file);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            results.addAll(modules);
        }

        return results;
    }

    /**
     * Get an {@code IJarModule} class from the
     * specified jar file.
     *
     * @param moduleClass  The class to search for.
     * @param file         The jar file to search in.
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public List<Class<T>> getModule(Class<T> moduleClass, File file) throws IOException {
        PreCon.notNull(file);
        PreCon.isValid(!file.isDirectory(), "file argument must be a file, not a directory.");
        PreCon.isValid(file.getName().endsWith(".jar"), "file argument must point to a .jar file.");

        List<Class<T>> moduleClasses = _moduleMap.get(file.getAbsolutePath());
        if (moduleClasses == null) {

            JarFile jarFile = new JarFile(file);

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
                if (moduleClass.isAssignableFrom(c)) {

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
                return new ArrayList<Class<T>>(moduleClasses);
            }

            // cache module class
            _moduleMap.put(file.getAbsolutePath(), moduleClasses);

            // add class names to prevent reloading
            _loadedClasses.addAll(classNames);
        }

        return moduleClasses;
    }

    /**
     * Get all {@code IJarModule} classes from jar files
     * in a directory and instantiate them.
     *
     * <p>Can only load implementations with an empty constructor.</p>
     *
     * @param moduleClass  The class to search for.
     * @param directory    The directory to search for jar files in.
     */
    public List<T> loadModules(Class<T> moduleClass, File directory) {
        PreCon.notNull(directory);
        PreCon.isValid(directory.exists(), "directory argument points to a location that does not exist.");
        PreCon.isValid(directory.isDirectory(), "directory argument must be a directory, not a file.");


        List<T> results = new ArrayList<>(5);
        File[] files = directory.listFiles();
        if (files == null)
            return results;

        for (File file : files) {

            if (file.isDirectory() || !file.getName().endsWith(".jar"))
                continue;

            List<T> modules;

            try {
                modules = loadModule(moduleClass, file);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            results.addAll(modules);
        }

        return results;
    }

    /**
     * Get an {@code IJarModule} class from the
     * specified jar file and instantiate it.
     *
     * <p>Can only load implementations with an empty constructor.</p>
     *
     * @param moduleClass  The class to search for.
     * @param file         The jar file to search in.
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public List<T> loadModule(Class<T> moduleClass, File file) throws IOException {
        PreCon.notNull(file);
        PreCon.isValid(!file.isDirectory(), "file argument must be a file, not a directory.");
        PreCon.isValid(file.getName().endsWith(".jar"), "file argument must point to a .jar file.");

        List<T> result = new ArrayList<>(5);

        List<Class<T>> moduleClasses = getModule(moduleClass, file);
        if (moduleClasses.isEmpty())
            return result;


        for (Class<T> clazz : moduleClasses) {
            // create instance of module
            T instance;

            try {
                instance = _moduleFactory.create(clazz);
            } catch (InstantiationException e) {
                e.printStackTrace();
                continue;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                continue;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                continue;
            }

            result.add(instance);
        }

        return result;
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
    public ModuleFactory<T> getDefaultModuleFactory() {
        return new ModuleFactory<T>() {
            @Override
            public <M extends T> M create(Class<M> clazz)
                    throws IllegalAccessException, InstantiationException {
                return clazz.newInstance();
            }
        };
    }

    /**
     * Module factory interface.
     *
     * @param <T>  Module type.
     */
    public interface ModuleFactory<T> {
        <M extends T> M create(Class<M> clazz)
                throws InstantiationException, IllegalAccessException, NoSuchMethodException;
    }

}
