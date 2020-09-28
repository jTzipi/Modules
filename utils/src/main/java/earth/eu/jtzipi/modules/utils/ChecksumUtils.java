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

package earth.eu.jtzipi.modules.utils;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
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


    private static byte[] hash( File file, MessageDigest messageDigest ) throws IOException {
        byte[] hb;
        RandomAccessFile rand = new RandomAccessFile( file, "r" );

        hb = DigestUtils.digest( messageDigest, rand );
        rand.close();

        return hb;
    }
}
