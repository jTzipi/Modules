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

import java.nio.file.Path;

/**
 * Watch Event Handler.
 *
 * If you want to handle one or all events a directory watcher listen for.
 *
 * To control flow of watcher you must return one of {@code EventAction}.
 * E.G. to stop further watching you must return {@linkplain EventAction#STOP}.
 *
 * @author jTzipi
 */
public interface IWatchEventHandler {

    /**
     * If overflow occur.
     *
     * @param path path
     * @param cnt  how often
     * @return event action
     */
    EventAction onOverflow( final Path path, final int cnt );

    /**
     * A path is created.
     *
     * @param path path
     * @param cnt  how often
     * @return event action
     */
    EventAction onCreate( final Path path, final int cnt );

    /**
     * If modify event occur.
     *
     * @param path path
     * @param cnt  how often
     * @return event action
     */
    EventAction onModify( final Path path, final int cnt );

    /**
     * A path is deleted.
     *
     * @param path path
     * @param cnt  how often
     * @return event action
     */
    EventAction onDelete( final Path path, final int cnt );

    /**
     * Event Action .
     */
    enum EventAction {
        /**
         * Skip this event.
         */
        SKIP,
        /**
         * Proceed.
         */
        ADVANCE,
        /**
         * Stop watching.
         */
        STOP;

    }


}
