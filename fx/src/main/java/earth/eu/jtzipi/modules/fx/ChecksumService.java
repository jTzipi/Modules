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

import earth.eu.jtzipi.modules.fx.node.PathNodeFX;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.*;


public class ChecksumService extends Service<Map<PathNodeFX, String>> {

    private static final ExecutorService CSES = Executors.newCachedThreadPool();

    private static final ConcurrentMap<PathNodeFX, String> CACHE_MAP = new ConcurrentHashMap<>();

    // message digest property
    private final ObjectProperty<String> fxMDProp = new SimpleObjectProperty<>( this, "FX_HASH_ALGO_DIGEST_PROP", MessageDigestAlgorithms.SHA_512 );
    // files to calculate hash value
    private ObservableList<PathNodeFX> fxPathFXNodeList = FXCollections.emptyObservableList();

    @Override
    protected Task<Map<PathNodeFX, String>> createTask() {

        Set<PathNodeFX> pathSet = new HashSet<>( new ArrayList<>( fxPathFXNodeList ) );

        return new ChecksumTaskSimple( pathSet, fxMDProp.getValue() )
                ;
    }

    public String getDigestAlgo() {

        return fxMDProp.getValue();
    }

    public void setDigestAlgo( String algoStr ) {

        Objects.requireNonNull( algoStr );
        fxMDProp.setValue( algoStr );
    }


    public void setFXNodes( List<PathNodeFX> pathNodeList ) {

        Objects.requireNonNull( pathNodeList );
        fxPathFXNodeList = FXCollections.observableList( pathNodeList );
    }


    private static class ChecksumTaskSimple extends Task<Map<PathNodeFX, String>> {
        private final Set<PathNodeFX> pathS;
        private final String mdAlgo;

        ChecksumTaskSimple( final Set<PathNodeFX> pathNodeSet, final String messageDigestAlgoStr ) {

            this.pathS = pathNodeSet;
            this.mdAlgo = messageDigestAlgoStr;
        }

        @Override
        protected Map<PathNodeFX, String> call() {

            Map<PathNodeFX, String> resultM = new HashMap<>();
            DigestUtils du = new DigestUtils( mdAlgo );

            long len = pathS.size();
            long c = 0;
            updateProgress( 0, len );
            for ( PathNodeFX pathNode : pathS ) {

                try {
                    String hex = du.digestAsHex( pathNode.getPathNode().getValue() );
                    pathNode.fxPathHashProp( mdAlgo ).setValue( hex );
                    resultM.put( pathNode, hex );
                } catch ( final IOException ioE ) {
                    setException( ioE );
                    pathNode.fxPathHashProp( mdAlgo ).setValue( "Error computing Hash" );
                }

                updateProgress( ++c, len );

            }
            return resultM;
        }

    }

    private static class ChecksumTask extends Task<Map<PathNodeFX, String>> {

        private static final org.slf4j.Logger LOG = LoggerFactory.getLogger( "ChecksumTask" );

        private final CompletionService<Map.Entry<PathNodeFX, String>> NODE_FX_HASH_CS = new ExecutorCompletionService<>( CSES );

        private final Set<PathNodeFX> pathS;
        private final String mdAlgo;

        ChecksumTask( final Set<PathNodeFX> pathNodeSet, final String messageDigestAlgoStr ) {

            this.pathS = pathNodeSet;
            this.mdAlgo = messageDigestAlgoStr;
        }

        @Override
        protected Map<PathNodeFX, String> call() {


            MessageDigest md = DigestUtils.getDigest( mdAlgo, DigestUtils.getSha512Digest() );
            Map<PathNodeFX, String> resultM = new HashMap<>();

            long toCalc = 0L;

            for ( PathNodeFX nodeFX : pathS ) {

                if ( !CACHE_MAP.containsKey( nodeFX ) ) {

                    Callable<Map.Entry<PathNodeFX, String>> hc = () -> Map.entry( nodeFX, Hex.encodeHexString( DigestUtils.digest( md, nodeFX.getPathNode().getValue() ) ) );
                    // submit -> thread
                    NODE_FX_HASH_CS.submit( hc );
                    toCalc++;
                } else {
                    resultM.put( nodeFX, CACHE_MAP.get( nodeFX ) );
                }
            }

            LOG.info( "Calculating sha of " + toCalc + " path" );
            // nothing added
            if ( 0L == toCalc ) {
                LOG.info( "Nothing todo..." );

            } else {

                updateProgress( 0L, toCalc );
                // here we can do something other


                for ( int c = 0; c < toCalc; c++ ) {

                    try {
                        // blocking throws IE
                        Future<Map.Entry<PathNodeFX, String>> result = NODE_FX_HASH_CS.take();
                        // throws EE
                        Map.Entry<PathNodeFX, String> me = result.get();

                        PathNodeFX pathNode = me.getKey();
                        String sha = me.getValue();

                        LOG.info( "Path '" + pathNode.getPathNode().getValue() + "' hash '" + sha + "'" );
                        // set hash
                        // TODO: FX Thread?
                        pathNode.fxPathHashProp( mdAlgo ).setValue( sha );

                        // put to cache
                        CACHE_MAP.put( pathNode, sha );


                        updateProgress( c + 1, toCalc );
                    } catch ( InterruptedException iE ) {
                        Thread.currentThread().interrupt();
                        LOG.warn( "Calc interrupted!", iE );
                    } catch ( ExecutionException ee ) {

                        LOG.warn( "Error occurs!", ee );
                        setException( ee );
                        failed();
                    }
                }
            }
            return resultM;
        }
    }
}
