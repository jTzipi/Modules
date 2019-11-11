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

package earth.eu.jtzipi.modules.node.path;


import earth.eu.jtzipi.modules.io.IOUtils;
import earth.eu.jtzipi.modules.node.INode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Default Path Node.
 * <p>
 *     Regular path node is a node which can be read by the jvm.
 *
 *
 * @author jTzipi
 */
public class RegularPathNode implements IPathNode, Comparable<IPathNode> {

    private static final Logger LOG = LoggerFactory.getLogger( "PathNode" );




    /** parent node. If null root. */
    private IPathNode parent;
    /** path to this node. */
    private Path path;
    /** Path is hidden. */
    boolean hidden;
    //
    // Properties
    //


    /** Type .*/
    String type;
    /** Byte length .*/
    long length;
    /** Path level. */
    int depth;
    /** Link */
    boolean link;
    /** dir. */
    boolean dir;
    /** Readable. */
    boolean readable;

    /** Indicator for subnodes created */
    boolean subNodesCreated;
    /** sub nodes.*/
    private List<? extends IPathNode> subNodeL;
    /** Name .*/
    String name;
    /** Description.*/
    String desc;
    /** File Time (optional) . */
    FileTime ftc;
    /**
     * PathNode main.
     * @param path
     * @param parentPathNode
     */
    RegularPathNode( final Path path, final IPathNode parentPathNode ) {

        this.parent = parentPathNode;
        this.path = path;
    }

    /**
     * Create new path node.
     * @param path path
     * @param parentNode parent node (maybe null if root)
     * @return PathNode for path and parent
     * @throws NullPointerException if {@code path} is
     */
    public static RegularPathNode of( final Path path, final IPathNode parentNode )  {
        Objects.requireNonNull(path);

        RegularPathNode pn = new RegularPathNode( path, parentNode );

        pn.init( path );


        return pn;
    }

    private void init( final Path path )  {
        this.dir = Files.isDirectory( path );
        try {
            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);

            this.length = dir? DIR_LENGTH : attrs.size( );

            ftc = attrs.creationTime();
        } catch ( IOException e ) {

            this.length = 0L;
            ftc = FileTime.fromMillis( 0L );
        }
        try{
            this.type = Files.probeContentType( path );
        } catch ( final IOException ioE ) {
            this.type = "<Unknown>";
        }


        try {
            this.hidden = Files.isHidden( path );
        } catch ( IOException e ) {

            this.hidden = false;
        }
        this.readable = Files.isReadable(path);
        this.subNodesCreated = false;
        this.name = IOUtils.getPathDisplayName( path );
        this.desc = IOUtils.getPathTypeDescription( path );
        this.depth = path.getNameCount();
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

    @Override
    public List<? extends IPathNode> getSubnodes( Predicate<Path> pp ) {
        if(!isDir()) {
            return Collections.emptyList();
        }
        Objects.requireNonNull( pp );
        // if not created
        if( !subNodesCreated ) {


            this.subNodeL = IOUtils.lookupDir( getValue() )
                    .stream()
                    .filter(pp)
                    .sorted()
                    .map( sp -> NodeProvider.create( sp, RegularPathNode.this ) )
                    .collect( Collectors.toList() );
            this.subNodesCreated = true;
        }

        return this.subNodeL;
    }

    @Override
    public INode<Path> getParent() {
        return parent;
    }

    @Override
    public boolean isLeaf() {
        return (!isDir() && !isLink()) || !isReadable();
    }

    @Override
    public Path getValue() {
        return path;
    }

    @Override
    public Optional<FileTime> getCreated() {
        return Optional.of( ftc );
    }

    @Override
    public int hashCode() {
        int res = Objects.hashCode( getValue() );
        res= 79 * res + Long.hashCode( getFileLength() );
        res= 79 * res + Objects.hashCode( getDesc() );
        res = 79 * res + Objects.hashCode( getName() );

        return res;
    }

    @Override
    public boolean equals( Object object ) {
        if( this == object ) {
            return true;
        }
        if( !(object instanceof IPathNode) ) {
            return false;
        }

        IPathNode other = (IPathNode) object;
        Path thisPath = getValue();
        Path otherPath = other.getValue();


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
    public int compareTo( IPathNode pathNode ) {
        return COMP.compare( this, pathNode );
    }


}
