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


package com.jcwhatever.bukkit.generic.utils;

import com.jcwhatever.bukkit.generic.utils.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.annotation.Nullable;

/**
 * File handling utilities.
 */
public final class FileUtils {

    private FileUtils() {}

    /**
     * Specifies how sub directories are traversed when
     * searching for script files.
     */
    public enum DirectoryTraversal {
        /**
         * Do not traverse sub directories.
         */
        NONE,
        /**
         * Traverse all sub directories.
         */
        RECURSIVE
    }

    /**
     * Get all non-directory files in a folder.
     *
     * @param folder     The folder to search for files in.
     * @param traversal  The directory traversal of the search.
     */
    public static List<File> getFiles(File folder, DirectoryTraversal traversal) {
        return getFiles(folder, traversal, null);
    }

    /**
     * Get all non-directory files in a folder.
     *
     * @param folder         The folder to search for files in.
     * @param traversal      The directory traversal of the search.
     * @param fileValidator  The validator used to validate files.
     */
    public static List<File> getFiles(File folder,
                                      DirectoryTraversal traversal,
                                      @Nullable IEntryValidator<File> fileValidator) {
        PreCon.notNull(folder);
        PreCon.isValid(folder.isDirectory(), "folder argument must be a folder.");
        PreCon.notNull(traversal);

        File[] files = folder.listFiles();
        if (files == null)
            return new ArrayList<>(0);

        List<File> results = new ArrayList<File>(files.length);

        for (File file : files) {

            if (file.isDirectory() && traversal == DirectoryTraversal.RECURSIVE) {

                List<File> traversed = getFiles(file, DirectoryTraversal.RECURSIVE, fileValidator);
                results.addAll(traversed);
            }
            else if (!file.isDirectory()) {

                if (fileValidator != null && !fileValidator.isValid(file))
                    continue;

                results.add(file);
            }
        }

        return results;
    }

    /**
     * Get the extension of a file, not including a dot.
     *
     * @param file  The file to check.
     */
    public static String getFileExtension(File file) {
        PreCon.notNull(file);

        return getFileExtension(file.getName());
    }

    /**
     * Get the extension of a file name, not including a dot.
     *
     * @param fileName  The file name to check.
     */
    public static String getFileExtension(String fileName) {
        PreCon.notNull(fileName);

        int i = fileName.lastIndexOf('.');
        if (i != -1) {
            return fileName.substring(i + 1);
        }

        return "";
    }

    /**
     * Get the name of a file not including the extension.
     *
     * @param file The file to check.
     */
    public static String getNameWithoutExtension(File file) {
        PreCon.notNull(file);

        return getNameWithoutExtension(file.getName());
    }

    /**
     * Get the name of a file not including the extension.
     *
     * @param fileName  The file name to check.
     */
    public static String getNameWithoutExtension(String fileName) {
        PreCon.notNull(fileName);

        int i = fileName.lastIndexOf('.');
        if (i != -1) {
            return fileName.substring(0, i);
        }

        return fileName;
    }

    /**
     * Get the relative path of a file using a base path
     * to specify the absolute portion.
     *
     * @param base  The absolute portion of the path.
     * @param path  The absolute path to convert to a relative path.
     */
    public static String getRelative(File base, File path) {
        String absBase = base.getAbsolutePath();
        String absPath = path.getAbsolutePath();

        if (absPath.indexOf(absBase) != 0)
            return absPath;

        return absPath.substring(absBase.length());
    }

    /**
     * Get a text file from a class resource.
     *
     * @param cls           The class to get a resource stream from.
     * @param resourcePath  The path of the file within the class jar file.
     * @param charSet       The encoding type used by the text file.
     *
     * @throws java.lang.IllegalArgumentException
     */
    @Nullable
    public static String scanTextFile(Class<?> cls, String resourcePath, Charset charSet) {
        PreCon.notNull(cls);
        PreCon.notNullOrEmpty(resourcePath);
        PreCon.notNull(charSet);

        InputStream input = cls.getResourceAsStream(resourcePath);
        if (input == null)
            return null;

        return scanTextFile(input, charSet, 50);
    }


    /**
     * Get text file contents as a string.
     *
     * @param file      The file to scan.
     * @param charSet   The encoding type used by the text file.
     *
     * @throws java.lang.IllegalArgumentException
     */
    @Nullable
    public static String scanTextFile(File file, Charset charSet) {
        PreCon.notNull(file);
        PreCon.notNull(charSet);

        InputStream input = null;

        try {
            input = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (input == null)
            return null;

        return scanTextFile(input, charSet, (int)file.length());
    }

    /**
     * Get text file contents from a stream.
     *
     * @param input              The input stream to scan
     * @param charSet            The encoding type used by the text file.
     * @param initialBufferSize  The initial size of the buffer.
     *
     * @throws java.lang.IllegalArgumentException
     */
    @Nullable
    public static String scanTextFile(InputStream input, Charset charSet, int initialBufferSize) {
        PreCon.notNull(input);
        PreCon.notNull(charSet);

        StringBuilder result = new StringBuilder(initialBufferSize);

        Scanner scanner = new Scanner(input, charSet.name());

        while (scanner.hasNextLine()) {
            result.append(scanner.nextLine());
            result.append('\n');
        }

        try {
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }

    /**
     * Extract a class resource into a file.
     *
     * @param cls           The class to get a resource stream from.
     * @param resourcePath  The path of the file within the class jar file.
     * @param outDir        The output directory.
     *
     * @throws java.lang.RuntimeException
     */
    @Nullable
    public static File extractResource(Class<?> cls, String resourcePath, File outDir) {
        PreCon.notNull(cls);
        PreCon.notNull(resourcePath);
        PreCon.notNull(outDir);

        if (!outDir.exists() && !outDir.mkdirs())
            throw new RuntimeException("Failed to create output folder(s).");

        File outFile = new File(outDir, getFilename(resourcePath));
        if (outFile.exists())
            return outFile;

        InputStream input = cls.getResourceAsStream("/res/" + resourcePath);
        if (input == null)
            return null;

        FileOutputStream output = null;
        try {

            if (!outFile.createNewFile()) {
                return null;
            }

            output = new FileOutputStream(outFile);

            byte[] buffer = new byte[4096];

            int read;
            while ((read = input.read(buffer)) > 0) {
                output.write(buffer, 0, read);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {

            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (output != null)
                    output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return outFile;
    }

    private static String getFilename(String resource) {
        String[] components = TextUtils.PATTERN_FILEPATH_SLASH.split(resource);
        return components[components.length - 1];
    }
}

