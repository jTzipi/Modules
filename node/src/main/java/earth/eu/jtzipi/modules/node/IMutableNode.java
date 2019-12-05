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

/**
 * INode with capability to append custom node.
 * @param <T> type
 */
public interface IMutableNode<T> extends INode<T> {

    /**
     * Add node to this node's sub nodes.
     * @param node node
     * @return index
     * @throws NullPointerException
     */
    boolean addNode( INode<T> node );

    /**
     * Remove node from sub list if contained.
     * @param node node
     * @return removed node if found
     */
    boolean removeNode( INode<T> node );

    /**
     * Remove node with index.
     * @param index index &ge; 0
     * @return node if present
     * @throws IndexOutOfBoundsException if {@code index} is &lt; 0 or &; sub node size
     */
    INode<T> remove( int index );
}
