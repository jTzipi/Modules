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

package earth.eu.jtzipi.modules.io.task;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Objects;
import java.util.concurrent.Callable;

import static earth.eu.jtzipi.modules.utils.ChecksumUtils.calcHash;


/**
 *
 */
public class FileHashTask implements Callable<String> {

    private final Path path;
    private final MessageDigest md;

    /**
     * @param path          path to file
     * @param messageDigest message digest
     */
    FileHashTask( final Path path, final MessageDigest messageDigest ) {
        this.path = path;
        this.md = messageDigest;
    }

    public static FileHashTask of( final Path path, MessageDigest messageDigest ) throws IOException {
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

        return new FileHashTask( path, messageDigest );
    }

    @Override
    public String call() {


        String ret;

        try {
            ret = calcHash( path, md );
        } catch ( final IOException ioE ) {

            ret = "?";
        }

        return ret;
    }
}
