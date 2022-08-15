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

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.BlockingQueue;
import java.util.function.Predicate;

public class SimpleFileWalker implements FileVisitor<Path> {

    private final BlockingQueue<? super Path> bq;
    private Predicate<? super Path> pp = p -> true;

    private SimpleFileWalker( final BlockingQueue<? super Path> bq ) {

        this.bq = bq;
    }

    @Override
    public FileVisitResult preVisitDirectory( final Path path, final BasicFileAttributes basicFileAttributes ) {


        return Files.isReadable( path ) ? FileVisitResult.CONTINUE : FileVisitResult.SKIP_SUBTREE;
    }

    @Override
    public FileVisitResult visitFile( final Path path, final BasicFileAttributes basicFileAttributes ) {

        if ( pp.test( path ) ) {
            try {
                bq.put( path );
            } catch ( final InterruptedException ie ) {

                Thread.currentThread().interrupt();
                return FileVisitResult.TERMINATE;
            }
        }

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed( final Path path, final IOException e ) {

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory( final Path path, final IOException e ) {
        return FileVisitResult.CONTINUE;
    }

    public void setPredicate( Predicate<? super Path> pathPredicate ) {
        this.pp = pathPredicate;
    }
}


