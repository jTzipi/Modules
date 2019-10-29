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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

/**
 * Callable for searching paths in a dir and its sub dirs.
 * <p>
 *     This path find task traverses all sub dirs of a root dir adding
 *     path's to a list matching certain predicate.
 *     <br/>
 *     When all directories are traversed a list of found paths returned.
 *     <br/>
 *
 *     There is no handling of intermediate results.
 * </p>
 * @author jTzipi
 */
public class FindPathTask implements Callable<List<Path>> {




    private static final Logger Log = LoggerFactory.getLogger( "FindPath" );

    //private long matches;
    private Path path;      // root path
    private List<Path> foundPathL;
    private Predicate<Path> criteria;   // predicate



    FindPathTask( final Path rootPath,
                  final Predicate<Path> pathPredicate) {

        this.path = rootPath;
        this.foundPathL = new ArrayList<>();
        this.criteria = pathPredicate;


    }

    /**
     * Create FindPathTask.
     *
     * @param root root dir
     * @param pathCriteria path predicate (optional)
     *
     * @return FindPathTask
     * @throws IOException if
     */
    public static FindPathTask of( Path root, Predicate<Path> pathCriteria ) throws IOException {
        Objects.requireNonNull(root, "root path is null");


        if( !Files.isReadable( root ) ) {
            throw new IOException("Path[='"+root+"'] is not readable");
        }
        // error
        if( !Files.isDirectory( root )) {
            throw new IllegalArgumentException( "Path[='"+root+"'] is not dir" );
        }
        // set default predicate
        if( null == pathCriteria) {
            pathCriteria = IOUtils.PATH_ACCEPT_ALL;
        }

        return new FindPathTask( root, pathCriteria );
    }

    @Override
    public List<Path> call()  {

        Log.warn( "Start" );
        search( path );



        //System.out.println("Fertig " + path  );
        // System.out.println(" " + matches   );
        return foundPathL;
    }

    private void search( final Path path ) {
    // path not readable return
            if(!Files.isReadable( path )) {
                Log.warn( "Can not read dir '" + path + "'" );

                return;
            }


            try( DirectoryStream<Path> ds = Files.newDirectoryStream(path) ) {
            for( Path pn : ds ) {



                //System.out.println(pn);
                if( Files.isDirectory( pn )) {
                    search( pn );
                }

                if( criteria.test( pn ) ) {

        foundPathL.add( pn );

                }
            }

        } catch( final IOException ioE ) {

            Log.warn("Error reading dir", ioE  );
        }

    }
}
