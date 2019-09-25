/*
 * Copyright (c) 2019 Tim Langhammer
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package earth.eu.jtzipi.modules.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.filechooser.FileSystemView;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Utilities for I/O related methods.
 *
 * @author jTzipi
 */
public final class IOUtils {

    /**
     * Acronym unknown path name.
     */
    public static final String UNKNOWN_PATH_NAME = "<Unknown>";
    /**
     * Acronym unknown path suffix.
     */
    public static final String UNKNOWN_PATH_SUFFIX = "";
    /** Match all files path filer. */
    public static final DirectoryStream.Filter<Path> ACCEPT = path -> true;
    /** Math all filter. */
    public static final Predicate<Path> PATH_ACCEPT_ALL = path -> true;

    public static final Predicate<Path> PATH_ACCEPT_DIR = path -> Files.isReadable( path ) && Files.isDirectory( path );

    private static final Logger LOG = LoggerFactory.getLogger( "IOUtils" );
    /** File System View.*/
    private static final FileSystemView FSV = FileSystemView.getFileSystemView();
    private IOUtils() {
        throw new AssertionError();
    }


    /**
     * Format bytes.
     * @param bytes byte
     * @param si standard unit
     * @return formatted file size
     */
    public static String formatFileSize( long bytes, boolean si ) {
        // no formatting
        if( 0 >= bytes ) {
            return "0 B";
        }
        int unit = si ? 1000 : 1024;
        // if no need to format
        if( unit > bytes ) {
            return bytes + " B";
        }

        String unitSymbol = si ? "kMGT" : "KMGT";

        int exp = (int) (Math.log(bytes) / Math.log(unit));

        double ri = bytes / Math.pow(unit, exp);


        String pre = unitSymbol.charAt(exp - 1) +  ( si ? "" : "i" );
        return String.format("%.1f %sB", ri, pre);
    }

    /**
     * Return file path name denoting this path.
     * <p>
     *  This
     * </p>
     * @param path path to file
     * @return file name
     * @throws NullPointerException
     */
    public static String getPathDisplayName( final Path path ) {
        Objects.requireNonNull( path );
        return FSV.getSystemDisplayName( path.toFile() );
    }

    /**
     * Return description of path system dependent.
     * @param path path
     * @return description
     */
    public static String getPathTypeDescription( final Path path ) {
        return FSV.getSystemTypeDescription( path.toFile() );
    }

    /**
     * Try to load all sub path's of path p.
     * @param p path to lookup
     * @return list of path's or empty list if not readable or no dir
     * @throws NullPointerException if {@code p} is null
     * @see #lookupDir(Path, Predicate)
     */
    public static List<Path> lookupDir( final Path p ) {
        return IOUtils.lookupDir( p, null );
    }

    /**
     * Lookup path for sub path.
     * <p>
     *     Path should be a directory to make sense.
     * </p>
     * @param p path to lookup
     * @param pathPredicate filter
     * @return list of path
     * @throws NullPointerException {@code p} is {@code null}
     * @see #lookupDir(Path)
     */
    public static List<Path> lookupDir( final Path p, Predicate<Path> pathPredicate ) {
        Objects.requireNonNull( p );

        if( !Files.isReadable( p ) ) {

            return Collections.emptyList();
        }
        if( !Files.isDirectory( p ) ) {

            return Collections.emptyList();
        }

        DirectoryStream.Filter<Path> filter;
        if ( null != pathPredicate ) {
            filter = pathPredicate::test;
        } else {
            filter = ACCEPT;
        }
        List<Path> nodeL = new ArrayList<>();

        try ( final DirectoryStream<Path> ds = Files.newDirectoryStream( p, filter ) ) {

            for ( final Path path : ds ) {
                System.out.println( "Gadi : [" + path.toString() + "]" );
                nodeL.add( path );
            }

        } catch ( final IOException ioE ) {

            LOG.warn( "Can not read", ioE );
        }

        return nodeL;
    }

    /**
     * Return the file name of a file denoted by this path.
     * This is the part before the last dot (.).
     *
     * @param path the path to a file
     * @return the file name without the file ending or suffix
     * @throws IOException if {@code path} is not readable
     * @throws NullPointerException if {@code path} is null
     */
    public static String getPathName( final Path path ) throws IOException {
        Objects.requireNonNull( path );
        if ( !Files.isReadable( path ) ) {
            throw new IOException( "Path '" + path + "' is not readable" );
        }

        String[] temp = splitPathFileName(path);
        return 0 == temp.length ? UNKNOWN_PATH_NAME : temp[0];
    }

    /**
     * Read path name ignoring io error.
     * @param path path to read
     * @return name of file or {@linkplain #UNKNOWN_PATH_NAME}
     */
    public static String getPathNameSafe( final Path path ) {
        String name = UNKNOWN_PATH_NAME;
        try {
            name = getPathName(path);
        } catch (IOException ioE) {


            //
        }

        return name;
    }
    /**
     * File name suffix.
     * @param path path to file
     * @return suffix i.E. part of file name after last dot
     * @throws IOException if {@code path} is not readable
     * @throws  NullPointerException if {@code path} is null
     */
    public static String getPathSuffix(final Path path ) throws IOException {
        Objects.requireNonNull(path);
        // if not readable throw
        if ( !Files.isReadable( path ) ) {
            throw new IOException( "Path '" + path + "' is not readable" );
        }


        String[] temp = splitPathFileName(path);
        return 0 == temp.length ? UNKNOWN_PATH_SUFFIX : temp[1];
    }

    /**
     * Read file suffix for path ignoring io error.
     * @param path path
     * @return
     */
    public static String getPathSuffixSafe(final Path path) {
        String suffix = UNKNOWN_PATH_SUFFIX;
        try {
            suffix = getPathSuffix(path);
        } catch (IOException ioE) {

            // ignored

        }
        return suffix;
    }


    /**
     * Split file name of path into two parts 1) file name 2) file suffix.
     * @param path path
     * @return array with index 0 == file name 1 == file suffix (maybe '' if no suffix) or empty array if {@code path}
     * have no file name
     */
    private static String[] splitPathFileName( Path path ) {
        assert null != path : "Error : path is null";
        Path p = path.getFileName();

        if( null == p ) {

            return new String[0];
        }

        String fileName =  p.toString();

        int lastDot = fileName.lastIndexOf( "." );

        String[] ret = new String[2];
        ret[0] = lastDot > 0 ? fileName.substring(0, lastDot) : fileName;
        ret[1] = lastDot > 0 ? fileName.substring( lastDot ) : "";

        return ret;
    }
}
