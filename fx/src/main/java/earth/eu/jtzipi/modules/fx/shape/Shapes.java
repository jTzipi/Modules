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

package earth.eu.jtzipi.modules.fx.shape;

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
    /**
     * Create a shape of arrow directed left.
     * <p>
     *     Shape of arrow &lt;
     * </p>
     * @param w width
     * @param h height
     * @param offset offset
     * @param strokeWidth stroke width
     * @return shape
     */
    public static Shape arrow( double w, double h, double offset, double strokeWidth ) {




        double mid = w / 2D;

        // double dist = 12D;      // offset
        double xUp = w - offset;    // x left up
        double yUp = offset;
        double yMid = mid;
        double xDown = xUp;
        double yDown = h - offset;

        return ShapeBuilder.create().mxy( xUp, yUp ).lxy( yMid, yMid ).lxy( xDown, yDown ).strokeWidth( strokeWidth ).strokeJoin( StrokeLineJoin.ROUND ).build();
    }
}
