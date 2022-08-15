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
import earth.eu.jtzipi.modules.io.OS;
import earth.eu.jtzipi.modules.node.INode;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.util.*;
import java.util.function.Predicate;

/**
 * Root path node.
 * <p>
 * This  is a special node which is the top most path node in each operating system.
 * </p>
 *
 * @author jTzipi
 */
public final class RootPathNode implements IPathNode {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger( "RootPath" );
    private final List<IPathNode> subNode = new ArrayList<>();
    private String rootName;
    private Path rootPath;

    private RootPathNode() {

    }


    /**
     * Return a root system node.
     *
     * @param os operating sys
     * @return root path node
     * @throws IOException          if some error reading
     * @throws NullPointerException if {@code os} is null
     */
    public static RootPathNode of( final OS os ) throws IOException {
        Objects.requireNonNull( os );

        final RootPathNode rpn = new RootPathNode();

        rpn.init( os );

        return rpn;
    }


    private void init( final OS os ) throws IOException {
        switch ( os ) {
            case WINDOWS:
                LOG.info( "Detected Windows" );
                initWin();
                break;
            case LINUX:
            case SOLARIS:
            case MAC:
                initUnixmac();
                break;
        }
    }


    private void initUnixmac() {

        final Path root = Paths.get( "/" );
        List<Path> rootDir;
//
        try {
            rootDir = IOUtils.lookupDir( root );
        } catch ( final IOException ioE ) {

            LOG.warn( "Failed to read root node '/'", ioE );
            rootDir = Collections.emptyList();
        }

        for ( final Path subPath : rootDir ) {

            final boolean readable = Files.isReadable( subPath );
            final IPathNode node = readable ? RegularPathNode.of( subPath, this ) : NotReadablePathNode.of( subPath, this );
            subNode.add( node );
        }

    }

    private void initWin() throws IOException {

        final FileSystem fs = FileSystems.getDefault();

        if ( null == fs ) {
            throw new IOException( "No FileSytem!" );
        }
        final Iterable<Path> userRoot = fs.getRootDirectories();

        // add a all user root
        // for windows we get the disks known
        // ATTENTION
        // some disks may not be readable duo to USB not found
        // so we need to add them as NotReadablePathNode
        for ( final Path usPath : userRoot ) {
            if ( Files.isReadable( usPath ) ) {
                subNode.add( RegularPathNode.of( usPath, this ) );
            } else {
                subNode.add( NotReadablePathNode.of( usPath, this ) );
            }
        }

        // set home dir of user
        subNode.add( RegularPathNode.of( IOUtils.getHomeDir(), this ) );

        // TODO add network node

        // set computer name
        rootName = System.getenv( "COMPUTERNAME" );
        rootPath = Paths.get( "/" );

    }


    @Override
    public String getName() {
        return rootName;
    }

    @Override
    public String getDesc() {
        return "OS Root Node";
    }

    @Override
    public boolean isLink() {
        return false;
    }

    @Override
    public boolean isDir() {
        return true;
    }

    @Override
    public boolean isReadable() {
        return true;
    }

    @Override
    public long getFileLength() {
        return IPathNode.DIR_LENGTH;
    }

    @Override
    public int getDepth() {
        return IPathNode.ROOT_DEPTH;
    }

    @Override
    public String getType() {

        return "root";
    }

    @Override
    public boolean isHidden() {

        return false;
    }

    @Override
    public boolean isCreatedSubNode() {

        return true;
    }

    @Override
    public List<IPathNode> getSubnodes( final Predicate<? super Path> predicate, final boolean streamDirProp ) {

        return subNode;
    }

    @Override
    public Optional<FileTime> getCreated() {

        return Optional.empty();
    }

    @Override
    public INode<Path> getParent() {

        return null;
    }

    @Override
    public Path getValue() {

        return rootPath;
    }

    @Override
    public boolean isLeaf() {

        return false;
    }

    @Override
    public int compareTo( final IPathNode pathNode ) {

        return COMP.compare( this, pathNode );
    }

    @Override
    public int hashCode() {

        int result = rootName != null ? rootName.hashCode() : 0;
        result = 31 * result + ( rootPath != null ? rootPath.hashCode() : 0 );
        return result;
    }

    @Override
    public boolean equals( final Object o ) {

        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        final RootPathNode that = ( RootPathNode ) o;

        if ( rootName != null ? !rootName.equals( that.rootName ) : that.rootName != null ) return false;
        return rootPath != null ? rootPath.equals( that.rootPath ) : that.rootPath == null;
    }
}