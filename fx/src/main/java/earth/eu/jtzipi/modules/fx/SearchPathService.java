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

package earth.eu.jtzipi.modules.fx;


import earth.eu.jtzipi.modules.io.task.PathCrawler;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


/**
 * Search Path Service.
 * <p>
 * This is a javaFX Service to look for {@linkplain java.nio.file.Path}s.
 *
 * </p>
 */
public class SearchPathService extends Service<List<Path>> {


    private ObjectProperty<List<Path>> fxRootPathProp = new SimpleObjectProperty<>( this, "FX_SEAR" );
    private ObjectProperty<Predicate<Path>> fxPathPredicateProp = new SimpleObjectProperty<>( this, "F" );
    private ReadOnlyObjectWrapper<Path> fxSearchPathROWProp = new ReadOnlyObjectWrapper<>( this, "", null );

    /**
     * Search Path Service.
     *
     * @param rootList  root path
     * @param predicate path predicate
     */
    SearchPathService( final List<Path> rootList, final Predicate<Path> predicate ) {

        this.fxRootPathProp.setValue( rootList );
        this.fxPathPredicateProp.setValue( predicate );
        this.fxRootPathProp.addListener( this::onPathChanged );
        this.fxPathPredicateProp.addListener( this::onPredicateChanged );
    }

    /**
     * Create search service for predicate and one or more root paths.
     *
     * @param predicate predicate
     * @param rootPath  mandatory root
     * @param other     optional root
     * @return Service to Search
     * @throws NullPointerException if {@code predicate}|{@code rootPath}
     */
    public static SearchPathService of( Predicate<Path> predicate, Path rootPath, Path... other ) {

        Objects.requireNonNull( predicate, "You have no path predicate" );
        Objects.requireNonNull( rootPath, "Root path is null" );
        List<Path> pathL = new ArrayList<>();
        pathL.add( rootPath );
        if ( null != other ) {
            Collections.addAll( pathL, other );
        }

        return of( predicate, pathL );
    }

    /**
     * Create search service for predicate and list of root paths.
     *
     * @param pathPred predicate
     * @param root     root path
     * @return search for path service
     * @throws NullPointerException if {@code pathPred}|{@code root}
     */
    public static SearchPathService of( Predicate<Path> pathPred, List<Path> root ) {

        Objects.requireNonNull( root, "Root path list is null!" );
        Objects.requireNonNull( pathPred, "You have no path predicate" );
        return new SearchPathService( root, pathPred );
    }

    /**
     * Return root path list property.
     *
     * @return fx root path property
     */
    public ObjectProperty<List<Path>> getRootPathPropFX() {

        return fxRootPathProp;
    }

    /**
     * Return predicate property.
     *
     * @return fx predicate property
     */
    public ObjectProperty<Predicate<Path>> getPathPredicatePropFX() {

        return fxPathPredicateProp;
    }

    /**
     * Return current dir searching for path.
     *
     * @return current dir searching
     */
    public ReadOnlyObjectProperty<Path> getCurrentDirPropFX() {

        return fxSearchPathROWProp.getReadOnlyProperty();
    }

    private void onPathChanged( ObservableValue<? extends List<Path>> obs, List<Path> oldPathList, List<Path> newPathList ) {

    }

    private void onPredicateChanged( ObservableValue<? extends Predicate<Path>> obs, Predicate<Path> oldPredicate, Predicate<? super Path> newpredicate ) {

    }

    @Override
    protected Task<List<Path>> createTask() {

        return new SearchPathTask( fxRootPathProp.getValue(), fxPathPredicateProp.getValue() );
    }

    private static final class SearchPathTask extends Task<List<Path>> {

        private static final org.slf4j.Logger LOG = LoggerFactory.getLogger( "TaskSearch" );
        private static final ExecutorService ES = Executors.newCachedThreadPool();
        private final Predicate<Path> predicate;
        private final List<Path> rootPathL;

        private SearchPathTask( final List<Path> pathL, final Predicate<Path> predicate ) {

            this.rootPathL = pathL;
            this.predicate = predicate;
        }

        @Override
        protected List<Path> call() {

            final BlockingQueue<Path> bQ = new LinkedBlockingQueue<>();
            final List<Path> foundPathL = new ArrayList<>();

            final Set<Path> roots = rootPathL.stream().map( Path::getRoot ).collect( Collectors.toSet() );

            int rs = roots.size();
            final Set<Future<?>> futureTaskS = new HashSet<>( rs );
            for ( Path root : roots ) {

                LOG.info( "Start search '" + root + "'" );
                PathCrawler pc = PathCrawler.of( root, predicate, bQ );
                futureTaskS.add( ES.submit( pc ) );
            }

            while ( true ) {

                try {
                    Path newPath = bQ.take();

                    // search finished
                    if ( PathCrawler.__NULL__.equals( newPath ) ) {

                        rs--;
                        // all thread are finished
                        if ( rs == 0 ) {
                            break;
                        }

                    }
                    foundPathL.add( newPath );

                    updateValue( foundPathL );
                } catch ( final InterruptedException iE ) {


                    LOG.warn( "Thread ie! Cancel all running task." );
                    // Re set thread state
                    Thread.currentThread().interrupt();
                    // cancel all running
                    for ( Future<?> f : futureTaskS ) {
                        f.cancel( true );
                    }
                }

            }

            return foundPathL;
        }
    }

}