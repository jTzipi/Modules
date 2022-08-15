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

package earth.eu.jtzipi.modules.utils;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.AccessControlException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Objects;

/**
 * Utils for calc checksum or md.
 *
 * @author jTzipi
 */
public final class ChecksumUtils {

    /**
     * Small file size.
     */
    public static final long SIZE_FILE_SMALL = 1_000_000L;
    /**
     * Large file size.
     */
    public static final long SIZE_FILE_LARGE = 12_000_000L;
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger( "ChecksumUtils" );
    private static final int BUFFER_SIZE_1024 = 1024;
    private static final int BUFFER_SIZE_LARGE = 1_024_000;


    private ChecksumUtils() {
// no
        throw new AccessControlException( "" );
    }

    /**
     * Calculate digest of path.
     * <p>
     * This should work for large files since we use a random access file.
     *
     * @param path          path [not null , must be a file]
     * @param messageDigest digest
     * @return hex hash code
     * @throws IOException              I/O or {@code path} is not readable
     * @throws NullPointerException     if {@code path} is null
     * @throws IllegalArgumentException if path is dir
     */
    public static String calcHash( final Path path, MessageDigest messageDigest ) throws IOException {

        Objects.requireNonNull( path );
        if ( null == messageDigest ) {

            messageDigest = DigestUtils.getSha256Digest();
        }

        if ( Files.isDirectory( path ) ) {
            throw new IllegalArgumentException( "Path '" + path + "' seem to be a dir" );
        }
        if ( !Files.isReadable( path ) ) {
            throw new IOException( "File '" + path + "' is not readable" );
        }

        final long fileSize = Files.size( path );


        /*final byte[] hash = fileSize <= SIZE_FILE_SMALL
                ? hashSmall( path, messageDigest )
                :  fileSize > SIZE_FILE_LARGE
                ? hashLarge( path, messageDigest )
                : hash( path, messageDigest );*/
        // return Hex.encodeHexString( hash );
        return calcHashCommonCodec( path, messageDigest );
    }

    /**
     * Try to compute hash value of a file.
     *
     * @param path path to file . Should be no dir
     * @param md   message digest
     * @return
     * @throws IOException
     * @throws NullPointerException     if {@code path} is null
     * @throws IllegalArgumentException if {@code path} is not a file
     */
    public static String calcHashCommonCodec( final Path path, MessageDigest md ) throws IOException {

        Objects.requireNonNull( path );
        if ( null == md ) {

            md = DigestUtils.getSha256Digest();
        }
        if ( Files.isDirectory( path ) ) {
            throw new IllegalArgumentException( "Path '" + path + "' seem to be a dir" );
        }
        if ( !Files.isReadable( path ) ) {
            throw new IOException( "File '" + path + "' is not readable" );
        }
        byte[] hash = DigestUtils.digest( md, path.toFile() );
        return Hex.encodeHexString( hash );
    }

    private static byte[] hashSmall( final Path file, final MessageDigest md ) throws IOException {


        final byte[] fbyte = Files.readAllBytes( file );

        return md.digest( fbyte );

    }

    private static byte[] streamHashFile( final File file, int bufSize, MessageDigest md ) throws IOException {

        byte[] buf = new byte[bufSize];
        try ( DigestInputStream dis = new DigestInputStream( new FileInputStream( file ), md ) ) {

            for ( int read = dis.read( buf, 0, bufSize ); read > -1; read = dis.read( buf, 0, bufSize ) ) {
                // here we don't need anything
            }

            md = dis.getMessageDigest();
            return md.digest();
        }
    }

    private static byte[] hashFile( final File file, final MessageDigest md ) {

        final ByteBuffer byteBuffer = ByteBuffer.allocate( 1_000_000 );
        byte[] digest;
        try ( final RandomAccessFile raf = new RandomAccessFile( file, "r" ); final FileChannel fch = raf.getChannel() ) {
            while ( fch.read( byteBuffer ) > 0 ) {
                md.update( byteBuffer );
                byteBuffer.clear();
            }
            digest = md.digest();
            return digest;
        } catch ( final IOException ioE ) {
            LOG.error( "Can not calculate hash", ioE );

            digest = new byte[0];
        }

        return digest;
    }

    private static byte[] hashLarge( final Path file, final MessageDigest md ) throws IOException {

        final long size = Files.size( file );

        final ByteBuffer buf = ByteBuffer.allocate( BUFFER_SIZE_LARGE );

        try ( final RandomAccessFile raf = new RandomAccessFile( file.toFile(), "r" );
              final FileChannel ch = raf.getChannel() ) {


            // read the first 'BUFFER_SIZE_LARGE' bytes
            ch.read( buf );
            // update md
            md.update( buf );
            // reset buffer
            buf.clear();
            // move file pointer
            ch.position( size - BUFFER_SIZE_LARGE );
            // read last byte
            ch.read( buf );
            md.update( buf );
        }

        return md.digest();
    }

    private static byte[] hash( final Path file, final MessageDigest messageDigest ) throws IOException {
        // close random acc file close file also close file ch.
        try ( final RandomAccessFile rand = new RandomAccessFile( file.toFile(), "r" ); final FileChannel fileChannel = rand.getChannel() ) {

            final ByteBuffer byteBuffer = ByteBuffer.allocate( BUFFER_SIZE_LARGE );


            while ( fileChannel.read( byteBuffer ) > 0 ) {
                messageDigest.update( byteBuffer );
                byteBuffer.clear();
            }

            // byte[] digest =

        }

        return messageDigest.digest();
    }
}
