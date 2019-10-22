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

package earth.eu.jtzipi.modules.io.watcher;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static java.nio.file.StandardWatchEventKinds.*;


public class Watcher {

    private static final Logger Log = LoggerFactory.getLogger( "Watcher" );
    // store watch keys for paths
    private final Map<WatchKey, Path> watchKeyM = new HashMap<>();


    private ExecutorService es;
    private Path dir;
    private WatchService ws;
    private boolean recursive ;
    private boolean trace;

    private boolean running;
    private BiConsumer<Path, WatchEvent.Kind> pac;
    private Consumer<Path> errorpc;

    private Watcher() {

    }



    /**
     * Create Watcher.
     * @return Watcher
     * @throws IOException if no watch service
     */
    public static Watcher create(  ) throws IOException {


        try {

            Watcher w = new Watcher( );
            w.init();

            return w;
        } catch (  final IOException ioe ) {

            Log.error( "WatchService failed", ioe );

            throw ioe;
        }

    }

    private void  init(  ) throws IOException {
        this.ws = FileSystems.getDefault().newWatchService();
    }

    /**
     * Start watch dir.
     * @param dirpath path to dir
     * @return Future
     * @throws IOException if {@code dirpath} is not readable or is not a dir
     *
     */
    public Future<WatchBar> watch( final Path dirpath ) throws IOException {
        Objects.requireNonNull( dirpath );
        // check readable
        if( !Files.isReadable( dirpath )) {

            throw new IOException( "Dir 'dirPath' is not readable" );
        }
        // check dir


        if( es.isShutdown() ) {

        }


        boolean pathNew = watchKeyM.values().stream().noneMatch( path -> dirpath.equals( path ) );
        if( !pathNew ) {
            throw new IllegalArgumentException( "" );
        }





        return es.submit( doGuckmal( dirpath ) );
    }

    public void stop() {

        if( es.isShutdown() ) {

            return;
        }

        try {
            ws.close();
        } catch ( IOException ioE ) {

            Log.warn( "Fail to stop WatchService" );
        } finally {
            es.shutdown();
            this.running = false;
            try {
                es.awaitTermination( 9L, TimeUnit.SECONDS );
            } catch ( InterruptedException iE ) {
                // ignored
            }
        }
    }

    private Path register( final Path dirPath )  {


        try {

            WatchKey key = dir.register( ws, StandardWatchEventKinds.ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE );
            // trace old entries
            if(trace) {
                Path oldPath = watchKeyM.get( key );
                // new entry
                if( null == oldPath ) {

                    Log.info( "Watching ' + dirPath + '" );
                } else {

                    if( oldPath.equals( dirPath ) ) {
                        Log.info( "Try registering same dir ' + dirPath + '" );
                    }

                    Log.info( "Updating ' + dirPath + '" );
                }
            }
            this.watchKeyM.put( key, dirPath );
            return dirPath;

        } catch ( IOException ioe ) {

            Log.warn( "Can not register watchkey for path '" + dirPath  );
            return null;
        }
    }

    final List<Path> registerDir( final Path dir )  {
        List<Path> failedL = new ArrayList<>();


        try {
            Files.walkFileTree( dir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)

                {

                      Path path = register(dir);

                if( null == path ) {
                    failedL.add( dir );
                }

                    return FileVisitResult.CONTINUE;
                }
            } );
        } catch ( IOException e ) {
failedL.add( dir );
        }

return failedL;
    }

    private  Callable<WatchBar> doGuckmal( final Path path )  {

        return new Callable<WatchBar>() {

            @Override
            public WatchBar call() throws InterruptedException {



                if( recursive) {
                    List<Path> ioL = registerDir( path );
                } else {

                }

                // nothing registered
                if( watchKeyM.isEmpty()) {

// TODO: return

                }

                for(;;) {

                    WatchKey key;
                    try {
                        key = ws.take();
                    } catch (InterruptedException iE) {

                        throw iE;
                    }
    // path of dir
                    Path dir = watchKeyM.get(key);
                    if (dir == null) {
                        Log.warn("Watch key is null");
                        continue;
                    }
        // poll events
                    for (WatchEvent<?> event: key.pollEvents()) {
                        WatchEvent.Kind kind = event.kind();

    // TODO: how to handle?
                        if (kind == OVERFLOW) {
                            continue;
                        }


                        WatchEvent<Path> wat = (WatchEvent<Path>)event; // cast
                        Path path = wat.context();                      // name of path
                        Path abs = dir.resolve(path);                   // resolve against
;
                        if (recursive && (kind == ENTRY_CREATE)) {

                                if (Files.isDirectory(abs, LinkOption.NOFOLLOW_LINKS)) {
                                    registerDir(abs);
                                }

                        }
                    }

                    // THIS MUST BE
                    boolean valid = key.reset();
                    if (!valid) {
                        Path removed = watchKeyM.remove( key );

                        // no more dirs to watch
                        if (watchKeyM.isEmpty()) {



                            break;
                        }
                    }
                }

                return null;
            }
        };
    }

    public interface WatchBar {

        List<Path> errorList();


    }
}