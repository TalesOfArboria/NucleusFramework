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


package com.jcwhatever.nucleus.utils.file;

import com.jcwhatever.nucleus.utils.validate.IValidator;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
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
                                      @Nullable IValidator<File> fileValidator) {
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

        return absPath.substring(absBase.length() + 1);
    }

    /**
     * Get a text file from a class resource.
     *
     * @param cls            The class to get a resource stream from.
     * @param resourcePath   The path of the file within the class jar file.
     * @param charSet        The encoding type used by the text file.
     *
     * @return  Null if resource not found.
     *
     * @throws java.lang.IllegalArgumentException
     */
    @Nullable
    public static String scanTextFile(Class<?> cls, String resourcePath, Charset charSet) {
        return scanTextFile(cls, resourcePath, charSet, null);
    }

    /**
     * Get a text file from a class resource.
     *
     * @param cls            The class to get a resource stream from.
     * @param resourcePath   The path of the file within the class jar file.
     * @param charSet        The encoding type used by the text file.
     * @param lineValidator  Optional validator to use for each scanned line.
     *                       Returning false excludes the line from the result.
     *
     * @return  Null if resource not found.
     *
     * @throws java.lang.IllegalArgumentException
     */
    @Nullable
    public static String scanTextFile(Class<?> cls, String resourcePath,
                                      Charset charSet,
                                      @Nullable IValidator<String> lineValidator) {
        PreCon.notNull(cls);
        PreCon.notNullOrEmpty(resourcePath);
        PreCon.notNull(charSet);

        InputStream input = cls.getResourceAsStream(resourcePath);
        if (input == null)
            return null;

        String result = scanTextFile(input, charSet, 50, lineValidator);

        try {
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Get text file contents as a string.
     *
     * @param file           The file to scan.
     * @param charSet        The encoding type used by the text file.
     *
     * @return  Null if file not found.
     *
     * @throws java.lang.IllegalArgumentException
     */
    @Nullable
    public static String scanTextFile(File file, Charset charSet) {
        PreCon.notNull(file);
        PreCon.notNull(charSet);

        return scanTextFile(file, charSet, null);
    }

    /**
     * Get text file contents as a string.
     *
     * @param file           The file to scan.
     * @param charSet        The encoding type used by the text file.
     * @param lineValidator  Optional validator to use for each scanned line.
     *                       Returning false excludes the line from the result.
     *
     * @return  Null if file not found.
     *
     * @throws java.lang.IllegalArgumentException
     */
    @Nullable
    public static String scanTextFile(File file, Charset charSet,
                                      @Nullable IValidator<String> lineValidator) {
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

        String result = scanTextFile(input, charSet, (int)file.length(), lineValidator);

        try {
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Get text file contents as a string from a file contained in a
     * zip file.
     *
     * @param zipFile        The zip file that contains the text file.
     * @param fileName       The name and path of the text file.
     * @param charSet        The encoding type used by the text file.
     *
     * @return  Null if file not found or error reading the file.
     *
     * @throws java.lang.IllegalArgumentException
     */
    @Nullable
    public static String scanTextFile(ZipFile zipFile, String fileName, Charset charSet) {

        return scanTextFile(zipFile, fileName, charSet, null);
    }

    /**
     * Get text file contents as a string from a file contained in a
     * zip file.
     *
     * @param zipFile        The zip file that contains the text file.
     * @param fileName       The name and path of the text file.
     * @param charSet        The encoding type used by the text file.
     * @param lineValidator  Optional validator to use for each scanned line.
     *                       Returning false excludes the line from the result.
     *
     * @return  Null if file not found or error reading the file.
     *
     * @throws java.lang.IllegalArgumentException
     */
    @Nullable
    public static String scanTextFile(ZipFile zipFile, String fileName, Charset charSet,
                                      @Nullable IValidator<String> lineValidator) {
        PreCon.notNull(zipFile);
        PreCon.notNull(fileName);
        PreCon.notNull(charSet);

        ZipEntry entry = zipFile.getEntry(fileName);

        InputStream input = null;

        try {
            input = zipFile.getInputStream(entry);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (input == null)
            return null;

        String result = scanTextFile(input, charSet, (int)entry.getSize(), lineValidator);

        try {
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
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
    public static String scanTextFile(InputStream input, Charset charSet, int initialBufferSize) {
        PreCon.notNull(input);
        PreCon.notNull(charSet);

        return scanTextFile(input, charSet, initialBufferSize, null);
    }

    /**
     * Get text file contents from a stream.
     *
     * @param input              The input stream to scan
     * @param charSet            The encoding type used by the text file.
     * @param initialBufferSize  The initial size of the buffer.
     * @param lineValidator      Optional validator to use for each scanned line.
     *                           Returning false excludes the line from the result.
     *
     * @throws java.lang.IllegalArgumentException
     */
    public static String scanTextFile(InputStream input, Charset charSet,
                                      int initialBufferSize,
                                      @Nullable IValidator<String> lineValidator) {
        PreCon.notNull(input);
        PreCon.notNull(charSet);

        StringBuilder result = new StringBuilder(initialBufferSize);

        Scanner scanner = new Scanner(input, charSet.name());

        while (scanner.hasNextLine()) {

            String line = scanner.nextLine();

            if (lineValidator != null && !lineValidator.isValid(line))
                continue;

            if (result.length() > 0)
                result.append('\n');

            result.append(line);
        }

        return result.toString();
    }

    /**
     * Writes a text file.
     *
     * <p>Intended for writing a single line at a time. The line producer
     * is used to retrieve each line and to know when to stop.</p>
     *
     * <p>The method finishes when the line producer returns null</p>
     *
     * @param file           The file to write.
     * @param charset        The encoding to use.
     * @param lineProducer   The line producer.
     *
     * @return  The number of lines written.
     */
    public static int writeTextFile(File file, Charset charset, ITextLineProducer lineProducer) {
        PreCon.notNull(file);
        PreCon.notNull(charset);

        return writeTextFile(file, charset, -1, lineProducer);
    }

    /**
     * Writes a text file.
     *
     * <p>Intended for writing a single line at a time. The line producer
     * is used to retrieve each line.</p>
     *
     * <p>The method finishes when the line producer returns null or the total number
     * of lines written matches the specified total.</p>
     *
     * @param file           The file to write.
     * @param charset        The encoding to use.
     * @param totalLines     The total number of lines to write.
     * @param lineProducer   The line producer.
     *
     * @return  The number of lines written.
     */
    public static int writeTextFile(File file, Charset charset, int totalLines, ITextLineProducer lineProducer) {
        PreCon.notNull(file);
        PreCon.notNull(charset);

        OutputStreamWriter writer = null;
        int written = 0;

        try {
            FileOutputStream fileStream = new FileOutputStream(file);
            writer = new OutputStreamWriter(fileStream, charset.name());

            while (written < totalLines || totalLines == -1) {
                String line = lineProducer.nextLine();
                if (line == null)
                    break;

                writer.write(line);
                writer.write('\n');
                written++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return written;
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

    /**
     * Interface for getting the next line
     * to write to a text file.
     */
    public interface ITextLineProducer {

        /**
         * Get the next line.
         *
         * @return Null to stop writing.
         */
        @Nullable
        String nextLine();
    }
}

