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

package earth.eu.jtzipi.modules.io.audiovideo;

import java.util.Arrays;
import java.util.List;

/**
 * Audio Type Enum.
 */
public enum AudioType {

    WAV( 1L, "Wave", "audio/wav", "wav" ),

    /**
     * MP3.
     */
    MP3( 2L, "Mpeg layer three", "audio/mp3", "mp3" ),
    /**
     * Ogg Vorbis .
     */
    OGG( 4L, "Ogg Vorbis", "audio/ogg", "ogg" ),

    FLAC( 12L, "F lossless audio codec", "audio/fla", "flac" ),
    UNKNOWN( 0L, "", "", "" );

    private final long id;
    private final String desc;
    private final String mime;
    private final List<String> suffixL;

    /**
     * Audio Type.
     *
     * @param id      id
     * @param desc    description
     * @param mime    mime
     * @param suffice file suffix
     */
    AudioType( final long id, final String desc, final String mime, final String... suffice ) {
        this.id = id;
        this.desc = desc;
        this.mime = mime;
        this.suffixL = Arrays.asList( suffice );
    }
}
