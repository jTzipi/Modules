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

package earth.eu.jtzipi.modules.fx;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.SkinBase;
import javafx.scene.paint.Color;

public class BendButtonSkin extends SkinBase<BendButton> {


    private static final String SHAPE = "m 85.833919,18.7762 c 3.823207,3.823206 3.823207,34.797009 0,38.620216 -3.823206,3.823206 -34.797009,3.823206 -38.620216,0 -3.823206,-3.823206 -3.823206,-34.797009 0,-38.620216 3.823207,-3.823206 34.79701,-3.823206 38.620216,0 z";

    private static final double WIDTH_MIN = 9D;
    private static final double WIDTH_PREF = 46D;
    private static final double WIDTH_MAX = 999D;

    private static final double HEIGHT_MIN = 9D;
    private static final double HEIGHT_PREF = 46D;
    private static final double HEIGHT_MAX = 999D;

    private double cw;
    private double ch;
    private Canvas canvas;
    private GraphicsContext gc;
    /**
     * Constructor for all SkinBase instances.
     *
     * @param control The control for which this Skin should attach to.
     */
    protected BendButtonSkin( BendButton control ) {
        super( control );
        cw = control.getWidth();
        ch = control.getHeight();

        this.canvas = new Canvas( cw, ch );

        draw();

        getChildren().add( canvas );
    }

    @Override
    protected double computeMinWidth( double height, double topInset, double rightInset, double bottomInset, double leftInset ) {
        return WIDTH_MIN;
    }

    @Override
    protected double computeMinHeight( double width, double topInset, double rightInset, double bottomInset, double leftInset ) {

        return HEIGHT_MIN;
    }

    @Override
    protected double computeMaxWidth( double height, double topInset, double rightInset, double bottomInset, double leftInset ) {
        return Math.min( WIDTH_MAX, cw );
    }

    @Override
    protected double computeMaxHeight( double width, double topInset, double rightInset, double bottomInset, double leftInset ) {
        return HEIGHT_MAX;
    }

    @Override
    protected double computePrefWidth( double height, double topInset, double rightInset, double bottomInset, double leftInset ) {
        return WIDTH_MAX;
    }

    @Override
    protected double computePrefHeight( double width, double topInset, double rightInset, double bottomInset, double leftInset ) {
        return Math.min( HEIGHT_MAX, ch );
    }

    private void draw() {

        BendButton bb = getSkinnable();
        gc = canvas.getGraphicsContext2D();

        gc.setLineWidth( 3D );
        gc.setStroke( Color.rgb( 9,9,254 ) );
        gc.strokeText( bb.getTextPropFX().getValue(), 4D, 46D );
        gc.appendSVGPath( SHAPE );

        gc.stroke();
    }
}
