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

package earth.eu.jtzipi.modules.io.image;

import earth.eu.jtzipi.modules.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Objects;
import java.util.WeakHashMap;

/**
 * Utils for reading and writing image.
 *
 * @author jTzipi
 */
public  final class ImageUtils {
private static final Logger Log = LoggerFactory.getLogger( "ImageUtils" );

    // cache for image reader
    private static final WeakHashMap<String, ImageReader> IMG_READER_MAP = new WeakHashMap<>();

    private ImageUtils() {
        //
    }

    /**
     * Try to width and height of an image.
     *
     *
     * @param image image path
     * @return dim if readable or {@link ImageDimension#EMPTY} if {@code image} is not a image
     * @throws IOException if {@code image} is not readable or no image reader is found.
     *
     */
    public static ImageDimension getImageDimension( final Path image ) throws IOException {
        Log.info( "...try to read image '" + image + "'" );
        // no read throw
        if( !Files.isReadable(image)) {
            throw new IOException("");
        }
        // no image return empty
        if( !IOUtils.isImage( image ) ) {
            return ImageDimension.EMPTY;
        }

        Log.info( "... image found" );
        Log.info( "... Type is '" + IOUtils.getPathSuffixSafe( image ) );

        String sfx= IOUtils.getPathSuffixSafe( image );         // suffix parse
        File imgFile = image.toFile();                          // to file
        ImageReader ir = IMG_READER_MAP.get( sfx );             // cached?

        if(  ir != null ) {
            // try to read by cached image reader
            Log.info( "... read from cache" );
            return  tryReadDim( imgFile,ir );
        } else {
            Log.info( "... reader not cached" );
            //
            Iterator<ImageReader> irit = ImageIO.getImageReadersBySuffix( sfx );

            while ( irit.hasNext() ) {
                ir = irit.next();
                Log.info( "... found reader try to parse dim" );
                ImageDimension imgDim = tryReadDim( image.toFile(), ir );

            if( null != imgDim ) {
                Log.info( "... dim read" );
                // put reader to cache and return dim
                IMG_READER_MAP.put( sfx, ir );
                return imgDim;
            }
            }
        }

        throw new IOException("Failed to read dim of file");
    }


    private static ImageDimension tryReadDim( File file, ImageReader imgRead ) {
        try ( ImageInputStream iis = new FileImageInputStream( file ) ) {


            imgRead.setInput( iis );
            int minIdx = imgRead.getMinIndex();
            int width = imgRead.getWidth( minIdx );
            int height = imgRead.getHeight( minIdx );


            return ImageDimension.of( width, height );
        } catch ( final IOException ioE ) {
Log.error( "Failed to read dimension ", ioE );
            return null;
        } finally {
            // dispose resource
            imgRead.dispose();
        }

    }

    /**
     * Create a buffered image from swing icon.
     *
     * @param icon icon
     * @return buffered image
     */
    public static BufferedImage iconToBufferedImage( javax.swing.Icon icon ) {
        Objects.requireNonNull( icon );
        BufferedImage bufImg = GraphicsUtilities.createTranslucentCompatibleImage( icon.getIconWidth(), icon.getIconHeight() );
        icon.paintIcon( null, bufImg.createGraphics(), 0, 0  );

        return bufImg;
    }

}
