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

package earth.eu.jtzipi.modules.utils;


import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * Little pool of useful regular expression.
 *
 * @author jTzipi
 */
public enum RegUs implements Supplier<Pattern> {

    /**
     * ISO Date.
     * <p>
     * Like 2000-02-01
     */
    ISO_DATE( "^(?<year>[0-9]{4})-(?<month>1[0-2]|0[1-9])-(?<day>[0-2][0-9]|3[0-1])$" ),

    /**
     * File properties.
     * pattern for all files ending with
     * <ul>
     *     <li>properties</li>
     *     <li>json</li>
     *     <li>cfg</li>
     *     <li>conf</li>
     *     <li>ini</li>
     * </ul>
     * Ignoring case.
     */
    FILE_PROPERTIES_UNICODE_I( "^.*\\.(properties|json|ini|cfg)$", Pattern.UNICODE_CASE );

    /**
     * Font file.
     */
    public static final Pattern FONT_TYPE_PATTERN = Pattern.compile( "^.*\\.(otf|ttf)$" );
    /**
     * Video file.
     */
    public static final Pattern MOVIE_TYPE_PATTERN = Pattern.compile( "^.*\\.(avi|mp4|mpg|ogg|flv)$" );
    /**
     * Audio file.
     */
    public static final Pattern AUDIO_TYPE_PATTERN = Pattern.compile( "^.*\\.(mp3|ogg|flac|m4a|au|wav|aif)$" );
    public static final Pattern IMAGE_RASTER_TYPE_PATTERN = Pattern.compile( "^.*\\.(jpeg|jpg|png|tif|tiff|bmp|gif)$" );
    public static final Pattern IMAGE_VECTOR_TYPE_PATTERN = Pattern.compile( "^.*\\.(svg)" );
    public static final Pattern BIN_WINSYS_TYPE_PATTERN = Pattern.compile( "^.*\\.(sys|dll).$" );
    public static final Pattern BIN_WINEXEC_PATTERN = Pattern.compile( "^.*\\.(bat|com|exe|msi).$" );
    public static final Pattern BIN_MACTYPE_PATTERN = Pattern.compile( "(sys|dll).$" );
    public static final Pattern BIN_LINUXSYS_TYPE_PATTERN = Pattern.compile( "^.*\\.(so|rc|rpm|bundle).$" );
    public static final Pattern BIN_LINUXEXEC_TYPE_PATTERN = Pattern.compile( "^.*\\.(run|sh).$" );
    public static final Pattern BIN_ARC_TYPE_PATTERN = Pattern.compile( "^.*\\.(arj|zip|7z|gzip|bzip|tar|rar).$" );
    public static final Pattern TEXT_TYPE_PATTERN = Pattern.compile( "^.*\\.(txt|dat|rtf)$" );
    public static final Pattern MARKUP_TYPE_PATTERN = Pattern.compile( "^.*\\.(xml|fxml|md|yaml)$" );
    public static final Pattern HTML_TYPE_PATTERN = Pattern.compile( "^.*\\.(xhtml|html|htm)$" );
    public static final Pattern SECURE_TYPE_PATTERN = Pattern.compile( "^.*\\.(otf|ttf)$" );
    public static final Pattern PROPERTIES_TYPE_PATTERN = Pattern.compile( "^.*\\.(properties|json|ini|cfg)$" );
    /**
     * All Java Regular Expression meta Characters.
     */
    public static final Map<String, String> META_MAP = new HashMap<>();

    static {
        META_MAP.put( "$", "\\$" );
        META_MAP.put( "(", "\\(" );
        META_MAP.put( ")", "\\)" );
        META_MAP.put( "*", "\\*" );
        META_MAP.put( "+", "\\+" );
        META_MAP.put( ".", "\\." );
        META_MAP.put( "?", "\\?" );
        META_MAP.put( "[", "\\[" );
        META_MAP.put( "\\", "\\\\" );
        META_MAP.put( "^", "\\^" );
        META_MAP.put( "{", "\\{" );
        META_MAP.put( "|", "\\|" );
    }

    private final String regex;
    private final Pattern patter;

    RegUs( final String regExp, final int... modifier ) {
        this.regex = regExp;

        int flag = 0;
        if ( modifier.length > 0 ) {

            for ( final int mod : modifier ) {
                flag |= mod;
            }
        }
        this.patter = Pattern.compile( regExp, flag );

    }

    @Override
    public Pattern get() {
        return patter;
    }

    /**
     * Return regular expression.
     *
     * @return reg
     */
    public String getReg() {
        return this.regex;
    }
}
