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

package earth.eu.jtzipi.modules.io.watcher;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Watch Task I.
 * <p>
 * Watch task is a access if for watching directories for changes via
 * {@link java.nio.file.WatchService} .
 * A client may register directory paths for watching.
 *
 * </p>
 */
public interface IWatchTask {

    /**
     * Register path to watch.
     *
     * @param path dir
     * @throws IOException          if this path is not readable or not a directory
     * @throws NullPointerException if {@code path} is null
     */
    void register( Path path ) throws IOException;

    /**
     * Start watch.
     * <p>
     *
     * </p>
     */
    void watch();
}
