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

import earth.eu.jtzipi.modules.io.IOUtils;
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
 * Callable for searching paths.
 *
 *
 */
public class FindPathTask implements Callable<Long> {

    public static final Path __NULL__ = Paths.get("/__NULL__");


    private static final Logger Log = LoggerFactory.getLogger( "FindPath" );

    private long matches;
    private Path path;      // root path
    private Predicate<Path> criteria;   // predicate
    private BlockingQueue<Path> foundPathBQ;    // shared path

    FindPathTask( final Path rootPath,
                  final Predicate<Path> pathPredicate,
                //  final BlockingDeque<Path> pathBlockingDeque,
                    final BlockingQueue<Path> resultQueue ) {

        this.path = rootPath;

        this.criteria = pathPredicate;

        this.foundPathBQ= resultQueue;
    }

    /**
     *
     * @param root
     * @param pathCriteria
     * @param sharedQueue
     * @return
     * @throws IOException
     */
    public static FindPathTask of( Path root, Predicate<Path> pathCriteria, BlockingQueue<Path> sharedQueue ) throws IOException {
        Objects.requireNonNull(root, "root path is null");
        Objects.requireNonNull( sharedQueue );

        if( !Files.isReadable( root ) ) {
            throw new IOException("Path[='"+root+"'] is not readable");
        }
        // error
        if( !Files.isDirectory( root )) {
            throw new IllegalArgumentException( "Path[='"+root+"'] is dir" );
        }
        // set default predicate
        if( null == pathCriteria) {
            pathCriteria = IOUtils.PATH_ACCEPT_ALL;
        }

        return new FindPathTask( root, pathCriteria, sharedQueue );
    }

    @Override
    public Long call() throws Exception {

        Log.warn( "Starte suche" );
        search( path );
        foundPathBQ.add( __NULL__) ;


        System.out.println("Fertig " + path  );
        System.out.println(" " + matches   );
        return matches;
    }

    private void search( final Path path ) {

            if(!Files.isReadable( path )) {
                Log.warn( "Can not read dir '" + path + "'" );

                return;
            }
        try( DirectoryStream<Path> ds = Files.newDirectoryStream(path) ) {
            final Iterator<Path> pit = ds.iterator();
            while(pit.hasNext()) {


                Path pn = pit.next();
                //System.out.println(pn);
                if( Files.isDirectory( pn )) {
                    search( pn );
                }

                if( criteria.test( pn ) ) {
                   boolean offered = foundPathBQ.offer( pn );

                   if( offered ) {
                       matches++;
                   }
                }
            }

        } catch( final IOException ioE ) {

        }

    }
}
