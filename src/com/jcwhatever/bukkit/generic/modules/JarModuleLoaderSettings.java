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

import com.jcwhatever.bukkit.generic.utils.IEntryValidator;
import com.jcwhatever.bukkit.generic.utils.FileUtils.DirectoryTraversal;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import java.io.File;
import java.util.jar.JarFile;
import javax.annotation.Nullable;

/**
 * Settings for an {@link JarModuleLoader} instance.
 *
 * @param <T>  The module type.
 */
public class JarModuleLoaderSettings<T> {

    private File _moduleFolder;
    private DirectoryTraversal _directoryTraversal = DirectoryTraversal.NONE;
    private ClassLoadMethod _classLoadMethod = ClassLoadMethod.SEARCH;

    private IModuleInfoFactory<T> _moduleInfoFactory;
    private IModuleFactory<T> _moduleFactory;
    private IEntryValidator<String> _classValidator;
    private IEntryValidator<Class<T>> _typeValidator;
    private IEntryValidator<JarFile> _jarValidator;
    private IClassNameFactory<T> _classNameFactory;

    private boolean _isSealed;

    /**
     * Get the module folder.
     *
     * <p>Required.</p>
     *
     * @return  Null if the value has not been set yet.
     */
    @Nullable
    public File getModuleFolder() {
        return _moduleFolder;
    }

    /**
     * Get the directory traversal used to find jar files.
     */
    public DirectoryTraversal getDirectoryTraversal() {
        return _directoryTraversal;
    }

    /**
     * Get the method used to load classes from jar files.
     */
    public ClassLoadMethod getClassLoadMethod() {
        return _classLoadMethod;
    }

    /**
     * Get the factory used to provide the class name to
     * load from a jar file when the {@code ClassLoadMethod}
     * is set to {@code DIRECT}.
     */
    @Nullable
    public IClassNameFactory<T> getClassNameFactory() {
        return _classNameFactory;
    }

    /**
     * Get the module info factory used to retrieve and
     * instantiate info about the module.
     */
    public IModuleInfoFactory<T> getModuleInfoFactory() {
        if (_moduleInfoFactory == null)
            _moduleInfoFactory = getDefaultModuleInfoFactory();

        return _moduleInfoFactory;
    }

    /**
     * Get the module factory used to instantiate the module
     * instances.
     */
    public IModuleFactory<T> getModuleFactory() {
        if (_moduleFactory == null)
            _moduleFactory = getDefaultModuleFactory();

        return _moduleFactory;
    }

    /**
     * Get the validator used to validate a class name
     * before it is loaded.
     */
    public IEntryValidator<String> getClassValidator() {
        if (_classValidator == null)
            _classValidator = getDefaultClassValidator();

        return _classValidator;
    }

    /**
     * Get the validator used to validate a module class.
     */
    public IEntryValidator<Class<T>> getTypeValidator() {
        if (_typeValidator == null)
            _typeValidator = getDefaultTypeValidator();

        return _typeValidator;
    }

    /**
     * Get the validator used to validate jar files
     * found in the module folder.
     */
    public IEntryValidator<JarFile> getJarValidator() {
        if (_jarValidator == null)
            _jarValidator = getDefaultJarValidator();

        return _jarValidator;
    }

    /**
     * Set the module folder to search for jar files in.
     *
     * <p>Required setting.</p>
     *
     * @param moduleFolder  The module folder.
     */
    public void setModuleFolder(File moduleFolder) {
        PreCon.notNull(moduleFolder);
        PreCon.isValid(moduleFolder.exists(), "moduleFolder must exist.");
        PreCon.isValid(moduleFolder.isDirectory(), "moduleFolder argument must be a folder.");

        checkSealed();

        _moduleFolder = moduleFolder;
    }

    /**
     * Set the directory traversal used while searching for
     * jar files.
     *
     * <p>Required setting.</p>
     *
     * @param directoryTraversal  The directory traversal type.
     */
    public void setDirectoryTraversal(DirectoryTraversal directoryTraversal) {
        PreCon.notNull(directoryTraversal);

        checkSealed();

        _directoryTraversal = directoryTraversal;
    }

    /**
     * Set how classes are loaded from jar files.
     *
     * <p>Required setting.</p>
     *
     * @param loadMethod  The method to use.
     */
    public void setClassLoadMethod(ClassLoadMethod loadMethod) {
        PreCon.notNull(loadMethod);

        checkSealed();

        _classLoadMethod = loadMethod;
    }

    /**
     * Set the factory that provides the class name to load
     * from a jar file when the {@code ClassLoadMethod} is set
     * to {@code DIRECT}.
     *
     * @param classNameFactory  The class name factory.
     */
    public void setClassNameFactory(IClassNameFactory<T> classNameFactory) {
        PreCon.notNull(classNameFactory);

        checkSealed();

        _classNameFactory = classNameFactory;
    }

    /**
     * Set the module info factory used to read info about
     * the module and instantiate an info object.
     *
     * @param moduleInfoFactory  The module info factory. Null to use default.
     */
    public void setModuleInfoFactory(@Nullable IModuleInfoFactory<T> moduleInfoFactory) {
        checkSealed();

        _moduleInfoFactory = moduleInfoFactory;
    }

    /**
     * Set the module factory used to instantiate modules.
     *
     * @param moduleFactory  The module factory. Null to use default.
     */
    public void setModuleFactory(@Nullable IModuleFactory<T> moduleFactory) {
        checkSealed();

        _moduleFactory = moduleFactory;
    }

    /**
     * Set the validator used to validate class names
     * before they are loaded.
     *
     * @param classValidator  The class validator. Null to use default.
     */
    public void setClassValidator(@Nullable IEntryValidator<String> classValidator) {
        checkSealed();

        _classValidator = classValidator;
    }

    /**
     * Set the validator used to validate module types.
     *
     * <p>Note: The type is pre-validated by the {@code JarModuleLoader}
     * against the expected module type.</p>
     *
     * @param typeValidator  The type validator. Null to use default.
     */
    public void setTypeValidator(@Nullable IEntryValidator<Class<T>> typeValidator) {
        checkSealed();

        _typeValidator = typeValidator;
    }

    /**
     * Set the validator used to validate jar files in the modules folder.
     *
     * <p>Note: The type is pre-validated for the ".jar" extension
     * by the {@code JarModuleLoader}</p>
     *
     * @param jarValidator  The jar validator. Null to use default.
     */
    public void setJarValidator(@Nullable IEntryValidator<JarFile> jarValidator) {
        checkSealed();

        _jarValidator = jarValidator;
    }


    /**
     * Get a new instance of the default class validator.
     */
    protected IEntryValidator<String> getDefaultClassValidator() {

        return new IEntryValidator<String>() {
            @Override
            public boolean isValid(String entry) {
                return true;
            }
        };
    }

    /**
     * Get a new instance of the default type validator.
     */
    protected IEntryValidator<Class<T>> getDefaultTypeValidator() {

        return new IEntryValidator<Class<T>>() {
            @Override
            public boolean isValid(Class<T> entry) {
                return true;
            }
        };
    }

    /**
     * Get a new instance of the default jar validator.
     */
    protected IEntryValidator<JarFile> getDefaultJarValidator() {

        return new IEntryValidator<JarFile>() {
            @Override
            public boolean isValid(JarFile entry) {
                return true;
            }
        };
    }

    /**
     * Get a new instance of the default module factory.
     */
    protected IModuleFactory<T> getDefaultModuleFactory() {

        return new IModuleFactory<T>() {
            @Override
            public T create(Class<T> clazz, JarModuleLoader<T> loader)
                    throws IllegalAccessException, InstantiationException {
                return clazz.newInstance();
            }
        };
    }

    /**
     * Get a new instance of the default module info factory.
     */
    protected IModuleInfoFactory<T> getDefaultModuleInfoFactory() {

        return new IModuleInfoFactory<T>() {
            @Override
            public IModuleInfo create(T module, JarModuleLoader<T> loader) {
                return new SimpleModuleInfo(module.getClass().getCanonicalName());
            }
        };
    }

    private void checkSealed() {
        if (_isSealed)
            throw new IllegalStateException("Cannot modify JarModuleLoaderSettings after it has been used.");
    }

    void seal() {
        _isSealed = true;

        if (getModuleFolder() == null)
            throw new IllegalStateException("Module folder is required but not set.");

        if (getClassLoadMethod() == ClassLoadMethod.DIRECT && getClassNameFactory() == null)
            throw new IllegalStateException("A class name factory is required when the class " +
                    "load method is set to DIRECT.");
    }

    /**
     * Represents an object that provides class names based
     * on a provided jar file.
     */
    public static interface IClassNameFactory<T> {

        /**
         * Get the name of the class that should be loaded
         * from the specified file.
         *
         * @param jarFile  The jar file to get a class name for.
         * @param loader   The module loader that needs the class name.
         *
         * @return  Null to cancel loading of jar file.
         */
        @Nullable
        String getClassName(JarFile jarFile, JarModuleLoader<T> loader);
    }

}
