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

import earth.eu.jtzipi.modules.node.INode;

import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.text.Collator;
import java.util.*;
import java.util.function.Predicate;

/**
 * Path node architecture.
 * <p>
 *     A path node is a node with a {@link java.nio.file.Path} value.
 *     <br>
 *
 *     TODO: IOException
 * </p>
 *
 * @author jTzipi
 */
public interface IPathNode extends INode<Path> {


    // Collator for compare name

    Collator COL = Collator.getInstance();
    /** Default comparator. */
    Comparator<IPathNode> COMP = Comparator.comparing(IPathNode::isDir).thenComparing( IPathNode::isReadable ).thenComparing( ( pn, pn2 ) -> COL.compare( pn.getName(), pn2.getName()  ) ).reversed();
    /**
     * Length of a directory.
     */
    long DIR_LENGTH = -1L;
    /**
     * Depth of root node;
     */
    int ROOT_DEPTH = 0;

    /**
     * If this path name is not readable.
     */
    String PATH_NOT_READABLE = "<Not readable>";

    /**
     * Create a list of nodes until the root.
     * @param node node to create the path
     * @return list of path nodes to root
     */
    static List<IPathNode> getPathToRoot( IPathNode node ) {
        Objects.requireNonNull( node );

        List<IPathNode> pathL = new ArrayList<>();
        pathL.add( node );

        while( node.getParent() != null ) {

            node = ( IPathNode ) node.getParent();
            pathL.add( node );
        }
        return pathL;
    }

    /**
     * Name of path.
     * <p>
     *     That is the name of last path component.
     * </p>
     *
     * @return name
     */
    String getName();

    /**
     * Path description.
     * System dependent description of file.
     * @return description
     */
    String getDesc();

    /**
     * Path is a link to an other path.
     * @return link
     */
    boolean isLink();

    /**
     * Path is a directory.
     * @return is this path a directory
     */
    boolean isDir();

    /**
     * Path is readable by Java.
     * @return if path is regular readable
     */
    boolean isReadable();

    /**
     * File size of path in bytes or {@linkplain #DIR_LENGTH}.
     * @return length of content of path
     */
    long getFileLength();

    //  Optional<FileTime> getLastAccessTime();

    /**
     * Depth of path.
     * @return path depth
     */
    int getDepth();

    /**
     * Type of path.
     * System dependent description of file type.
     * Like image or folder.
     * @return type
     */
    String getType();

    /**
     * Is this path hidden.
     *
     * @return property hidden
     */
    boolean isHidden();

    /**
     * List of sub nodes.
     * @return list of path wrapping sub node
     */
    default List<? extends IPathNode> getSubnodes() {
        return getSubnodes( path ->  true );
    }

    /**
     * List of sub nodes.
     * @param predicate filter
     * @return list of path
     */
    List<? extends IPathNode> getSubnodes( Predicate<Path> predicate );

    /**
     * Return time of creation if readable.
     * @return created date if readable or Optional.empty();
     */
    Optional<FileTime> getCreated();

}
