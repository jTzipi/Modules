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

package earth.eu.jtzipi.modules.fx.shape;

import earth.eu.jtzipi.modules.utils.Utils;
import javafx.geometry.Pos;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineJoin;


/**
 * Provider of arbitrary shape.
 */
public final class Shapes {

    /**
     * Maximal value for param.
     */
    public static final double MAX_LEN = 7_000D;

    /**
     * Minimal value for param.
     */
    public static final double MIN_LEN = 1D;

    private Shapes() {

    }

    /**
     * Create a arrow shape.
     *
     * @param w           width
     * @param h           height
     * @param offsetX     offset x
     * @param offsetY     offset y
     * @param strokeWidth stroke width
     * @param pointTo     pos
     * @return Arrow shape
     */
    public static Shape arrow( final double w, final double h, final double offsetX, final double offsetY, final double strokeWidth, final Pos pointTo ) {

        Utils.clamp( w, MIN_LEN, MAX_LEN );
        Utils.clamp( h, MIN_LEN, MAX_LEN );
        Utils.clamp( strokeWidth, ShapeBuilder.MIN_STROKE_LEN, ShapeBuilder.MAX_STROKE_LEN );


        final double x1;
        final double x2;
        final double x3;
        final double y1;
        final double y2;
        final double y3;

        final double midX = Math.floor( w / 2D );
        final double midY = Math.floor( h / 2D );

        switch ( pointTo ) {

            // Arrow up
            case TOP_CENTER:
                x1 = offsetX;
                y1 = midY;
                x2 = midX;
                y2 = offsetY;
                x3 = w - offsetX;
                y3 = midY;
                break;
            // Arrow left
            case CENTER_LEFT:
                x1 = w - offsetX;
                y1 = offsetY;
                x2 = offsetX;
                y2 = midY;
                x3 = w - offsetX;
                y3 = h - offsetY;
                break;
            // Arrow right
            case CENTER_RIGHT:
                x1 = offsetX;
                y1 = offsetY;
                x2 = w - offsetX;
                y2 = midY;
                x3 = offsetX;
                y3 = h - offsetY;
                break;
            // Arrow down
            case BOTTOM_CENTER:
                x1 = offsetX;
                y1 = midY;
                x2 = midX;
                y2 = h - offsetY;
                x3 = w - offsetX;
                y3 = midY;
                break;

            default:
                x1 = 0D;
                y1 = 0D;
                x2 = 0D;
                y2 = 0D;
                x3 = 0D;
                y3 = 0D;
        }

        return createArrow( x1, y1, x2, y2, x3, y3, strokeWidth );
    }


    /**
     * Create a shape of arrow directed left.
     * <p>
     * Shape of arrow &lt;
     * </p>
     *
     * @param w           width
     * @param h           height
     * @param offset      offset
     * @param strokeWidth stroke width
     * @return shape
     */
    public static Shape arrowLeft( final double w, final double h, final double offset, final double strokeWidth ) {


        final double mid = Math.floor( w / 2D );

        // double dist = 12D;      // offset
        final double xUp = w - offset;    // x left up
        final double yUp = offset;
        final double yDown = h - offset;

        return ShapeBuilder.create().mxy( xUp, yUp ).lxy( mid, mid ).lxy( xUp, yDown ).strokeWidth( strokeWidth ).strokeJoin( StrokeLineJoin.ROUND ).build();
    }

    private static Shape createArrow( final double x1, final double y1, final double x2, final double y2, final double x3, final double y3, final double width ) {

        return ShapeBuilder.create().mxy( x1, y1 ).lxy( x2, y2 ).lxy( x3, y3 ).strokeWidth( width ).strokeJoin( StrokeLineJoin.ROUND ).build();
    }
}
