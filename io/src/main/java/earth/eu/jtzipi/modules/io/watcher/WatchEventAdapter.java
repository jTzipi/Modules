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

package earth.eu.jtzipi.modules.io.watcher;

import java.nio.file.Path;

/**
 * Adapter for IWatchEventHandler.
 *
 * @author jTzipi
 */
public class WatchEventAdapter implements IWatchEventHandler {




    @Override
    public EventAction onOverflow( final Path path, final int cnt ) {
        return EventAction.ADVANCE;
    }

    @Override
    public EventAction onCreate( final Path path, final int cnt ) {
        return EventAction.ADVANCE;
    }

    @Override
    public EventAction onModify( final Path path, final int cnt ) {
        return EventAction.ADVANCE;
    }

    @Override
    public EventAction onDelete( final Path path, final int cnt ) {
        return EventAction.ADVANCE;
    }
}
