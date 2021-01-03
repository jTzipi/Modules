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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Objects;

/**
 * Utils for calc checksum.
 */
public final class ChecksumUtils {

    private ChecksumUtils() {

    }

    /**
     * Calculate digest of path.
     * <p>
     * This should work for large files since we use a random access file.
     *
     * @param path          path [not null , must be a file]
     * @param messageDigest digest
     * @return hex hash code
     * @throws IOException              I/O
     * @throws NullPointerException     if
     * @throws IllegalArgumentException if path is dir
     */
    public static String calcHash( final Path path, MessageDigest messageDigest ) throws IOException {
        Objects.requireNonNull( path );
        if ( null == messageDigest ) {

            messageDigest = DigestUtils.getSha256Digest();
        }

        if ( Files.isDirectory( path ) ) {
            throw new IllegalArgumentException( "Path '" + path + " seem to be a dir" );
        }
        if ( !Files.isReadable( path ) ) {
            throw new IOException( "File '" + path + "' is not readable" );
        }


        return Hex.encodeHexString( hash( path.toFile(), messageDigest ) );
    }

    private static byte[] hashFile( final File file, final MessageDigest md ) {
        final ByteBuffer byteBuffer = ByteBuffer.allocate( 1_000_000 );
        try ( final RandomAccessFile raf = new RandomAccessFile( file, "r" ); final FileChannel fch = raf.getChannel() ) {
            while ( fch.read( byteBuffer ) > 0 ) {
                md.update( byteBuffer );
            }
            final byte[] digest = md.digest();
            return digest;
        } catch ( final IOException ioE ) {


            return null;
        }


    }

    private static byte[] hash( final File file, final MessageDigest messageDigest ) throws IOException {
        final byte[] hb;
        final RandomAccessFile rand = new RandomAccessFile( file, "r" );
        final FileChannel fileChannel = rand.getChannel();
        final ByteBuffer byteBuffer = ByteBuffer.allocate( 1_000_000 );


        while ( fileChannel.read( byteBuffer ) > 0 ) {
            messageDigest.update( byteBuffer );
        }
        final byte[] digest = messageDigest.digest();

        hb = DigestUtils.digest( messageDigest, rand );
        fileChannel.close();

        return hb;
    }
}
