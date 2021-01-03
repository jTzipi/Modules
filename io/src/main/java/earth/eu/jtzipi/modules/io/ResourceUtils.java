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

package earth.eu.jtzipi.modules.io;


import earth.eu.jtzipi.modules.io.image.GraphicsUtilities;
import earth.eu.jtzipi.modules.io.image.ImageDimension;
import earth.eu.jtzipi.modules.io.image.ImageUtils;
import javafx.scene.text.Font;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.*;

/**
 * Resource Utils.
 * <p>
 *     Cache for image, font and other resources.
 *
 * </p>
 *
 * @author jTzipi
 */
public final class ResourceUtils {


    // Cache for image.
    private static final Map<String, Map<ImageDimension, BufferedImage>> IMAGE_CACHE = new WeakHashMap<>();
    private static final Map<String, Font> FONT_CACHE = new HashMap<>();
    private ResourceUtils() {

    }

    public static List<Path> loadImagesFromDir( final Path pathToDir, final boolean write ) throws IOException {
        Objects.requireNonNull( pathToDir, "Path is null" );
        if ( !Files.isReadable( pathToDir ) ) {

            throw new IOException( "Can not read '" + pathToDir + "'" );
        }


        if ( !Files.isDirectory( pathToDir, LinkOption.NOFOLLOW_LINKS ) ) {

            return Collections.emptyList();
        }
    // try to read images from dir
        final List<Path> imgPathL = IOUtils.lookupDir( pathToDir, IOUtils.PATH_ACCEPT_IMAGE );

        final List<Path> readImgL = new ArrayList<>();
        //
        for ( final Path imgPath : imgPathL ) {

            final String imgName;
            final ImageDimension imgDim;
            try {
                imgName = IOUtils.getPathPrefix( imgPath );
                imgDim = ImageUtils.getImageDimension( imgPath );

                final Map<ImageDimension, BufferedImage> imgMap = IMAGE_CACHE.computeIfAbsent( imgName, gadi -> new HashMap<>() );

                // Image found and write not
                if ( imgMap.containsKey( imgDim ) && !write ) {

                    continue;
                }

                final URL imgUrl = imgPath.toUri().toURL();
                final BufferedImage bufImg = GraphicsUtilities.loadCompatibleImage( imgUrl );
                imgMap.put( imgDim, bufImg );
                readImgL.add( imgPath );

            } catch ( final IOException ioE ) {


            }

        }
return imgPathL;
    }

    public static void clearImgCache() {
        IMAGE_CACHE.clear();
    }

    public static List<Path> loadFontsFromDir( final Path pathToFont, final double sizeDef, final boolean writeProp ) throws IOException {
        Objects.requireNonNull( pathToFont, "Path is null" );
        if ( !Files.isReadable( pathToFont ) ) {

            throw new IOException( "Can not read '" + pathToFont + "'" );
        }

        if ( !Files.isDirectory( pathToFont, LinkOption.NOFOLLOW_LINKS ) ) {

            return Collections.emptyList();
        }

        final List<Path> fontPathL = IOUtils.lookupDir( pathToFont, IOUtils.PATH_ACCEPT_FONT );
        final List<Path> ret = new ArrayList<>();


        for ( final Path fp : fontPathL ) {


            final String fontName;
            try {
                fontName = IOUtils.getPathPrefix( fp );
                if ( FONT_CACHE.containsKey( fontName ) && !writeProp ) {

                    continue;
                }

                final Font f = IOUtils.loadFont( fp, sizeDef );
                FONT_CACHE.put( fontName, f );

                ret.add( fp );
            } catch ( final IOException ioe ) {


            }




        }
        return ret;
    }

    /**
     * Thumbnail Dimension.s
     */
    enum ThumbDim {

        /**
         * Dimension 16x16.
         */
        DIM_16_16(ImageDimension.of( 16, 16 )),
        DIM_24_24( ImageDimension.of( 24, 24 ) )
        ;

        private final ImageDimension dim;

        ThumbDim( final ImageDimension imgDim ) {
    this.dim = imgDim;
        }

        public ImageDimension getDim() {
            return dim;
        }
    }
}
