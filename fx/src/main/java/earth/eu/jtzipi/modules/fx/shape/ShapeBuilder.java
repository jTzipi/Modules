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

import earth.eu.jtzipi.modules.utils.IBuilder;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for shapes and paths.
 *
 */
public class ShapeBuilder implements IBuilder<Shape> {


    private List<PathElement> shapeL = new ArrayList<>();
    private double lastX = 0D;
    private double lastY = 0D;
    private StrokeLineJoin strokeJoin = StrokeLineJoin.ROUND;
    private StrokeType strokeType = StrokeType.CENTERED;
    private StrokeLineCap strokeLineCap = StrokeLineCap.ROUND; // sLineCap
    private double strokeWidth = 1.0D;
    private Paint strokeColor = Color.gray( 0.7D );
    private boolean smooth = true;

    private ShapeBuilder() {
        shapeL.add( new MoveTo( lastX, lastY ) );
    }

    /**
     * Create a new builder.
     * @return 
     */
    public static ShapeBuilder create() {
        return new ShapeBuilder();
    }

    /**
     * Set width of stroke.
     *
     * @param strokeWidth stroke width [0,1]
     * @return {@code this}
     */
    public ShapeBuilder strokeWidth( final double strokeWidth ) {
        this.strokeWidth = strokeWidth ;

        return this;
    }

    /**
     * Stroke join.
     *
     * @param join stroke join
     * @return {@code }
     */
    public ShapeBuilder strokeJoin( final StrokeLineJoin join ) {
        this.strokeJoin = join;
        return this;
    }

    /**
     *
     * @param x
     * @return
     */
    public ShapeBuilder lx( final double x ) {
        this.shapeL.add( new LineTo( x, lastY ) );
        this.lastX = x;
        return this;
    }

    /**
     *
     * @param y
     * @return
     */
    public ShapeBuilder ly( final double y ) {
        this.shapeL.add( new LineTo( lastX,y ) );
        this.lastY = y;
        return this;
    }

    /**
     *
     * @param x
     * @param y
     * @return
     */
    public ShapeBuilder lxy( final double x, final double y ) {
        this.shapeL.add( new LineTo( x, y ) );
        this.lastX = x;
        this.lastY = y;
        return this;
    }

    public ShapeBuilder mx( final double x ) {
        this.shapeL.add( new MoveTo( x, lastY ) );
        this.lastX = x;
        return this;
    }

    public ShapeBuilder my( final double y ) {
        this.shapeL.add( new MoveTo( lastX,y ) );
        this.lastY = y;
        return this;
    }

    public ShapeBuilder mxy( final double x, final double y ) {
        this.shapeL.add( new MoveTo( x, y ) );
        this.lastX = x;
        this.lastY = y;
        return this;
    }

    public ShapeBuilder quadTo( final double x, final double y, final double ctrX, final double ctrY ) {
        this.shapeL.add( new QuadCurveTo( ctrX, ctrY, x, y ) );
        this.lastY = y;
        this.lastX = x;
        return this;
    }

    public ShapeBuilder close() {
        this.shapeL.add( new ClosePath() );
        return this;
    }


    @Override
    public Shape build() {
        Path p = new Path( shapeL );

        p.setSmooth( smooth );
        p.setStrokeWidth( strokeWidth );
        p.setStrokeLineJoin( strokeJoin );
        p.setStroke( strokeColor );
        p.setStrokeLineCap( strokeLineCap );
        p.setStrokeType( strokeType );

        return p;
    }
}
