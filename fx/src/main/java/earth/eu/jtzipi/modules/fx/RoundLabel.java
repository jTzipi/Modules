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

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;


/**
 * A Label with round border.
 */
public class RoundLabel extends Label {


    // Path
    private static final String SHAPE = "m 85.833919,18.7762 c 3.823207,3.823206 3.823207,34.797009 0,38.620216 -3.823206,3.823206 -34.797009,3.823206 -38.620216,0 -3.823206,-3.823206 -3.823206,-34.797009 0,-38.620216 3.823207,-3.823206 34.79701,-3.823206 38.620216,0 z";



    public RoundLabel() {
        super();
        init();
    }

    private void init() {
        SVGPath round = new SVGPath();


        round.setContent( SHAPE );

        setShape( round );

        BorderStroke bs = new BorderStroke( Color.BLACK, BorderStrokeStyle.SOLID, null, BorderWidths.DEFAULT );
        setBorder( new Border(bs));

        setPrefSize( 57,57 );
        setAlignment( Pos.CENTER );
        setText( "Gysi" );
        
    }
}
