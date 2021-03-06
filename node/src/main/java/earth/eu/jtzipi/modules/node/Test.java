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

package earth.eu.jtzipi.modules.node;

import earth.eu.jtzipi.modules.io.IOUtils;
import earth.eu.jtzipi.modules.node.path.IPathNode;
import earth.eu.jtzipi.modules.node.path.RegularPathNode;

import java.nio.file.Files;
import java.util.List;

public class Test {

    public static void main( final String[] args ) {

        final IPathNode node = RegularPathNode.of( IOUtils.getHomeDir(), null );

        final List<? extends IPathNode> l = node.getSubnodes( Files::isDirectory );


    }
}
