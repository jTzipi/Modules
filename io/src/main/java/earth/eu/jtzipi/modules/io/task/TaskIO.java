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
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Predicate;

/**
 * Task IO based.
 * @author jTzipi
 */
public final class TaskIO {
    /**
     * Number of CPU's.
     */
    public static final int CPUS = Runtime.getRuntime().availableProcessors();

    private static final Logger Log = LoggerFactory.getLogger( "TaskIO" );
    /**
     * Default Executor.
     */
    private static ExecutorService FIXED_THREAD = Executors.newFixedThreadPool( CPUS );

    private TaskIO() { throw new AssertionError(""); }
    /**
     * Start a file search.
     * <p>
     *
     * </p>
     *
     * @param rootPathList directories to start
     * @param pathPred     predicate
     * @param ser          Executor service
     * @return map with path keys and Futures of async computation of path
     */
    public static Map<Path, Future<List<Path>>> searchAsFuture( final List<Path> rootPathList, Predicate<Path> pathPred, ExecutorService ser ) {
        Objects.requireNonNull( rootPathList, "root path" );

        if( null == ser ) {
            ser = FIXED_THREAD;
        }

        Map<Path, Future<List<Path>>> futureLM = new HashMap<>();
        for ( Path dir : rootPathList ) {
            try {
                FindPathTask fpt = FindPathTask.of( dir, pathPred );
                futureLM.put( dir, ser.submit( fpt ) );
            } catch ( final IOException ioE ) {
                futureLM.put( dir, null );
                Log.warn( "Can not read dir'" + dir );
            }


            // System.err.println("Los gehts '" + path + "'" );


        }
        return futureLM;
    }

    /**
     * Start a search for files found in list of root dirs.
     *
     * @param rootPathList
     * @param pathPred
     * @param sharedQ
     * @param ser
     * @return
     */
    public static List<Future<?>> search( final List<Path> rootPathList, Predicate<Path> pathPred, BlockingQueue<Path> sharedQ, ExecutorService ser ) {


        List<Future<?>> ret = new ArrayList<>();
        for( Path path : rootPathList ) {
try {
    PathCrawler pc = PathCrawler.of( path, pathPred, sharedQ );
    ret.add( ser.submit( pc ) );
} catch ( final IOException ioE ) {

}
        }

        return ret;
    }
    /**
     * Coerce an unchecked Throwable to a RuntimeException.
     * <p/>
     * If the Throwable is an Error, throw it; if it is a
     * RuntimeException return it, otherwise throw IllegalStateException
     *
     * (c) Brian Goetz and Tim Peierls
     * @param t Throwable
     * @return specific
     */
    public static RuntimeException launderThrowable(Throwable t) {
        if (t instanceof RuntimeException) {
            return (RuntimeException) t;
        } else if (t instanceof Error) {
            throw (Error) t;
        } else {
            throw new IllegalStateException("Not unchecked", t);
        }
    }
}
