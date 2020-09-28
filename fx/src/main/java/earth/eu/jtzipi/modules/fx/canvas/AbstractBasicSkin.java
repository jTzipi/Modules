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

package earth.eu.jtzipi.modules.fx.canvas;

import earth.eu.jtzipi.modules.utils.Utils;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Control;
import javafx.scene.control.SkinBase;

import java.util.Objects;

/**
 * Basic Skin.
 *
 * @param <C> widget
 */
public abstract class AbstractBasicSkin<C extends Control> extends SkinBase<C> {

    /**
     * Minimum height.
     */
    public static final double HEIGHT_MIN = 12D;
    /**
     * Minimum width.
     */
    public static final double WIDTH_MIN = 12D;
    /**
     * Maximum width.
     */
    public static final double WIDTH_MAX = 90000D;
    /**
     * Maximum height.
     */
    public static final double HEIGHT_MAX = 90000D;

    Canvas canvas;

    /**
     * C.
     *
     * @param c widget
     */
    public AbstractBasicSkin( C c ) {
        super( c );
        init();
        // Add change listener for width and update only if width changed
        c.widthProperty().addListener( ( obs, wio, win ) -> {
            if ( null != win && win != wio ) {
                resize( win.doubleValue(), c.getHeight() );
            }
        } );

        // Add change listener for height and update only if changed
        c.heightProperty().addListener( ( obs, heo, hen ) -> {
            if ( null != hen && hen != heo ) {

                resize( c.getWidth(), hen.doubleValue() );
            }
        } );
    }

    /**
     * If width or height of widget &gt; 0 this is true.
     *
     * @param control widget
     * @return {@code false} if width or height of {@code control} &le; 0
     * @throws NullPointerException if {@code control} is null
     */
    public static boolean isControlLegalSized( Control control ) {
        Objects.requireNonNull( control );
        return Double.compare( control.getWidth(), 0D ) > 0D
                && Double.compare( control.getHeight(), 0D ) > 0D;
    }

    public static boolean isControlLegalPrefSized( Control control ) {
        Objects.requireNonNull( control );
        return Double.compare( control.getPrefWidth(), 0D ) > 0D
                && Double.compare( control.getPrefHeight(), 0D ) > 0D;
    }

    @Override
    protected double computeMinWidth( double height, double topInset, double rightInset, double bottomInset, double leftInset ) {
        double minWidth = super.computeMinWidth( height, topInset, rightInset, bottomInset, leftInset );

        return Math.max( minWidth, WIDTH_MIN );
    }

    @Override
    protected double computeMinHeight( double width, double topInset, double rightInset, double bottomInset, double leftInset ) {
        double minHeight = super.computeMinHeight( width, topInset, rightInset, bottomInset, leftInset );

        return Math.max( minHeight, HEIGHT_MIN );
    }

    /**
     * Check size of this.
     */
    void init() {
        C control = getSkinnable();

        // clamp width|height and compare with pref width|height
        // if w|h > prefW|prefH set to min width
        // so we have a minimum w and h to set pref size

        double w = Utils.clamp( control.getWidth(), WIDTH_MIN, WIDTH_MAX );
        double h = Utils.clamp( control.getHeight(), HEIGHT_MIN, HEIGHT_MAX );

        double prefW = w > control.getPrefWidth()
                ? w
                : Utils.clamp( control.getPrefWidth(), WIDTH_MIN, WIDTH_MAX );
        double prefH = h > control.getPrefHeight()
                ? h
                : Utils.clamp( control.getPrefHeight(), HEIGHT_MIN, HEIGHT_MAX );       //;
        // ,GADI

        control.setPrefSize( prefW, prefH );

    }

    /**
     * Main draw method.
     */
    public abstract void draw();

    /**
     * Called from listener code if the control size changed.
     *
     * @param newWidth  width
     * @param newHeight height
     */
    public abstract void resize( double newWidth, double newHeight );
}
