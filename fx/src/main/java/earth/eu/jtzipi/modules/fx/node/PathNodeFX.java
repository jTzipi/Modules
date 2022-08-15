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

package earth.eu.jtzipi.modules.fx.node;


import earth.eu.jtzipi.modules.io.IOUtils;
import earth.eu.jtzipi.modules.node.path.IPathNode;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * Wrapper around {@link IPathNode} for JavaFX nodes.
 */
public class PathNodeFX implements Comparable<PathNodeFX> {

    public static final FileTime NO_FILE_TIME = FileTime.fromMillis( 0L );
    private static final Logger LOG = LoggerFactory.getLogger( "PathNodeFX" );
    /**
     * IPath Object Property. Mutable.
     */
    private final ObjectProperty<IPathNode> fxPathNodeProp = new SimpleObjectProperty<>( this, "FX_PATH_NODE_PROP", null );
    // Read only properties.
    private final ReadOnlyLongWrapper fxPathNodeLengthPropROW = new ReadOnlyLongWrapper( this, "FX_PATH_NODE_LENGTH_PROP_READ_ONLY", 0L );         // Node Size prop
    private final ReadOnlyStringWrapper fxPathNodeSfxPropROW = new ReadOnlyStringWrapper( this, "FX_PATH_NODE_SUFFIX_PROP_READ_ONLY", null );             // Node Suffix prop
    private final ReadOnlyStringWrapper fxPathNodeTypePropROW = new ReadOnlyStringWrapper( this, "FX_PATH_NODE_TYPE_PROP_READ_ONLY", null );
    private final ReadOnlyObjectWrapper<FileTime> fxPathNodeFileTimeROW = new ReadOnlyObjectWrapper<>( this, "FX_PATH_NODE_TYPE_PROP_READ_ONLY", null );

    // -- optional --
    private final Map<String, StringProperty> hashPropM = new HashMap<>();


    PathNodeFX() {

        this.fxPathNodeProp.addListener( this::onChangePathNode );
    }

    /**
     * Create a new path node fx wrapping a path node.
     *
     * @param pathNode node
     * @return path node fx
     * @throws NullPointerException if {@code pathNode} is null
     */
    public static PathNodeFX of( final IPathNode pathNode ) {

        Objects.requireNonNull( pathNode );
        PathNodeFX pnfx = new PathNodeFX();
        pnfx.init( pathNode );

        return pnfx;
    }

    public final IPathNode getPathNode() {

        return fxPathNodeProp().get();
    }

    /**
     * Set path node to wrap.
     *
     * @param pathNode path node
     * @throws NullPointerException if {@code  pathNode} is null
     */
    public final void setPathNode( final IPathNode pathNode ) {

        Objects.requireNonNull( pathNode );

        fxPathNodeProp().set( pathNode );
    }

    public final ReadOnlyLongProperty fxPathLengthPropRO() {

        return this.fxPathNodeLengthPropROW.getReadOnlyProperty();
    }

    public final ReadOnlyStringProperty fxPathTypePropRO() {

        return this.fxPathNodeTypePropROW.getReadOnlyProperty();
    }

    public final ReadOnlyStringProperty fxPathSuffixPropRO() {

        return this.fxPathNodeSfxPropROW.getReadOnlyProperty();
    }


    public final ReadOnlyObjectProperty<FileTime> fxPathCreatedFileTimePropRO() {

        return this.fxPathNodeFileTimeROW.getReadOnlyProperty();
    }

    public final StringProperty fxPathHashProp( final String algoStr ) {

        if ( null == hashPropM.get( algoStr ) ) {

            hashPropM.put( algoStr, new SimpleStringProperty( this, "FX_PATH_NODE_HASH_" + algoStr + "_PROP" ) );
        }
        return hashPropM.get( algoStr );
    }


    final ObjectProperty<IPathNode> fxPathNodeProp() {

        return this.fxPathNodeProp;
    }


    private void init( final IPathNode pathNode ) {

        this.fxPathNodeProp.setValue( pathNode );
        this.fxPathNodeLengthPropROW.set( pathNode.getFileLength() );
        this.fxPathNodeTypePropROW.set( pathNode.getType() );
        this.fxPathNodeSfxPropROW.set( IOUtils.getPathSuffixSafe( pathNode.getValue() ) );
        this.fxPathNodeFileTimeROW.setValue( pathNode.getCreated().orElse( NO_FILE_TIME ) );
        this.hashPropM.clear();
    }

    private void onChangePathNode( ObservableValue<? extends IPathNode> obs, IPathNode oldPathNode, IPathNode newPathNode ) {

        LOG.info( "Change detected '" + obs + "'" );
        if ( null != newPathNode && oldPathNode != newPathNode ) {
            init( newPathNode );
        }

    }

    @Override
    public int compareTo( PathNodeFX o ) {

        return null == o ? -1 : getPathNode().compareTo( o.getPathNode() );
    }
}