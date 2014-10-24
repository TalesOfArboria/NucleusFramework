package com.jcwhatever.bukkit.generic.modules;

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
 * Aids in loading jar files that implement {@code IJarModule}.
 *
 * @author JC The Pants
 *
 */
public class JarModuleLoader<T extends IJarModule> {

    private Set<String> _loadedClasses = new HashSet<>(1000);
    private Map<String, List<Class<T>>> _moduleMap = new HashMap<>(100);

    /**
     * Get all {@code IJarModule} classes from jar files
     * in a directory.
     *
     * @param directory
     * @return
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

            if (file.isDirectory() || !file.getName().endsWith(".jar"))
                continue;

            List<Class<T>> modules;

            try {
                modules = getModule(moduleClass, file);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            if (modules != null) {
                results.addAll(modules);
            }
        }

        return results;
    }

    /**
     * Get an {@code IJarModule} class from the
     * specified jar file.
     *
     * @param file
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    public List<Class<T>> getModule(Class<T> moduleClass, File file) throws IOException {
        PreCon.notNull(file);
        PreCon.isValid(!file.isDirectory(), "file argument must be a file, not a directory.");
        PreCon.isValid(file.getName().endsWith(".jar"), "file argument must point to a .jar file.");

        List<Class<T>> moduleClasses = _moduleMap.get(file.getAbsolutePath());
        if (moduleClasses == null) {

            moduleClasses = new ArrayList<>(50);

            JarFile jarFile = new JarFile(file);

            Enumeration<JarEntry> entries = jarFile.entries();

            URL[] urls = { new URL("jar:file:" + file + "!/") };

            URLClassLoader classLoader = URLClassLoader.newInstance(urls, getClass().getClassLoader());
            Set<String> classNames = new HashSet<>(50);

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();

                if (entry.isDirectory() || !entry.getName().endsWith(".class"))
                    continue;

                String className = entry.getName().substring(0,  entry.getName().length() - 6);
                className = className.replace('/', '.');

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

                if (moduleClass.isAssignableFrom(c)) {
                    moduleClasses.add((Class<T>)c);
                }
            }

            jarFile.close();


            // module class not found
            if (moduleClasses.isEmpty()) {
                return new ArrayList<Class<T>>(moduleClasses);
            }

            // cache module class
            _moduleMap.put(file.getAbsolutePath(), moduleClasses);

            // cache class names
            _loadedClasses.addAll(classNames);
        }

        return moduleClasses;
    }

    /**
     * Get all {@code IJarModule} classes from jar files
     * in a directory and instantiate them.
     *
     * @param directory
     * @return
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

            if (modules != null) {
                results.addAll(modules);
            }
        }

        return results;
    }


    /**
     * Get an {@code IJarModule} class from the
     * specified jar file and instantiate it.
     *
     * @param file
     * @return
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
                instance = clazz.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
                continue;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                continue;
            }

            result.add(instance);

        }

        return result;
    }

}
