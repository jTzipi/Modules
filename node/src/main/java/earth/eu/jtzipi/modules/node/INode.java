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
