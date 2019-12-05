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

import javafx.beans.property.*;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;

public class BendButton extends Control {


    private StringProperty fxTextProp = new SimpleStringProperty( this, "FX_TEXT_PROP" );


    private ObjectProperty<Image> fxImageProp = new SimpleObjectProperty<>();

    private ObjectProperty<StrokeType> fxFrameStrokeTypeProp = new SimpleObjectProperty<>();
    private ObjectProperty<Color> fxFrameStrokeColorProp = new SimpleObjectProperty<>();
    private DoubleProperty fxFrameStrokeWidthProp = new SimpleDoubleProperty();


    public BendButton( String text, Image image, double prefWidth, double prefHeight ) {
        this.fxTextProp.setValue( text );
        this.fxImageProp.setValue( image );

        setWidth( prefWidth );
        setHeight( prefHeight );

        // setOnMouseEntered( me -> setBorder( new Bo ) );
    }

    public final StringProperty getTextPropFX() {
        return this.fxTextProp;
    }


    public final ObjectProperty<Color> getFrameStrokeColorPropFX(){
        return this.fxFrameStrokeColorProp;
    }

    public final ObjectProperty<StrokeType> getFrameStrokeTypePropFX() {

        return this.fxFrameStrokeTypeProp;
    }

    public final DoubleProperty getFrameStrokeWidthPropFX() {
        return this.fxFrameStrokeWidthProp;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new BendButtonSkin( this );
    }
}
