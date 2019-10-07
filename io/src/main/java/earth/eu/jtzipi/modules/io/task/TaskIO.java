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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Predicate;

/**
 * Task IO based.
 * @author jTzipi
 */
public class TaskIO {
    public static final int CPUS = Runtime.getRuntime().availableProcessors();
    private static final Logger Log = LoggerFactory.getLogger( "TaskIO" );
    private static ExecutorService ser = Executors.newFixedThreadPool( CPUS );

    /**
     * Start a file search.
     *
     * @param rootPathList directories to start
     * @param pathPred     predicate
     *
     * @param ser          Executor service
     */
    public static Map<Path, Future<List<Path>>> searchFiles( final List<Path> rootPathList, Predicate<Path> pathPred, ExecutorService ser ) {
        Objects.requireNonNull( rootPathList, "root path" );


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
}
