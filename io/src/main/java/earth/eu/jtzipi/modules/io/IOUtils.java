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

import javafx.scene.image.Image;
import javafx.scene.text.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.filechooser.FileSystemView;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
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

    public static final double MIN_FONT_SIZE = 7D;

    public static final double DEFAULT_FONT_SIZE = 14D;
    /**
     * Type image map.
     */
    public static final Map<String, String> IMG_TYPE_MAP = new HashMap<>();

    static {
        IMG_TYPE_MAP.put( ".jpg", "JPEG Image" );
    }

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
     * Load image for path.
     * @param path path to image
     * @return image
     * @throws IOException if loading failed
     * @throws NullPointerException if {@code path} is null
     */
    public static Image loadImage( final Path path) throws IOException {
        Objects.requireNonNull(path);

        try (InputStream ips = Files.newInputStream(path)) {
            Image img = new Image(ips);

            return img;
        } catch (final IOException ioE) {
            LOG.error("Can not read Pic '" + path + "' ");
            throw ioE;
        }
    }




    /**
     * Load a font from path.
     * @param path path
     * @param size size
     * @return Font object
     * @throws IOException io loading font or {@code path} is not readable
     *
     */
    public static Font loadFont( final Path path, final double size ) throws IOException  {
        Objects.requireNonNull(path);
        // Error
    if( !Files.isReadable( path )) {
        throw new IOException( "Path '"+path+"' is not readable" );
    }
        // Too small
        if( size < MIN_FONT_SIZE) {
            throw new IllegalArgumentException("Font size < " + MIN_FONT_SIZE );
        }
        // try
        Font font;
        try( InputStream io = Files.newInputStream(path)) {

            font = Font.loadFont(io, size);
        } catch (final IOException ioE) {
            LOG.error("Can not read font for path '" + path + "'" );
            throw ioE;
        }

        return font;

    }

    /**
     *
     * @param path
     * @param size
     * @return
     */
    public static Font loadFontSafe( final Path path,  double size ) {
        Objects.requireNonNull( path );
        if( size < MIN_FONT_SIZE ) {
            size = MIN_FONT_SIZE;
        }
        Font font;
        try {
            font = IOUtils.loadFont( path, size );
        } catch ( final IOException ioe ) {
            font = Font.getDefault();
        }
        return font;
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

    public static Path getHomeDir() {
        return Paths.get(System.getProperty( "user.home" ));
    }

    public static Path getProgramDir() {
        return Paths.get( System.getProperty( "user.dir" ) );
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
    if( null == path ) {
        return UNKNOWN_PATH_NAME;
    }
        String[] temp = split(path.getFileName().toString());
        return 0 == temp.length ? UNKNOWN_PATH_NAME : temp[0];
    }

    /**
     * Return 'raw' path name that is part of file name before first dot ({@literal .}).
     * @param path path
     * @return part of {@code path} name before '.' or {@linkplain #UNKNOWN_PATH_NAME}
     * @throws NullPointerException if {@code path} is null
     */
    public static String getPathName( final String path ) {
        Objects.requireNonNull( path );

        String[] temp = split(path);
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
        // Path without name
        if( null == path.getFileName() ) {
            return UNKNOWN_PATH_SUFFIX;
        }

        String[] temp = split(path.toString());
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
     * Return whether a path maybe image.
     * <p>
     *     Warning: this only check the file suffix against a map of known java supported image files.
     * </p>
     * @param path path to image
     * @return {@code false} if {@code path} seem to be no image or is null
     */
    public static boolean isImage( final Path path )  {
        String su = getPathSuffixSafe( path );
        // LOG.warn( "Suffix " + su );
        return null == path ? false : IMG_TYPE_MAP.containsKey( su.toLowerCase() );
    }
    private static String[] split( String fileName ) {
        assert null != fileName : "File name is null";

        int lastDot = fileName.lastIndexOf( "." );

        String[] ret = new String[2];
        ret[0] = lastDot > 0 ? fileName.substring(0, lastDot) : fileName;
        ret[1] = lastDot > 0 ? fileName.substring( lastDot ) : "";

        return ret;
    }
}
