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

package earth.eu.jtzipi.modules.fx.canvas;


import earth.eu.jtzipi.modules.utils.Utils;
import javafx.animation.Timeline;
import javafx.scene.control.Control;

/**
 * Base class for control using a canvas.
 */
public abstract class AbstractAnimatedWidget extends Control {


    public static final int FRAME_PER_SECOND_MIN = 1;

    public static final int FRAME_PER_SECOND = 27;

    public static final int FRAME_PER_SECOND_MAX = 60;


    Timeline tl;


    int framerate = FRAME_PER_SECOND;


    protected AbstractAnimatedWidget() {
        this.tl = new Timeline( framerate );
    }

    public void setFramerate( final int framerate ) {
        this.framerate = Utils.clamp( framerate, FRAME_PER_SECOND_MIN, FRAME_PER_SECOND_MAX );
    }

    protected abstract void start();

    protected abstract void pause();

    protected abstract void stop();

    protected abstract void resume();
}
