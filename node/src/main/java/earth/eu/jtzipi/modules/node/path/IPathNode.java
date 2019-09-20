package earth.eu.jtzipi.modules.node.path;


import earth.eu.jtzipi.modules.node.INode;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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
 */
public interface IPathNode extends INode<Path> {

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
    /** Directories first then readable. */
    Comparator<IPathNode> DIR_FIRST_COMP = Comparator.comparing( IPathNode::isLeaf ).thenComparing(IPathNode::isReadable);

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
     * @return name
     */
    String getName();

    /**
     * Path description.
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
     * @return if path is regular
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
     * @return type
     */
    String getType();

    /**
     * List of sub nodes.
     * @return list of path wrapping sub node
     */
    List<? extends IPathNode> getSubnodes();

    List<? extends IPathNode> getSubnodes( Predicate<Path> predicate );


}
