/*
 * Copyright (c) 2021 Tim Langhammer
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


import earth.eu.jtzipi.modules.utils.Utils;
import org.slf4j.LoggerFactory;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


/**
 * Watcher for changes of system root nodes.
 * <p>
 * This may be particular usefull if you want to listen to file system root node
 * changes like removable drives.
 * <p>
 * This is an enum type with purpose.
 * We do not want to create more then one instance of this class even via reflection.
 */
public enum SystemNodeWatcher {

    /**
     * Singleton.
     */
    SINGLETON;


    public static final Long MIN_POLL_RATE = 1L;
    public static final Long MIN_DELAY = 0L;
    private static final ScheduledExecutorService ES = Executors.newSingleThreadScheduledExecutor();
    private static final FileSystem FS = FileSystems.getDefault();
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger( "SysNodeWatcher" );
    private ScheduledFuture<?> schedf;

    /**
     * Create a new watcher and start watching for root changes.
     *
     * @param pathConsumer path consumer
     * @param delay        initial delay millisecond &ge; 0
     * @param pollRate     poll rate millisecond &gt; 0
     * @throws NullPointerException  if {@code pathConsumer} is null
     * @throws IllegalStateException if service shut down
     */
    public void startWatch( final Consumer<? super List<Path>> pathConsumer, long delay, long pollRate ) {
        Objects.requireNonNull( pathConsumer );
        if ( ES.isShutdown() || ES.isTerminated() ) {
            throw new IllegalStateException( "Service down" );
        }
        delay = Utils.clamp( delay, MIN_DELAY, Long.MAX_VALUE );
        pollRate = Utils.clamp( pollRate, MIN_POLL_RATE, Long.MAX_VALUE );

        final Watcher w = new Watcher( pathConsumer );
        schedf = ES.scheduleAtFixedRate( w, delay, pollRate, TimeUnit.MILLISECONDS );
    }

    /**
     * Stop this service.
     *
     * @throws IllegalStateException if ExecutorService is not running
     */
    public void stopWatch() {

        if ( ES.isShutdown() || ES.isTerminated() ) {
            throw new IllegalStateException( "Service down" );
        }
        if ( null == schedf ) {
            LOG.warn( "No task started!" );
            return;
        }


        schedf.cancel( true );
        ES.shutdown();
    }

    /**
     * This is our polling runnable .
     */
    private static final class Watcher implements Runnable {
        private final Consumer<? super List<Path>> pathC;
        private final List<Path> pl = new ArrayList<>();

        /**
         * Watcher.
         *
         * @param pathCmr path consumer
         */
        private Watcher( final Consumer<? super List<Path>> pathCmr ) {
            this.pathC = pathCmr;
        }

        @Override
        public void run() {

            pl.clear();
            FS.getRootDirectories().iterator().forEachRemaining( pl::add );

            pathC.accept( pl );


        }
    }
}
