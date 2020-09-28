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


        private Predicate<Path> pp;
        private BlockingQueue<Path> bq;

        private SimpleFileWalker( final BlockingQueue<Path> bq ) {
            this.bq = bq;
        }



        @Override
        public FileVisitResult preVisitDirectory( Path path, BasicFileAttributes basicFileAttributes ) throws IOException {


            return Files.isReadable( path ) ? FileVisitResult.CONTINUE : FileVisitResult.SKIP_SUBTREE;
        }

        @Override
        public FileVisitResult visitFile( Path path, BasicFileAttributes basicFileAttributes ) throws IOException {

            if( pp.test( path ) ) {
                try {
                    bq.put( path );
                } catch ( InterruptedException ie ) {

                    Thread.currentThread().interrupt();
                    return FileVisitResult.TERMINATE;
                }
            }

            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed( Path path, IOException e ) throws IOException {

            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory( Path path, IOException e ) throws IOException {
            return FileVisitResult.CONTINUE;
        }
    }


