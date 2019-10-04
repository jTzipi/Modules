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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

/**
 * Task IO based.
 * @author jTzipi
 */
public class TaskIO {
    private static final Logger Log = LoggerFactory.getLogger("");


    private static final int CPUS = Runtime.getRuntime().availableProcessors();
    private static ExecutorService ser = Executors.newFixedThreadPool( CPUS );


    /**
     * Start a file search.
     * @param rootPath directory to start
     * @param pathPred predicate
     * @param sharedPathQ shared path
     * @param ser Executor service
     * @throws IOException if {@code rootPath} is
     */
    public static void searchFiles( final Path rootPath,  Predicate<Path> pathPred, BlockingQueue<Path> sharedPathQ, ExecutorService ser ) throws IOException {
        Objects.requireNonNull(rootPath, "root path");
        Objects.requireNonNull( sharedPathQ, "shared" );

        if(!Files.isReadable(rootPath))  {
            throw new IOException( "Can not read '" + rootPath + "'" );
        }

        List<Path> dirs = IOUtils.lookupDir( rootPath, IOUtils.PATH_ACCEPT_DIR );



                //FindPathTask fpt = FindPathTask.of( path, imgPred, imgQ );
                // System.err.println("Los gehts '" + path + "'" );






    }
}
