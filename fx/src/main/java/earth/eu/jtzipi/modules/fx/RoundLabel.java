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

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.SVGPath;


/**
 * A Label with round border.
 */
public class RoundLabel extends Label {


    // Path "Gysi"
    private static final String SHAPE = "m 85.833919,18.7762 c 3.823207,3.823206 3.823207,34.797009 0,38.620216 -3.823206,3.823206 -34.797009,3.823206 -38.620216,0 -3.823206,-3.823206 -3.823206,-34.797009 0,-38.620216 3.823207,-3.823206 34.79701,-3.823206 38.620216,0 z";

    private static final double BORDER_STROKE_LEN = 1D;
    private static final double SIZE_PREF = 57D;
    private final SVGPath svgPath = new SVGPath();
    private ObjectProperty<BorderStrokeStyle> fxFrameStrokeTypeProp = new SimpleObjectProperty<>();
    private ObjectProperty<Color> fxFrameStrokeColorProp = new SimpleObjectProperty<>();
    private DoubleProperty fxFrameStrokeWidthProp = new SimpleDoubleProperty(this, "FX_STROKE_WIDTH_PROP", BORDER_STROKE_LEN);
    private DropShadow dropShadow = new DropShadow( 5D, Color.rgb( 122, 122, 246 ) );
    private Color fill = Color.gray( 0.9D );

    public RoundLabel(String text) {
        super();

        svgPath.setContent( SHAPE );
        setShape( svgPath );
        setText( text );
        setTextFill( fill  );
        init();
    }



    private void init() {


        BorderStroke bs = new BorderStroke( fill, BorderStrokeStyle.SOLID, null, new BorderWidths( fxFrameStrokeWidthProp.doubleValue() ) );
        double SIZE_HALF = SIZE_PREF / 2D;

        Stop stop1 = new Stop( 1D, fill );
        Stop stop3 = new Stop( 0D, Color.gray( 0.5D ) );
        RadialGradient bgrad = new RadialGradient( 90D, 0.0D, SIZE_HALF, SIZE_HALF, SIZE_HALF, false, CycleMethod.NO_CYCLE, stop1, stop3 );
        setBorder( new Border(bs));
        setBackground( new Background( new BackgroundFill( bgrad, CornerRadii.EMPTY, Insets.EMPTY ) ) );

        setPrefSize( SIZE_PREF,SIZE_PREF );
        setAlignment( Pos.CENTER );

        setOnMouseEntered(  me -> setEffect( dropShadow ) );
        setOnMouseExited( me -> setEffect( null ) );
    }
}
