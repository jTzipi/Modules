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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Base class for mutable nodes.
 *
 * @param <T> type
 */
public abstract class AbstractMutableNode<T> implements IMutableNode<T> {

    protected INode<T> parentNode;
    protected T value;
    protected List<INode<T>> subL;

    protected AbstractMutableNode(INode<T> parent, T val) {
        this.parentNode = parent;
        this.value = val;
        this.subL = new ArrayList<>();
    }

    @Override
    public boolean addNode( INode<T> node ) {
        return subL.add( node );
    }

    @Override
    public boolean removeNode( INode<T> node ) {
        if( !subL.contains( node ) ) {
            throw new IllegalArgumentException( "" );
        }
        return subL.remove( node );
    }

    @Override
    public INode<T> remove( int index ) {
        return subL.remove( index );
    }

    @Override
    public INode<T> getParent() {
        return parentNode;
    }


    @Override
    public T getValue() {
        return value;
    }

    @Override
    public List<? extends INode<T>> getSubnodes() {
        return getSubnodes( p -> true );
    }

    @Override
    public List<? extends INode<T>> getSubnodes( Predicate<? super T> predicate ) {
        return subL.stream().filter( nd -> predicate.test( nd.getValue() ) ).collect( Collectors.toList() );
    }

    protected abstract void createSubNodeList();
}
