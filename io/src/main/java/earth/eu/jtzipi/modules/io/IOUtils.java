/*
 * Copyright (c) 2021 Tim Langhammer
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

import earth.eu.jtzipi.modules.io.image.ImageType;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import org.slf4j.LoggerFactory;

import javax.swing.filechooser.FileSystemView;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Utilities for I/O related methods.
 *
 * @author jTzipi
 */
public final class IOUtils {

    /**
     * Return value for file size of not readable file.
     */
    public static final long FILE_NOT_READABLE = -5L;
    /**
     * Accept hidden files.
     */
    public static final Predicate<Path> PATH_ACCEPT_HIDDEN = path -> {
        try {
            return Files.isHidden( path );
        } catch ( IOException e ) {
            return false;
        }
    };
    /**
     * File size for a file not readable.
     */
    public static final long FILE_SIZE_UNKNOWN = -1L;
    /**
     * Minimal font size.
     */
    public static final double MIN_FONT_SIZE = 7D;

    /**
     * Type image map.
     */
    public static final Map<String, String> IMG_TYPE_MAP = new HashMap<>();
    /**
     * Font map.
     */
    public static final Map<String, String> TYPE_FON_MAP = new HashMap<>();
    /**
     * Match all files path filer.
     */
    public static final DirectoryStream.Filter<Path> ACCEPT = path -> true;
    /**
     * Math all filter.
     */
    public static final Predicate<Path> PATH_ACCEPT_ALL = path -> true;
    /**
     * Accept dirs .
     */
    public static final Predicate<Path> PATH_ACCEPT_DIR = path -> Files.isReadable( path ) && Files.isDirectory( path );
    /**
     * Acronym unknown path name.
     */
    private static final String UNKNOWN_PATH_NAME = "<Unknown>";
    /**
     * Acronym unknown path suffix.
     */
    private static final String UNKNOWN_PATH_SUFFIX = "";
    /**
     * Accept font files.
     */
    public static final Predicate<Path> PATH_ACCEPT_FONT = IOUtils::isFont;
    /**
     * Accept image files.
     */
    public static final Predicate<Path> PATH_ACCEPT_IMAGE = IOUtils::isImage;
    /**
     * File System View.
     */
    private static final FileSystemView FSV = FileSystemView.getFileSystemView();
    // for creating zip archives
    private static final Map<String, Boolean> ZIP_FS_MAP = Collections.singletonMap( "create", true );

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger( "IOUtils" );

    // TODO: use MediaType
    static {
        IMG_TYPE_MAP.put( ".jpg", "JPEG Image" );


    }

    private IOUtils() {
        throw new AssertionError();
    }

    /**
     * Load JavaFX image for path.
     *
     * @param path path to image
     * @return image
     * @throws IOException          if loading failed
     * @throws NullPointerException if {@code path} is null
     */
    public static Image loadImage( final Path path ) throws IOException {
        Objects.requireNonNull( path );

        try ( final InputStream ips = Files.newInputStream( path ) ) {
            final Image img = new Image( ips );

            return img;
        } catch ( final IOException ioE ) {
            LOG.warn( "Can not read Pic '" + path + "' " );
            throw ioE;
        }
    }

    /**
     * load image from path.
     *
     * @param path                    path to image
     * @param width                   pref width to
     * @param height                  pref height of image
     * @param preserveAspectRatioProp preserve aspect ratio
     * @param smoothProp              smooth
     * @return Image
     * @throws IOException fail
     */
    public static Image loadImage( final Path path, final double width, final double height, final boolean preserveAspectRatioProp, final boolean smoothProp ) throws IOException {
        try ( final InputStream fis = Files.newInputStream( path ) ) {
            return new Image( fis, width, height, preserveAspectRatioProp, smoothProp );
        } catch ( final IOException ioE ) {
            LOG.warn( "Can't read image for path " + path + "'" );
            throw ioE;
        }
    }

    /**
     * Get file size for path safe.
     *
     * @param path path
     * @return size or {@linkplain #FILE_SIZE_UNKNOWN}
     * @throws NullPointerException if
     */
    public static long getFileSizeSafe( final Path path ) {
        Objects.requireNonNull( path );
        long ret;

        try {
            ret = Files.size( path );
        } catch ( final IOException e ) {
            ret = FILE_SIZE_UNKNOWN;
        }
        return ret;
    }


    /**
     * Try to load image.
     *
     * @param path path
     * @return image if loaded or empty
     * @throws NullPointerException if {@code path} is null
     */
    public static Optional<Image> loadImageSafe( final Path path ) {

        try {
            final Image img = loadImage( path );
            return Optional.of( img );
        } catch ( final IOException e ) {
            return Optional.empty();
        }

    }

    /**
     * Load a JavaFX font from path.
     *
     * @param path path
     * @param size size
     * @return Font object
     * @throws IOException          io loading font or {@code path} is not readable
     * @throws NullPointerException if {@code path} is null
     */
    public static Font loadFont( final Path path, final double size ) throws IOException {
        Objects.requireNonNull( path );
        // Error
        if ( !Files.isReadable( path ) ) {
            throw new IOException( "Path '" + path + "' is not readable" );
        }
        // Too small
        if ( size < MIN_FONT_SIZE ) {
            throw new IllegalArgumentException( "Font size < " + MIN_FONT_SIZE );
        }
        // try
        final Font font;
        try ( final InputStream io = Files.newInputStream( path ) ) {

            font = Font.loadFont( io, size );
        } catch ( final IOException ioE ) {
            LOG.error( "Can not read font for path '" + path + "'" );
            throw ioE;
        }

        return font;

    }

    /**
     * Try to load a JavaFX font or return default system font.
     *
     * @param path path to font
     * @param size font size
     * @return font or system default
     * @throws NullPointerException if {@code path} is null
     */
    public static Font loadFontSafe( final Path path, double size ) {
        Objects.requireNonNull( path );
        if ( size < MIN_FONT_SIZE ) {
            size = MIN_FONT_SIZE;
        }
        Font font;
        try {
            font = loadFont( path, size );
        } catch ( final IOException ioe ) {
            font = Font.getDefault();
        }
        return font;
    }

    /**
     * Format bytes.
     *
     * @param bytes byte
     * @param si    standard unit
     * @return formatted file size
     */
    public static String formatFileSize( final long bytes, final boolean si ) {
        // no formatting
        if ( 0 >= bytes ) {
            return "0 B";
        }
        final int unit = si ? 1000 : 1024;
        // if no need to format
        if ( unit > bytes ) {
            return bytes + " B";
        }

        final String unitSymbol = si ? "kMGT" : "KMGT";

        final int exp = ( int ) ( Math.log( bytes ) / Math.log( unit ) );

        final double ri = bytes / Math.pow( unit, exp );


        final String pre = unitSymbol.charAt( exp - 1 ) + ( si ? "" : "i" );
        return String.format( "%.1f %sB", ri, pre );
    }

    /**
     * Return User home dir.
     *
     * @return user home
     */
    public static Path getHomeDir() {
        return Paths.get( System.getProperty( "user.home" ) );
    }

    /**
     * Return application dir.
     *
     * @return application dir
     */
    public static Path getProgramDir() {
        return Paths.get( System.getProperty( "user.dir" ) );
    }

    /**
     * Return file path name denoting this path.
     * <p>
     * This
     * </p>
     *
     * @param path path to file
     * @return file name
     * @throws NullPointerException if {@code path} null
     */
    public static String getPathDisplayName( final Path path ) {
        Objects.requireNonNull( path );
        return FSV.getSystemDisplayName( path.toFile() );
    }

    /**
     * Return system file icon.
     *
     * @param path path to file
     * @return icon
     * @throws NullPointerException if {@code path}
     */
    public static javax.swing.Icon getPathSystemIcon( final Path path ) {
        Objects.requireNonNull( path );
        return FSV.getSystemIcon( path.toFile() );
    }

    /**
     * Return description of path system dependent.
     *
     * @param path path
     * @return description
     */
    public static String getPathTypeDescription( final Path path ) {
        Objects.requireNonNull( path );
        return FSV.getSystemTypeDescription( path.toFile() );
    }

    /**
     * Stream zip dir.
     *
     * @param zipRoot    path
     * @param zipDirPath relative zip path
     * @return zip
     */
    public static List<Path> streamZip( final Path zipRoot, final Path zipDirPath ) {

        final FileSystem zfs = ZipUtils.getZipFilesystem( zipRoot );
        if ( null == zfs ) {
            return Collections.emptyList();
        }
        List<Path> pL;
        try {
            pL = Files.list( zfs.getPath( zipDirPath.toString() ) ).collect( toList() );
        } catch ( final IOException e ) {

            pL = Collections.emptyList();
        }
        return pL;
    }

    /**
     * Stream content of a directory and return entries as list.
     *
     * @param p path to dir
     * @return list of path's
     * @throws NullPointerException if
     */
    public static List<Path> streamDir( final Path p ) {
        Objects.requireNonNull( p, "Path must not null" );
        if ( !Files.isReadable( p ) ) {
            LOG.warn( "Path '" + p + "' is not readable" );
            return Collections.emptyList();
        }
        if ( !Files.isDirectory( p ) ) {

            throw new IllegalArgumentException( "You have to specify a directory" );
        }

        List<Path> pL;
        try ( final Stream<Path> ps = Files.list( p ) ) {
            pL = ps.collect( toList() );
        } catch ( final IOException ioE ) {
            LOG.warn( "IO E while streaming", ioE );
            pL = Collections.emptyList();
        }

        return pL;
    }

    /**
     * Try to load all sub path's of path p.
     *
     * @param p path to lookup
     * @return list of path's or empty list if not readable or no dir
     * @throws NullPointerException if {@code p} is null
     * @see #lookupDir(Path, Predicate)
     */
    public static List<Path> lookupDir( final Path p ) {
        return lookupDir( p, null );
    }

    /**
     * Lookup path for sub path.
     * <p>
     * Path should be a directory to make sense.
     * </p>
     *
     * @param p             path to lookup
     * @param pathPredicate filter
     * @return list of path
     * @throws NullPointerException {@code p} is {@code null}
     * @see #lookupDir(Path)
     */
    public static List<Path> lookupDir( final Path p, final Predicate<? super Path> pathPredicate ) {
        Objects.requireNonNull( p );

        if ( !Files.isReadable( p ) ) {

            return Collections.emptyList();
        }
        if ( !Files.isDirectory( p ) ) {

            return Collections.emptyList();
        }

        final DirectoryStream.Filter<Path> filter;
        if ( null != pathPredicate ) {
            filter = pathPredicate::test;
        } else {
            filter = ACCEPT;
        }
        final List<Path> nodeL = new ArrayList<>();

        try ( final DirectoryStream<Path> ds = Files.newDirectoryStream( p, filter ) ) {

            for ( final Path path : ds ) {
                System.out.println( "Gadi : [" + path + "]" );
                nodeL.add( path );
            }

        } catch ( final IOException ioE ) {

            LOG.warn( "Can not read", ioE );
        }

        return nodeL;
    }

    /**
     * Return all sub directories.
     *
     * @param path path to dir
     * @return list of subdirectories or empty list
     * @throws NullPointerException if {@code path} is null
     */
    public static List<Path> getSubDirsOf( final Path path ) {

        return lookupDir( path, PATH_ACCEPT_DIR );
    }

    /**
     * Return image type.
     *
     * @param path path to image
     * @return type if found or
     * @throws IOException          if path can not be read
     * @throws NullPointerException if {@code path} is null
     */
    public static ImageType determineImageType( final Path path ) throws IOException {
        Objects.requireNonNull( path );
        final byte[] code = readBytes( path, 0, 50 );
        ImageType imageType = ImageType.UNKNOWN;
        for ( final FileSig fileSig : FileSig.values() ) {

            if ( fileSig.type() == FileSig.Type.IMAGE ) {

                if ( fileSig.match( code ) ) {
                    imageType = ImageType.of( fileSig );
                    break;
                }
            }
        }

        return imageType;
    }

    /**
     * Read
     *
     * @param path
     * @param from position from &ge;0
     * @param until position until &gt;0
     * @return bytes read
     * @throws IOException if read fail
     */
    public static byte[] readBytes( final Path path, final int from, final int until ) throws IOException {

        final FileChannel fileChannel = FileChannel.open( path, StandardOpenOption.READ );
        final ByteBuffer bb = fileChannel.map( FileChannel.MapMode.READ_ONLY, from, until );

        final byte[] ret = bb.array();
        fileChannel.close();

        return ret;
    }

    /**
     * @param bytes bytes to read
     * @return hexadecimal value
     */
    public static String toHex( final byte[] bytes ) {
        final StringBuilder stringBuilder = new StringBuilder();

        for ( final byte aByte : bytes ) {
            stringBuilder.append( String.format( "%02x", aByte ) );
        }

        return stringBuilder.toString();
    }

    /**
     * Return file size if readable or FILE_NOT_READABLE.
     *
     * @param path to read
     * @return file size in byte
     */
    public static long getFilSizeSafe( final Path path ) {
        Objects.requireNonNull( path );

        try {
            return Files.size( path );
        } catch ( final IOException ioE ) {

            return FILE_NOT_READABLE;
        }

    }

    /**
     * Return the file name of a file denoted by this path.
     * This is the part before the last dot (.).
     *
     * @param path the path to a file
     * @return the file name without the file ending or suffix
     * @throws IOException          if {@code path} is not readable
     * @throws NullPointerException if {@code path} is null
     */
    public static String getPathPrefix( final Path path ) throws IOException {
        Objects.requireNonNull( path );
        if ( !Files.isReadable( path ) ) {
            throw new IOException( "Path '" + path + "' is not readable" );
        }

        final String[] temp = split( path.getFileName().toString() );
        return 0 == temp.length ? UNKNOWN_PATH_NAME : temp[0];
    }

    /**
     * Return 'raw' path name that is part of file name before first dot ({@literal .}).
     *
     * @param path path
     * @return part of {@code path} name before '.' or {@linkplain #UNKNOWN_PATH_NAME}
     * @throws NullPointerException if {@code path} is null
     */
    public static String getPathPrefix( final String path ) {
        Objects.requireNonNull( path );

        final String[] temp = split( path );
        return 0 == temp.length ? UNKNOWN_PATH_NAME : temp[0];
    }

    /**
     * Read path name ignoring io error.
     *
     * @param path path to read
     * @return name of file or {@linkplain #UNKNOWN_PATH_NAME}
     */
    public static String getPathNameSafe( final Path path ) {
        String name = UNKNOWN_PATH_NAME;
        try {
            name = getPathPrefix( path );
        } catch ( final IOException ioE ) {


            //
        }

        return name;
    }

    /**
     * File name suffix.
     *
     * @param path path to file
     * @return suffix i.E. part of file name after last dot
     * @throws IOException          if {@code path} is not readable
     * @throws NullPointerException if {@code path} is null
     */
    public static String getPathSuffix( final Path path ) throws IOException {
        Objects.requireNonNull( path );
        // if not readable throw
        if ( !Files.isReadable( path ) ) {
            throw new IOException( "Path '" + path + "' is not readable" );
        }
        // Path without name
        if ( null == path.getFileName() ) {
            return UNKNOWN_PATH_SUFFIX;
        }

        final String[] temp = split( path.toString() );
        return 0 == temp.length ? UNKNOWN_PATH_SUFFIX : temp[1];
    }

    /**
     * Read file suffix for path ignoring io error.
     *
     * @param path path
     * @return suffix of path or
     */
    public static String getPathSuffixSafe( final Path path ) {
        String suffix = UNKNOWN_PATH_SUFFIX;
        try {
            suffix = getPathSuffix( path );
        } catch ( final IOException ioE ) {

            // ignored

        }
        return suffix;
    }

    /**
     * Load Properties from path.
     *
     * @param pathToProp path
     * @return properties
     * @throws IOException          if not able to load
     * @throws NullPointerException if {@code pathToProp} is null
     */
    public static Properties loadProperties( final Path pathToProp ) throws IOException {
        Objects.requireNonNull( pathToProp );

        final Properties prop = new Properties();

        loadProperties( pathToProp, prop );

        return prop;
    }

    /**
     * Load Properties from path.
     *
     * @param pathToProp path to properties
     * @param prop       properties
     * @throws IOException if {@code pathToProp} !readable
     */
    public static void loadProperties( final Path pathToProp, final Properties prop ) throws IOException {

        Objects.requireNonNull( pathToProp );

        try ( final InputStream inStream = Files.newInputStream( pathToProp ) ) {
            prop.load( inStream );
        } catch ( final IOException ioE ) {

            LOG.error( "Fail to load proper,", ioE );
            throw ioE;
        }

    }

    /**
     * Return whether a path maybe image.
     * <p>
     * Warning: this only check the file suffix against a map of known java supported image files.
     * </p>
     *
     * @param path path to image
     * @return {@code false} if {@code path} seem to be no image or is null
     */
    public static boolean isImage( final Path path ) {
        final String su = getPathSuffixSafe( path );

        return null != path && IMG_TYPE_MAP.containsKey( su.toLowerCase() );
    }

    /**
     * Return true if path denote to a font.
     *
     * @param path path
     * @return {@code true} if path may be a font
     */
    public static boolean isFont( final Path path ) {
        final String su = getPathSuffixSafe( path );

        return null != path && TYPE_FON_MAP.containsKey( su.toLowerCase() );
    }

    private static FileSystem createZipFileSys( final Path zipPath ) throws URISyntaxException, IOException {
        // create a jar
        final URI uri = new URI( "jar", zipPath.toUri().toString(), null );

        final FileSystem zipFS = FileSystems.newFileSystem( uri, ZIP_FS_MAP );

        return zipFS;
    }

    private static String[] split( final String fileName ) {
        assert null != fileName : "File name is null";

        final int lastDot = fileName.lastIndexOf( "." );

        final String[] ret = new String[2];
        ret[0] = lastDot > 0 ? fileName.substring( 0, lastDot ) : fileName;
        ret[1] = lastDot > 0 ? fileName.substring( lastDot ) : "";

        return ret;
    }

}
