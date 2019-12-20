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

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Watch Service Builder.
 * <p>
 * Basically a wrapper for {@linkplain WatchService}
 * </p>
 *
 * @author
 */
public final class Watcher {

    private static final Logger Log = LoggerFactory.getLogger( "Watcher" );

    /**
     * Watchkeys for all events.
     */
    public static final List<WatchEvent.Kind<?>> WATCH_KEY_EVENT_ALL = Arrays.asList( ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE );
    private final WatchService ws;

    /**
     * No Access.
     */
    private Watcher( final WatchService ws ) {
        this.ws = ws;
    }

    /**
     * Create Watcher.
     * <p>
     * Here we create a new instance of WatchService.
     * This service will later used for all watch tasks.
     * </p>
     *
     * @param ws WatchService
     * @return Watcher
     *
     */
    public static Watcher create( WatchService ws ) {
        Objects.requireNonNull( ws );
        Watcher watcher = new Watcher( ws );
        watcher.init();
        return watcher;
    }

    /**
     * Try stop watch service.
     * @throws IOException if close failed
     */
    public void stop() throws IOException {
        this.ws.close();
    }

    private void init() {

    }

    /**
     * Create a handler for watch dir.
     * <p>
     *     The path must be a readable directory.
     *     You have option to <i>trace</i> {@code path} that is watch for new registration on this path.
     *     You can register
     * </p>
     * @param path path to dir
     * @param traceProp trace path property
     * @param recursiveProp register recursive property
     * @param watchEventHandler handler for watch events
     * @param eventType list of event types
     * @return watch task for path
     * @throws IOException          if {@code path} is not readable or is not a dir
     * @throws NullPointerException if {@code path} is null
     */
    public IWatchTask forPath( final Path path, final boolean traceProp, final boolean recursiveProp, final IWatchEventHandler watchEventHandler, final List<WatchEvent.Kind<?>> eventType ) throws IOException {
        Objects.requireNonNull( path );
        // check readable
        if ( !Files.isReadable( path ) ) {

            throw new IOException( "Dir '" + path + "' is not readable" );
        }
        // check dir
        if ( !Files.isDirectory( path ) ) {

            throw new IOException( "Path '" + path + "' seems to be not of type directory" );
        }

        WatchTask watchTask = new WatchTask( traceProp, recursiveProp, watchEventHandler, eventType );
        watchTask.register( path );


        return watchTask;
    }

    /**
     * Create a handler for watch dir.
     * @param dir dir to watch
     * @param watchEventHandler watch event handler
     * @return task
     * @throws IOException if {@code dir} is not readable or is not a dir
     *
     */
    public IWatchTask forPath( final Path dir, IWatchEventHandler watchEventHandler ) throws IOException {

        return forPath( dir, false, false, watchEventHandler, WATCH_KEY_EVENT_ALL );
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
    private class WatchTask implements IWatchTask {

        // store watch keys for paths
        private final Map<WatchKey, Path> watchKeyM = new HashMap<>();

        private final List<WatchEvent.Kind<?>> weks;    // Events to watch for
        private final boolean recursive;                      // watch sub dirs
        private final boolean trace;                          // trace
        private final IWatchEventHandler weha;                // Watch event

        private WatchTask( final boolean trace, final boolean recursive, final IWatchEventHandler watchEventHandler, List<WatchEvent.Kind<?>> watchKeyEventL ) {

            this.trace = trace;
            this.recursive = recursive;
            this.weha = watchEventHandler;
            this.weks = watchKeyEventL;
        }

        @Override
        public void register( final Path dirPath ) throws IOException {
            if ( !Files.isReadable( dirPath ) ) {
                throw new IOException( "Can not read '" + dirPath + "" );
            }
            if ( !Files.isDirectory( dirPath ) ) {
                throw new IOException( "Path '" + dirPath + "' is not a dir" );
            }

            // register Path
            WatchKey key = dirPath.register( ws, weks.toArray( new WatchEvent.Kind<?>[0] ) );
            // trace old entries
            if ( trace ) {
                Path oldPath = watchKeyM.get( key );
                // new entry
                if ( null == oldPath ) {

                    Log.info( "Watching '" + dirPath + "'" );
                } else {

                        if ( oldPath.equals( dirPath ) ) {
                             Log.info( "Try registering same dir '" + dirPath + "'" );
                        }

                        Log.info( "Updating '" + dirPath + "'" );
                }
                }

            Log.info( " Register '" + dirPath );
            this.watchKeyM.put( key, dirPath );


        }

        @Override
        public void watch() {

            // if nothing to watch
            if ( watchKeyM.isEmpty() ) {

                Log.warn( "No directories to watch!" );
                return;
            }
            Log.info( "[WATCH SERVICE] Start WatchTask'" );


// Start handling
            for ( ; ; ) {

                WatchKey key;
                try {
                    key = ws.take();
                } catch ( InterruptedException iE ) {
                    Log.warn( "IE ... stop!" );

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
                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> wat = ( WatchEvent<Path> ) event;  // cast
                    WatchEvent.Kind<?> kind = wat.kind();                  //
                    int ce = wat.count();                               //
                    Path path = wat.context();                          // name of path
                    Path abs = dirEvent.resolve( path );                     // resolve against
                    Log.warn( "Event '" + wat.kind() );
                    Log.info( abs + "'" );

                    IWatchEventHandler.EventAction eventAction;
                    // TODO: how to handle?
                    if ( kind == OVERFLOW ) {

                        eventAction = weha.onOverflow( abs, ce );

                    } else if ( kind == ENTRY_MODIFY ) {

                        // path is modified
                        eventAction = weha.onModify( abs, ce );
                    } else if ( kind == ENTRY_DELETE ) {
                        // path deleted
                        eventAction = weha.onDelete( abs, ce );
                    } else if ( kind == ENTRY_CREATE ) {
                        // recursive && (
                        eventAction = weha.onCreate( abs, ce );
                        if ( eventAction == IWatchEventHandler.EventAction.ADVANCE
                                && recursive
                                && Files.isDirectory( abs, LinkOption.NOFOLLOW_LINKS ) ) {
                            // sub dir register
                            List<Path> error = registerDir( abs );
                            Log.warn( "Dirs not watch '" + error );
                        }

                    } else {

                        // NOOP ?
                        // This case should not be occur
                        Log.error( "WatchEvent.Kind not known '" + kind + "'" );
                        break;
                    }

                    // user action on event
                    //

                    // user may stop watching
                    if ( eventAction == IWatchEventHandler.EventAction.STOP ) {

                        beforeStop( "User Break!" );
                        break;
                    } else if ( eventAction == IWatchEventHandler.EventAction.SKIP ) {
                        // user may skip all other event
                        Log.info( "User Event  continue" );
                        //continue;
                    }
                }

                // THIS MUST BE
                boolean valid = key.reset();

                if ( !valid ) {

                    Path removed = watchKeyM.remove( key );
                    Log.info( "Path '" + removed + "' not valid  removed" );
                    // no more dirs to watch
                    if ( watchKeyM.isEmpty() ) {

                        beforeStop( "No more dirs to watch. Stop watching." );
                        break;
                    }
                }
            }
            Log.info( "Ende Loop " );

        }

//        private void init() throws IOException {
//
//            if ( recursive ) {
//
//                // register all path and sub path
//                List<Path> failedL = registerDir( dir );
//
//                Log.info( "Dirs we can not watch '" + failedL + "'" );
//                // no dirs registered
//                if ( watchKeyM.isEmpty() ) {
//
//                    throw new IOException( "Dir '" + dir + "' is not registered" );
//                }
//
//            } else {
//
//                // register dir
//
//register( dir );
//
//            }
//
//        }

        private List<Path> registerDir( final Path dir ) {
            List<Path> failedL = new ArrayList<>();


            try {
                Files.walkFileTree( dir, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory( Path dirPath, BasicFileAttributes attrs ) {


                        try {
                            register( dirPath );
                        } catch ( final IOException ioE ) {
                            failedL.add( dirPath );
                        }

                        return FileVisitResult.CONTINUE;
                    }


                } );
            } catch ( IOException e ) {

                Log.error( "IO during register", e );
            }

            return failedL;
        }
        //  };

        private void beforeStop( String info ) {
            Log.info( info );

        }

    }


}