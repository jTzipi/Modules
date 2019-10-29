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
import java.util.concurrent.FutureTask;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Watch Service Builder.
 * TODO: Singleton?
 */

public final class Watcher {

    private static final Logger Log = LoggerFactory.getLogger( "Watcher" );

    private WatchService ws;
    private boolean running;

    /**
     * Watchkeys for all events.
     */
    public static List<WatchEvent.Kind<?>> WATCH_KEY_EVENT_ALL = Arrays.asList( ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE );

    private Watcher() {

    }


    /**
     * Create Watcher.
     * <p>
     * Here we create a new instance of WatchService.
     * This service will later used for all watch tasks.
     * </p>
     *
     * @return Watcher
     * @throws IOException if no watch service
     */
    public static Watcher create() throws IOException {

        Watcher watcher = new Watcher();

        try {
            watcher.ws = FileSystems.getDefault().newWatchService();
        } catch ( IOException ioE ) {
            Log.error( "No watch service", ioE );
            throw ioE;
        }

        return watcher;

    }

    private void init() throws IOException {

    }

    /**
     * Start watch dir.
     *
     * @param dir path to dir
     * @return Future
     * @throws IOException          if {@code dir} is not readable or is not a dir
     * @throws NullPointerException if {@code dir} is null
     */
    public FutureTask<?> watch( final Path dir, final boolean traceProp, final boolean recursiveProp, final IWatchEventHandler watchEventHandler, final List<WatchEvent.Kind<?>> eventType ) throws IOException {
        Objects.requireNonNull( dir );
        // check readable
        if ( !Files.isReadable( dir ) ) {

            throw new IOException( "Dir 'dirPath' is not readable" );
        }
        // check dir
        if ( !Files.isDirectory( dir ) ) {

            throw new IOException( "Path ' + dir + ' seems to be not of type directory" );
        }

        WatchTask wt = new WatchTask( dir, traceProp, recursiveProp, watchEventHandler, eventType );

        return new FutureTask<>( wt );
    }

    /**
     * Build FutureTask for watching given dir.
     *
     * @param dir directory
     * @return future task
     * @throws IOException          if {@code dir} is not readable or is not a dir
     * @throws NullPointerException if {@code dir} is null
     */
    public FutureTask<?> watch( final Path dir ) throws IOException {
        boolean trace = false;
        boolean recursive = false;
        List<WatchEvent.Kind<?>> wkL = WATCH_KEY_EVENT_ALL;
        IWatchEventHandler wh = new WatchEventAdapter();

        return watch( dir, trace, recursive, wh, wkL );
    }

    private Callable<WatchBar> doGuckmal( final Path path ) {
        return new Callable<WatchBar>() {
            @Override
            public WatchBar call() throws Exception {
                return new WatchBar() {
                    @Override
                    public List<Path> errorList() {
                        return new ArrayList<>();
                    }
                };
            }
        };
    }


    public interface WatchBar {

        List<Path> errorList();


    }

    /**
     * Watch Task.
     * <p>
     * Watching for events in a directory and optional sub directories with the ability to control flow.
     * </p>
     */
    private class WatchTask implements Callable<Void> {

        // store watch keys for paths
        private final Map<WatchKey, Path> watchKeyM = new HashMap<>();

        private final Path dir;                     // root dir
        private final List<WatchEvent.Kind<?>> weks; // Events to watch for
        private boolean recursive;                  // watch sub dirs
        private boolean trace;                      // trace
        private IWatchEventHandler weha;            // Watch event

        private WatchTask( final Path watchDir, final boolean trace, final boolean recursive, final IWatchEventHandler watchEventHandler, List<WatchEvent.Kind<?>> watchKeyEventL ) {
            this.dir = watchDir;
            this.trace = trace;
            this.recursive = recursive;
            this.weha = watchEventHandler;
            this.weks = watchKeyEventL;
        }

        /**
         * Register a dir for all events.
         *
         * @param dirPath path to dir
         * @return path or {@code null} if failed
         */
        private boolean register( final Path dirPath ) {


            try {

                WatchKey key = dirPath.register( ws, weks.toArray( new WatchEvent.Kind<?>[0] ) );
                // trace old entries
                if ( trace ) {
                    Path oldPath = watchKeyM.get( key );
                    // new entry
                    if ( null == oldPath ) {

                        Log.info( "Watching ' + dirPath + '" );
                    } else {

                        if ( oldPath.equals( dirPath ) ) {
                            Log.info( "Try registering same dir ' + dirPath + '" );
                        }

                        Log.info( "Updating ' + dirPath + '" );
                    }
                }

                Log.info( " Register '" + dirPath );
                this.watchKeyM.put( key, dirPath );
                return true;

            } catch ( IOException ioe ) {

                Log.warn( "Can not register watchkey for path '" + dirPath );
                return false;
            }
        }

        private final List<Path> registerDir( final Path dir ) {
            List<Path> failedL = new ArrayList<>();


            try {
                Files.walkFileTree( dir, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory( Path dirPath, BasicFileAttributes attrs ) {


                        if ( !register( dirPath ) ) {
                            failedL.add( dirPath );
                        }

                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed( Path path, IOException ioE ) {
                        failedL.add( path );

                        return FileVisitResult.CONTINUE;
                    }
                } );
            } catch ( IOException e ) {

                Log.error( "IO during register", e );
            }

            return failedL;
        }

        private void init() throws IOException {

            if ( recursive ) {
                List<Path> failedL = registerDir( dir );

                Log.info( "Dirs we can not watch '" + failedL + "'" );
                // no dirs registered
                if ( watchKeyM.isEmpty() ) {

                    throw new IOException( "Dir '" + dir + "' is not registered" );
                }

            } else {


                if ( !register( dir ) ) {
                    throw new IOException( "Dir '" + dir + "' is not registered" );
                }
            }

        }

        public Void call() throws Exception {

            Log.info( "Start of Callabale.\n 1. Register '" + dir + "'" );

            try {
                init();

                Log.info( "Registered!" );
            } catch ( Exception ioE ) {
                Log.error( "Failed to init", ioE );
                throw ioE;
            }


// Start handling
            for ( ; ; ) {

                WatchKey key;
                try {
                    key = ws.take();
                } catch ( InterruptedException iE ) {
                    Log.warn( "IE ... stop!" );
                    // TODO: was tun?
                    break;
                }
                // path of dir
                Path dirEvent = watchKeyM.get( key );
                if ( dirEvent == null ) {
                    Log.warn( "Watch key is not known" );
                    continue;
                }
                // poll events
                for ( WatchEvent<?> event : key.pollEvents() ) {
                    WatchEvent<Path> wat = ( WatchEvent<Path> ) event;  // cast
                    WatchEvent.Kind kind = wat.kind();                  //
                    int ce = wat.count();                               //
                    Path path = wat.context();                          // name of path
                    Path abs = dirEvent.resolve( path );                     // resolve against
                    Log.warn( "Event '" + wat.kind() );
                    Log.info( abs + "'" );

                    IWatchEventHandler.EventAction eventAction;
                    // TODO: how to handle?
                    if ( kind == OVERFLOW ) {

                        eventAction = weha.onOverflow( abs, ce );

                    }
// path is modified
                    else if ( kind == ENTRY_MODIFY ) {

                        eventAction = weha.onModify( abs, ce );
                    } else if ( kind == ENTRY_DELETE ) {

                        eventAction = weha.onDelete( abs, ce );
                    } else if ( kind == ENTRY_CREATE ) {
                        // recursive && (
                        eventAction = weha.onCreate( abs, ce );
                        if ( eventAction == IWatchEventHandler.EventAction.ADVANCE
                                && recursive
                                && Files.isDirectory( abs, LinkOption.NOFOLLOW_LINKS ) ) {
                            // sub dir register
                            registerDir( abs );
                        }

                    } else {

                        // NOOP ?
                        // This case should not be occur
                        Log.error( "WatchEvent.Kind not known '" + kind + "'" );
                        break;
                    }

                    if ( eventAction == IWatchEventHandler.EventAction.STOP ) {
// user may stop watching


                        Log.info( "User Break!" );
                        break;
                    } else if ( eventAction == IWatchEventHandler.EventAction.SKIP ) {
// user may skip
                        Log.info( "User Event  continue" );
                        continue;
                    }
                }

                // THIS MUST BE
                boolean valid = key.reset();

                if ( !valid ) {

                    Path removed = watchKeyM.remove( key );
                    Log.info( "Path '" + removed + "' not valid  removed" );
                    // no more dirs to watch
                    if ( watchKeyM.isEmpty() ) {

                        Log.info( "No more dirs to watch. Stop watching." );
                        break;
                    }
                }
            }
            Log.info( "Ende Loop " );
            return null;
        }
        //  };
    }


}