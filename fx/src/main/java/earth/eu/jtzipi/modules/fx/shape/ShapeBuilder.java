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

import earth.eu.jtzipi.modules.utils.IBuilder;
import earth.eu.jtzipi.modules.utils.Utils;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for shapes and paths.
 */
public class ShapeBuilder implements IBuilder<Shape> {

    /**
     * Default Stroke Type.
     */
    public static final StrokeType DEFAULT_STROKE_TYPE = StrokeType.CENTERED;
    /**
     * Default Stroke Line Join.
     */
    public static final StrokeLineJoin DEFAULT_STROKE_LINE_JOIN = StrokeLineJoin.ROUND;
    /**
     * Default Stroke Line Cap.
     */
    public static final StrokeLineCap DEFAULT_STROKE_LINE_CAP = StrokeLineCap.ROUND;
    /**
     * Default path stroke length.
     */
    public static final double DEFAULT_STROKE_LEN = 1.0D;
    /**
     * Default Path Paint.
     */
    public static final Paint DEFAULT_STROKE_PAINT = Color.gray( 0.77D );
    public static final double MIN_STROKE_LEN = 0.1D;
    public static final double MAX_STROKE_LEN = 9100D;
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger( "" );
    private final List<PathElement> shapeL = new ArrayList<>(); // path list
    private double lastX = 0D;  // last x coordinate
    private double lastY = 0D;  // last y coordinate
    private StrokeLineJoin strokeJoin = StrokeLineJoin.ROUND;   // Line Stroke
    private StrokeType strokeType = StrokeType.CENTERED;        // Stroke Type
    private StrokeLineCap strokeLineCap = StrokeLineCap.ROUND;  // Line Cap
    private double strokeWidth = DEFAULT_STROKE_LEN;      // Stroke width
    private Paint strokePaint = Color.gray( 0.7D ); // Stroke paint
    private boolean smooth = true;


    /**
     * Shape Builder.
     */
    private ShapeBuilder() {
        shapeL.add( new MoveTo( lastX, lastY ) );
    }

    /**
     * Create a new builder.
     *
     * @return new Shape Builder
     */
    public static ShapeBuilder create() {
        return new ShapeBuilder();
    }

    /**
     * Set width of stroke.
     *
     * @param strokeWidth stroke width [{@link #MIN_STROKE_LEN},{@link #MAX_STROKE_LEN}]
     * @return {@code this}
     */
    public ShapeBuilder strokeWidth( final double strokeWidth ) {
        this.strokeWidth = Utils.clamp( strokeWidth, MIN_STROKE_LEN, MAX_STROKE_LEN );

        return this;
    }

    /**
     * Stroke join.
     *
     * @param join stroke join
     * @return {@code this}
     */
    public ShapeBuilder strokeJoin( final StrokeLineJoin join ) {
        this.strokeJoin = null == join ? StrokeLineJoin.ROUND : join;
        return this;
    }

    /**
     * Append a horizontal line to x.
     *
     * @param x x coordinate
     * @return {@code this}
     */
    public ShapeBuilder lx( final double x ) {
        this.shapeL.add( new LineTo( x, lastY ) );
        this.lastX = x;
        return this;
    }

    /**
     * Append a ine to y.
     *
     * @param y y coordinate
     * @return {@code this}
     */
    public ShapeBuilder ly( final double y ) {
        this.shapeL.add( new LineTo( lastX, y ) );
        this.lastY = y;
        return this;
    }

    /**
     * Append a line to {x, y}.
     *
     * @param x x coord
     * @param y y coord
     * @return {@code this}
     */
    public ShapeBuilder lxy( final double x, final double y ) {
        this.shapeL.add( new LineTo( x, y ) );
        this.lastX = x;
        this.lastY = y;
        return this;
    }

    /**
     * Move to x.
     *
     * @param x x
     * @return {@code this}
     */
    public ShapeBuilder mx( final double x ) {
        this.shapeL.add( new MoveTo( x, lastY ) );
        this.lastX = x;
        return this;
    }

    /**
     * Move to y.
     *
     * @param y y
     * @return {@code this}
     */
    public ShapeBuilder my( final double y ) {
        this.shapeL.add( new MoveTo( lastX, y ) );
        this.lastY = y;
        return this;
    }

    /**
     * Move to {x,y}.
     *
     * @param x x
     * @param y y
     * @return {@code this}
     */
    public ShapeBuilder mxy( final double x, final double y ) {
        this.shapeL.add( new MoveTo( x, y ) );
        this.lastX = x;
        this.lastY = y;
        return this;
    }

    /**
     * Create a quadratic curve to {x,y}.
     *
     * @param x    x
     * @param y    y
     * @param ctrX control x
     * @param ctrY control y
     * @return {@code this}
     */
    public ShapeBuilder quadTo( final double x, final double y, final double ctrX, final double ctrY ) {
        this.shapeL.add( new QuadCurveTo( ctrX, ctrY, x, y ) );
        this.lastY = y;
        this.lastX = x;
        return this;
    }

    /**
     * Create a elliptic arc.
     *
     * @param x             x
     * @param y             y
     * @param radX          radius x
     * @param radY          radius y
     * @param xAxisRotation rotation of x
     * @param flagLarge     large arc flag
     * @param flagSwe       sweep flag
     * @return {@code this}
     */
    public ShapeBuilder arcTo( final double x, final double y, final double radX, final double radY, final double xAxisRotation, final boolean flagLarge, final boolean flagSwe ) {
        this.shapeL.add( new ArcTo( radX, radY, xAxisRotation, x, y, flagLarge, flagSwe ) );
        this.lastY = y;
        this.lastX = x;
        return this;

    }

    /**
     * Create a cubic curve to {x,y}.
     *
     * @param x       x
     * @param y       y
     * @param ctrXOne control x1
     * @param ctrYone control y1
     * @param ctrXtwo control x2
     * @param ctrYtwo control y2
     * @return {@code this}
     */
    public ShapeBuilder qubicCurveTo( final double x, final double y, final double ctrXOne, final double ctrYone, final double ctrXtwo, final double ctrYtwo ) {
        this.shapeL.add( new CubicCurveTo( ctrXOne, ctrYone, ctrXtwo, ctrYtwo, x, y ) );
        this.lastX = x;
        this.lastY = y;

        return this;
    }

    /**
     * close this shape.
     *
     * @return {@code this}
     */
    public ShapeBuilder close() {
        this.shapeL.add( new ClosePath() );
        return this;
    }

    /**
     * Set path stroke type.
     *
     * @param strokeType type
     * @return {@code this}
     */
    public ShapeBuilder strokeType( final StrokeType strokeType ) {
        this.strokeType = null == strokeType ? StrokeType.CENTERED : strokeType;
        return this;
    }

    /**
     * Set path stroke line cap.
     *
     * @param strokeLineCap line cap
     * @return {@code this}
     */
    public ShapeBuilder strokeLineCap( final StrokeLineCap strokeLineCap ) {
        this.strokeLineCap = null == strokeLineCap ? StrokeLineCap.ROUND : strokeLineCap;
        return this;
    }

    /**
     * Set path stroke paint.
     *
     * @param paint paint
     * @return {@code this}
     */
    public ShapeBuilder strokePaint( final Paint paint ) {
        this.strokePaint = paint;
        return this;
    }

    /**
     * Set smooth parameter.
     *
     * @param smoothProp smooth
     * @return {@code this}
     */
    public ShapeBuilder smooth( final boolean smoothProp ) {
        this.smooth = smoothProp;

        return this;
    }

    /**
     * Reset this builder.
     * Set every data to default.
     *
     * @return {@code this}
     */
    public ShapeBuilder reset() {
        this.shapeL.clear();
        this.lastX = 0D;
        this.lastY = 0D;

        this.strokeJoin = DEFAULT_STROKE_LINE_JOIN;
        this.strokeLineCap = DEFAULT_STROKE_LINE_CAP;
        this.strokeType = DEFAULT_STROKE_TYPE;
        this.strokeWidth = DEFAULT_STROKE_LEN;
        this.strokePaint = DEFAULT_STROKE_PAINT;


        return this;
    }

    @Override
    public Shape build() {
        final Path p = new Path( shapeL );

        p.setSmooth( smooth );
        p.setStrokeWidth( strokeWidth );
        p.setStrokeLineJoin( strokeJoin );
        p.setStroke( strokePaint );
        p.setStrokeLineCap( strokeLineCap );
        p.setStrokeType( strokeType );

        return p;
    }
}
