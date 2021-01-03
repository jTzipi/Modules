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

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;

/**
 * <p>Provides an abstract implementation of the <code>BufferedImageOp</code>
 * interface. This class can be used to created new image filters based
 * on <code>BufferedImageOp</code>.</p>
 *
 * @author Romain Guy <romain.guy@mac.com>
 */
public abstract class AbstractFilter implements BufferedImageOp {


    /**
     * {@inheritDoc}
     */
    public Rectangle2D getBounds2D( final BufferedImage src ) {
        return new Rectangle( 0, 0, src.getWidth(), src.getHeight() );
    }

    /**
     * {@inheritDoc}
     */
    public BufferedImage createCompatibleDestImage( final BufferedImage src,
                                                    ColorModel destCM ) {
        if ( destCM == null ) {
            destCM = src.getColorModel();
        }

        return new BufferedImage( destCM,
                destCM.createCompatibleWritableRaster(
                        src.getWidth(), src.getHeight() ),
                destCM.isAlphaPremultiplied(), null );
    }

    /**
     * {@inheritDoc}
     */
    public Point2D getPoint2D( final Point2D srcPt, final Point2D dstPt ) {
        return ( Point2D ) srcPt.clone();
    }

    /**
     * {@inheritDoc}
     */
    public RenderingHints getRenderingHints() {
        return null;
    }
}
