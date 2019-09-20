package earth.eu.jtzipi.modules.node;

import java.util.List;
import java.util.function.Predicate;

/**
 * Node basic architecture.
 * <p>
 *  *  Each node has a
 *  *  <ul>
 *  *      <li>parent node (which maybe null in case of root)</li>
 *  *      <li>list of sub nodes (which maybe empty)</li>
 *  *      <li>value</li>
 *  *      <li>property leaf</li>
 *  *  </ul>
 *  *</p>
 * @param <T> type of content
 * @author jTzipi
 */
public interface INode<T> {

    /**
     * Parent node or null if root.
     * @return parent node
     */
    INode<T> getParent();

    /**
     * Is this node a leaf.
     * @return {@code true} if this node
     */
    boolean isLeaf();

    /**
     * Value this node is holding.
     * @return nodes value or {@code null}
     */
    T getValue();

    /**
     * List of sub nodes.
     * @return sub node
     */
    List<? extends INode<T>> getSubnodes();

    /**
     * List of sub nodes filtered.
     *
     * @param predicate filter
     * @return sub node
     */
    List<? extends INode<T>> getSubnodes( Predicate<T> predicate );
}
