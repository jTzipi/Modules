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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.function.Predicate;


/**
 * PathCrawler scans directories for files.
 *   <p>
 *       We use a blocking q to store found files.
 *       That is this class is a <i>producer</i>.
 *      Other threads take files to do other tasks.
 *   </p>
 */
public class PathCrawler implements Callable<Void> {

    /** Indicator that this branch is finished.*/
    public static final Path __NULL__ = Paths.get("/null");

    private static final Logger LOG = LoggerFactory.getLogger( "" );

    private final Path root;
    private final Predicate<? super Path> pred;
    private final BlockingQueue<Path> foundPathBQ;    // shared path


    private PathCrawler( final Path dir, final Predicate<? super Path> predicate, final BlockingQueue<Path> sharedBQ ) {
        this.root = dir;
        this.pred = predicate;
        this.foundPathBQ = sharedBQ;
    }

    /**
     * Create a path crawler.
     *
     * @param rootDir         root directory
     * @param pathPred        path pattern
     * @param sharedBlockingQ shared blocking queue
     * @return path crawler
     * @throws IllegalArgumentException if {@code pathPred} is null
     * @throws NullPointerException     if {@code rootDir} or {@code sharedBlockingQ} are null
     */
    public static PathCrawler of( final Path rootDir, final Predicate<Path> pathPred, final BlockingQueue<Path> sharedBlockingQ ) {
        Objects.requireNonNull( rootDir );
        Objects.requireNonNull( sharedBlockingQ );
        if ( null == pathPred ) {
            throw new IllegalArgumentException( "You must provide a path predicate" );
        }


        return new PathCrawler( rootDir, pathPred, sharedBlockingQ );
    }

    public Void call() {

        search( root ); // crawl
        foundPathBQ.add( __NULL__ ); // put null
        return null;
    }

    private void search( final Path path ) {

        if ( !Files.isReadable( path ) ) {


            return;
        }
        try ( final DirectoryStream<Path> ds = Files.newDirectoryStream( path ) ) {
            final Iterator<Path> pit = ds.iterator();

            while ( pit.hasNext() ) {


                final Path pn = pit.next();
                //System.out.println(pn);
                if ( Files.isDirectory( pn ) ) {
                    search( pn );
                }

                // found
                if ( pred.test( pn ) ) {

                    // put to bq

                    try {
                        foundPathBQ.put( pn );
                    } catch ( final InterruptedException iE ) {

                        Thread.currentThread().interrupt();
                    }
                }
            }

        } catch( final IOException ioE ) {
LOG.warn("Warn ", ioE );
        }

    }
}
