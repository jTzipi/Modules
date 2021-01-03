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
import earth.eu.jtzipi.modules.node.INode;

import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;


public final class NotReadablePathNode implements IPathNode, Comparable<IPathNode> {

    private final Path path;
    private final IPathNode parent;

    private String name;
    private String desc;


    /**
     * @param path       path
     * @param parentNode parent node
     */
    NotReadablePathNode( final Path path, final IPathNode parentNode ) {
        this.path = path;
        this.parent = parentNode;
    }

    /**
     * Create instance of NotReadablePathNode.
     *
     * @param path       path
     * @param parentNode parent
     * @return created node
     */
    public static NotReadablePathNode of( final Path path, final IPathNode parentNode ) {
        Objects.requireNonNull( path );
        final NotReadablePathNode pathNode = new NotReadablePathNode( path, parentNode );
        pathNode.init( path );

        return pathNode;
    }

    private void init( final Path path ) {

        name = IOUtils.getPathDisplayName( path );
        desc = IOUtils.getPathTypeDescription( path );

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
        return false;
    }

    @Override
    public boolean isDir() {
        return false;
    }

    @Override
    public boolean isReadable() {
        return false;
    }

    @Override
    public long getFileLength() {
        return 0;
    }

    @Override
    public int getDepth() {
        return 0;
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public List<IPathNode> getSubnodes( final Predicate<? super Path> predicate ) {
        return Collections.emptyList();
    }

    @Override
    public Optional<FileTime> getCreated() {
        return Optional.empty();
    }

    @Override
    public INode<Path> getParent() {
        return parent;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public Path getValue() {
        return path;
    }

    @Override
    public int hashCode() {
        int result = path.hashCode();
        result = 31 * result + ( name != null ? name.hashCode() : 0 );
        result = 31 * result + ( desc != null ? desc.hashCode() : 0 );
        return result;
    }

    @Override
    public boolean equals( final Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        final NotReadablePathNode that = ( NotReadablePathNode ) o;

        if ( !path.equals( that.path ) ) return false;
        if ( name != null ? !name.equals( that.name ) : that.name != null ) return false;
        return desc != null ? desc.equals( that.desc ) : that.desc == null;
    }

    @Override
    public int compareTo( final IPathNode iPathNode ) {
        return COMP.compare( this, iPathNode );
    }
}
