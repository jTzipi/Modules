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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.function.Predicate;


/**
 * PathCrawler scans directories for files.
 *
 */
public class PathCrawler implements Callable<Void> {


    public static final Path __NULL__ = Paths.get("/null");
    private static final Logger LOG = LoggerFactory.getLogger( "" );
    private Path root;
    private Predicate<Path> pred;
    private BlockingQueue<Path> foundPathBQ;    // shared path


    private PathCrawler( final Path dir, final Predicate<Path> predicate, final BlockingQueue<Path> sharedBQ ) {
        this.root = dir;
        this.pred = predicate;
        this.foundPathBQ = sharedBQ;
    }



    public Void call(  ) {

        search( root );
        return null;
    }

    private void search( final Path path ) {

        if(!Files.isReadable( path )) {


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

                // found
                if( pred.test( pn ) ) {


                }
            }

        } catch( final IOException ioE ) {
LOG.warn("Warn ", ioE );
        }

    }
}
