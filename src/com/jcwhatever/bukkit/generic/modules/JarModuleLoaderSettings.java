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
    private DirectoryTraversal _directoryTraversal;

    private IModuleInfoFactory<T> _moduleInfoFactory;
    private IModuleFactory<T> _moduleFactory;
    private IEntryValidator<String> _classValidator;
    private IEntryValidator<Class<T>> _typeValidator;
    private IEntryValidator<JarFile> _jarValidator;

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
     *
     * <p>Required.</p>
     *
     * @return  Null if the value has not been set yet.
     */
    @Nullable
    public DirectoryTraversal getDirectoryTraversal() {
        return _directoryTraversal;
    }

    /**
     * Get the module info factory used to retrieve and
     * instantiate info about the module.
     *
     * @return  Null if the value has not been set yet.
     */
    public IModuleInfoFactory<T> getModuleInfoFactory() {
        return _moduleInfoFactory;
    }

    /**
     * Get the module factory used to instantiate the module
     * instances.
     *
     * @return  Null to use the default module factory.
     */
    @Nullable
    public IModuleFactory<T> getModuleFactory() {
        return _moduleFactory;
    }

    /**
     * Get the validator used to validate a class name
     * before it is loaded.
     *
     * @return  Null to use the default class validator.
     */
    public IEntryValidator<String> getClassValidator() {
        return _classValidator;
    }

    /**
     * Get the validator used to validate a module class.
     *
     * @return  Null to use the default type validator.
     */
    @Nullable
    public IEntryValidator<Class<T>> getTypeValidator() {
        return _typeValidator;
    }

    /**
     * Get the validator used to validate jar files
     * found in the module folder.
     *
     * @return  Null to use the default jar validator.
     */
    @Nullable
    public IEntryValidator<JarFile> getJarValidator() {
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

    private void checkSealed() {
        if (_isSealed)
            throw new IllegalStateException("Cannot modify JarModuleLoaderSettings after it has been used.");
    }

    void seal() {
        _isSealed = true;

        if (_moduleFolder == null)
            throw new IllegalStateException("Module folder is required but not set.");

        if (_directoryTraversal == null)
            throw new IllegalStateException("Directory Traversal is required but not set.");

    }
}
