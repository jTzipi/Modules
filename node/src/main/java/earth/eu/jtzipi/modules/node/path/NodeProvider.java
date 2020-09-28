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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

final class NodeProvider {

    private NodeProvider() {

    }

    public static IPathNode create( Path path, IPathNode parentPathNode ) {
        Objects.requireNonNull( path );

        if ( !Files.isReadable( path ) ) {

            return NotReadablePathNode.of( path, parentPathNode );
        }

        String sfx = IOUtils.getPathSuffixSafe( path );

        IPathNode node;



            switch ( sfx ) {
                case "zip":
                    node = ZipPathNode.of( path, parentPathNode ); break;
                default: node = RegularPathNode.of( path , parentPathNode );
            }

return node;
    }

}
