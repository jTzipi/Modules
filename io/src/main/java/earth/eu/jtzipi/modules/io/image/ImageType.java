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

package earth.eu.jtzipi.modules.io.image;


import earth.eu.jtzipi.modules.io.FileSig;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;


/**
 * Image Types readable by JDK.
 */
public enum ImageType {

    /**
     * GIF.
     */
    GIF( 1L, "Graphic", "image/gif", "gif" ),
    /**
     * JPG.
     */
    JPEG( 2L, "Joint Picture", "image/jpg", "jpg", "jpeg", "jpe", "jif", "jfif", "jfi" ),
    /**
     * JPG.
     */
    PNG( 4L, "Portable Network Graphic", "image/png", "png" ),
    /**
     *
     */
    BMP( 10L, "Bitmap", "image/bmp", "bmp" ),
    /**
     *
     */
    UNKNOWN( 0L, "Unknown Format", "application/octet-stream", "" );

    private final long i;
    private final String d;
    private final String mime;
    private final List<String> suffixL;

    /**
     * @param icon   id
     * @param desc   description
     * @param suffix one to many suffice
     */
    ImageType( final long icon, final String desc, final String mime, final String... suffix ) {
        this.i = icon;
        this.d = desc;
        this.mime = mime;
        this.suffixL = Arrays.asList( suffix );
    }

    /**
     * Return image type of File signature if match.
     *
     * @param fs signature
     * @return Type if matching else {@link ImageType#UNKNOWN}
     */
    public static ImageType of( final FileSig fs ) {
        Objects.requireNonNull( fs );
        if ( fs.type() != FileSig.Type.IMAGE ) {

            return UNKNOWN;
        }

        final ImageType itype;

        switch ( fs ) {
            case JPG:
            case JPG_RAW:
            case JPG_JFIF:
                itype = JPEG;
                break;
            case PNG:
                itype = PNG;
                break;
            case GIF:
                itype = GIF;
                break;
            case BMP:
                itype = BMP;
                break;
            default:
                itype = UNKNOWN;
        }

        return itype;
    }

    /**
     * Return mime type of image.
     *
     * @return mime type
     */
    public String getMime() {
        return mime;
    }

    /**
     * @return
     */
    public String getDesc() {
        return d;
    }

    public List<String> getSuffixList() {
        return suffixL;
    }

}
