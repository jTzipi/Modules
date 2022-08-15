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

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Utils to store and load zip files.
 * @author jTzipi
 */
public final class ZipUtils {

    final static Map<Path, FileSystem> zipMap = new HashMap<>();

    private ZipUtils() {

    }

    /**
     *
     * @param pathToZip
     * @return
     */
    public static FileSystem getZipFilesystem(final Path pathToZip )  {
        Objects.requireNonNull(pathToZip);
        if( Files.isReadable(pathToZip)) {
            throw new IllegalArgumentException("Can not read '+pathToZip+'");
        }


        return zipMap.computeIfAbsent( pathToZip, p -> {
            try {
                return FileSystems.newFileSystem( p, ClassLoader.getPlatformClassLoader() );
            } catch ( final IOException ioe ) {
                return null;
            }
        } );

    }

    public static BasicFileAttributes readZipAttributes( final Path root, final Path relPath ) throws IOException {

        final FileSystem zfs = getZipFilesystem( root );
        return zfs.provider().readAttributes( relPath, BasicFileAttributes.class );

    }

    void closeZipFS(final Path zipPath) {
        if( !zipMap.containsKey( zipPath )) {

            throw new IllegalStateException("");
        }

        try {
            zipMap.get( zipPath ).close();
        } catch ( final IOException ioe ) {


        }
    }
}
