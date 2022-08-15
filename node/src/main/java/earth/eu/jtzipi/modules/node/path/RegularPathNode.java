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

package earth.eu.jtzipi.modules.node.path;


import earth.eu.jtzipi.modules.io.IOUtils;
import earth.eu.jtzipi.modules.io.PathInfo;
import earth.eu.jtzipi.modules.node.INode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Default Path Node.
 * <p>
 * Regular path node is a node which can be read by the jvm.
 *
 * @author jTzipi
 */
public class RegularPathNode implements IPathNode {

    private static final Logger LOG = LoggerFactory.getLogger( "PathNode" );


    /**
     * parent node. If null root.
     */
    private final IPathNode parent;
    /**
     * path to this node.
     */
    private final Path path;
    /**
     * Path is hidden.
     */
    private boolean hidden;
    //
    // Properties
    //


    /**
     * Type .
     */
    private String type;
    /**
     * Byte length .
     */
    private long length;
    /**
     * Path level.
     */
    private int depth;
    /**
     * Link
     */
    private boolean link;
    /**
     * dir.
     */
    private boolean dir;
    /**
     * Readable.
     */
    private boolean readable;

    /**
     * Indicator for subnodes created
     */
    private boolean subNodesCreated;
    /**
     * sub nodes.
     */
    private List<IPathNode> subNodeL;
    /**
     * Name .
     */
    private String name;
    /**
     * Description.
     */
    private String desc;
    /**
     * File Time (optional) .
     */
    private FileTime ftc;


    /**
     * PathNode main.
     *
     * @param filepath       path
     * @param parentPathNode parent path
     */
    RegularPathNode( final Path filepath, final IPathNode parentPathNode ) {

        this.parent = parentPathNode;
        this.path = filepath;
        this.subNodesCreated = false;
    }

    /**
     * Create new path node.
     *
     * @param path       path
     * @param parentNode parent node (maybe null if root)
     * @return PathNode for path and parent
     * @throws NullPointerException if {@code path} is
     */
    public static RegularPathNode of( final Path path, final IPathNode parentNode ) {

        Objects.requireNonNull( path );

        final RegularPathNode pn = new RegularPathNode( path, parentNode );

        pn.init( path );


        return pn;
    }

    private void init( final Path gadi ) {

        this.dir = PathInfo.isDir( gadi );
        this.readable = PathInfo.isReadable( gadi );

        this.name = PathInfo.fileSystemName( gadi );
        this.type = PathInfo.fileSystemTypeDesc( gadi );
        this.desc = PathInfo.fileSystemTypeDesc( gadi );
        this.depth = gadi.getNameCount();
        this.link = PathInfo.isLink( gadi );
        this.hidden = PathInfo.isHidden( gadi );
        this.length = PathInfo.getLength( gadi );
        this.ftc = IOUtils.getFileCreationTime( gadi );
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    @Override
    public boolean isLink() {
        return link;
    }

    @Override
    public boolean isDir() {
        return dir;
    }

    @Override
    public boolean isReadable() {
        return readable;
    }

    @Override
    public long getFileLength() {
        return length;
    }

    @Override
    public int getDepth() {

        return depth;
    }

    @Override
    public String getType() {

        return type;
    }

    @Override
    public boolean isHidden() {

        return hidden;
    }

    /**
     * Are sub nodes created.
     *
     * @return {@code true} if sub nodes are created
     */
    @Override
    public boolean isCreatedSubNode() {

        return this.subNodesCreated;
    }

    @Override
    public List<IPathNode> getSubnodes( final Predicate<? super Path> pp, final boolean streamDir ) {

        if ( !isDir() ) {
            LOG.debug( "Try to get subnodes of non dir" );
            return Collections.emptyList();
        }
        Objects.requireNonNull( pp );
        // if not created
        if ( !isCreatedSubNode() ) {

            if ( streamDir ) {

                try ( final Stream<Path> stream = Files.list( getValue() ) ) {
                    this.subNodeL = stream.filter( pp ).sorted().map( sp -> NodeProvider.create( sp, RegularPathNode.this ) ).collect( Collectors.toList() );
                } catch ( final IOException ioE ) {
                    LOG.warn( "Can not stream dir '" + getValue() + "'", ioE );
                }
            } else {

                try {
                    this.subNodeL = IOUtils.lookupDir( getValue(), pp )
                            .stream()
                            .sorted()
                            .map( sp -> NodeProvider.create( sp, RegularPathNode.this ) )
                            .collect( Collectors.toList() );
                } catch ( final IOException ioE ) {
                    LOG.warn( "Can not read dir '" + getValue() + "'", ioE );
                }
            }
            this.subNodesCreated = true;
        }

        return this.subNodeL;
    }

    @Override
    public INode<Path> getParent() {
        return parent;
    }

    @Override
    public Optional<FileTime> getCreated() {

        return Optional.ofNullable( ftc );
    }

    @Override
    public Path getValue() {
        return path;
    }

    @Override
    public boolean isLeaf() {

        return ( !isDir() && !isLink() ) || !isReadable();
    }

    @Override
    public int hashCode() {

        int res = Objects.hashCode( getValue() );
        res = 79 * res + Long.hashCode( getFileLength() );
        res = 79 * res + Objects.hashCode( getDesc() );
        res = 79 * res + Objects.hashCode( getName() );

        return res;
    }

    @Override
    public boolean equals( final Object object ) {
        if ( this == object ) {
            return true;
        }
        if ( !( object instanceof IPathNode ) ) {
            return false;
        }

        final IPathNode other = ( IPathNode ) object;
        final Path thisPath = getValue();
        final Path otherPath = other.getValue();


        return thisPath.normalize().equals( otherPath.normalize() );
    }

    @Override
    public String toString() {
        return "PathNode{" +
                "path=" + path +
                ", type='" + type + '\'' +
                ", length=" + length +
                ", depth=" + depth +
                ", link=" + link +
                ", dir=" + dir +
                ", readable=" + readable +
                ", subNodesCreated=" + subNodesCreated +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }

    @Override
    public int compareTo( final IPathNode pathNode ) {
        return COMP.compare( this, pathNode );
    }


}