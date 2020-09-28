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

package earth.eu.jtzipi.modules.io;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Example to watch a directory (or tree) for changes to files.
 */

public class WatchDir {

    private final WatchService watcher;
    private final Map<WatchKey, Path> keys;
    private final boolean recursive;
    private boolean trace = false;

    /**
     * Creates a WatchService and registers the given directory
     */
    WatchDir( Path dir, boolean recursive ) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<>();
        this.recursive = recursive;

        if ( recursive ) {
            System.out.format( "Scanning %s ...\n", dir );
            registerAll( dir );
            System.out.println( "Done." );
        } else {
            register( dir );
        }

        // enable trace after initial registration
        this.trace = true;
    }

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast( WatchEvent<?> event ) {
        return ( WatchEvent<T> ) event;
    }

    static void usage() {
        System.err.println( "usage: java WatchDir [-r] dir" );
        System.exit( -1 );
    }

    public static void main( String[] args ) throws IOException {
        // parse arguments
        if ( args.length == 0 || args.length > 2 )
            usage();
        boolean recursive = false;
        int dirArg = 0;
        if ( args[0].equals( "-r" ) ) {
            if ( args.length < 2 )
                usage();
            recursive = true;
            dirArg++;
        }

        // register directory and process its events
        Path dir = Paths.get( args[dirArg] );
        new WatchDir( dir, recursive ).processEvents();
    }

    /**
     * Register the given directory with the WatchService
     */
    private void register( Path dir ) throws IOException {
        WatchKey key = dir.register( watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY );
        if ( trace ) {
            Path prev = keys.get( key );
            if ( prev == null ) {
                System.out.format( "register: %s\n", dir );
            } else {
                if ( !dir.equals( prev ) ) {
                    System.out.format( "update: %s -> %s\n", prev, dir );
                }
            }
        }
        keys.put( key, dir );
    }

    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void registerAll( final Path start ) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree( start, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory( Path dir, BasicFileAttributes attrs )
                    throws IOException {
                register( dir );
                return FileVisitResult.CONTINUE;
            }
        } );
    }

    /**
     * Process all events for keys queued to the watcher
     */
    void processEvents() {
        for ( ; ; ) {

            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch ( InterruptedException x ) {
                return;
            }

            Path dir = keys.get( key );
            if ( dir == null ) {
                System.err.println( "WatchKey not recognized!!" );
                continue;
            }

            for ( WatchEvent<?> event : key.pollEvents() ) {
                WatchEvent.Kind<?> kind = event.kind();

                // TBD - provide example of how OVERFLOW event is handled
                if ( kind == OVERFLOW ) {
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast( event );
                Path name = ev.context();
                Path child = dir.resolve( name );

                // print out event
                System.out.format( "%s: %s\n", event.kind().name(), child );

                // if directory is created, and watching recursively, then
                // register it and its sub-directories
                if ( recursive && ( kind == ENTRY_CREATE ) ) {
                    try {
                        if ( Files.isDirectory( child, NOFOLLOW_LINKS ) ) {
                            registerAll( child );
                        }
                    } catch ( IOException x ) {
                        // ignore to keep sample readbale
                    }
                }
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if ( !valid ) {
                keys.remove( key );

                // all directories are inaccessible
                if ( keys.isEmpty() ) {
                    break;
                }
            }
        }
    }
}

