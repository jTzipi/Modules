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

package earth.eu.jtzipi.modules.fx;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Glow;
import javafx.scene.effect.ImageInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Rating widget.
 *
 * @author jTzipi
 */
public class RatingFx extends Pane {


    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger( "RatingWidget" );

    private final Image image;                // base image
    private final int max;                      // rating max
    private final boolean split;                // split icon property


    /**
     * Icons for rate.
     */
    private final List<RatableIcon> iconList;
    /**
     * we track the hover position to give a feedback.
     */
    private final IntegerProperty fxHoverProp = new SimpleIntegerProperty( this, "", -1 );
    /**
     * we track the selected rating pos.
     */
    private final IntegerProperty fxRatingPosProp = new SimpleIntegerProperty( this, "", -1 );


    /**
     * RatingWidget.
     *
     * @param ratingMax maximal rating
     * @param ratedTo   current rating
     * @param baseImage image for rating
     * @param splitProp should image splitted
     */
    RatingFx( final int ratingMax, final int ratedTo, final Image baseImage, boolean splitProp ) {

        this.max = splitProp ? ratingMax * 2 : ratingMax;
        this.split = splitProp;
        this.image = baseImage;
        this.iconList = new ArrayList<>();
        this.fxRatingPosProp.setValue( ratedTo );


        init();
        createRatingWidget();

    }


    /**
     * Create rating widget.
     *
     * @param baseImage icon
     * @param max       max val
     * @param current   current val
     * @param splitProp split icon
     * @return rating
     */
    public static RatingFx of( Image baseImage, int max, int current, boolean splitProp ) {


        return new RatingFx( max, current, baseImage, splitProp );
    }

    private static List<Image> splitImage( Image image ) {

        double w = image.getWidth();
        double h = image.getHeight();

        PixelReader pr = image.getPixelReader();
        int imgMid = ( int ) w / 2;
        int imgH = ( int ) h;

        WritableImage westImage = new WritableImage( pr, imgMid, imgH );
        WritableImage eastImage = new WritableImage( pr, imgMid, 0, imgMid, imgH );

        List<Image> imageList = new ArrayList<>();
        imageList.add( westImage );
        imageList.add( eastImage );
        return imageList;
    }


    private void init() {

        List<Image> imageList = split ? splitImage( image ) : null;
        int ratedTo = fxRatingPosProp.getValue();
        //LOG.warn( "Gadi " + max + " " );
        for ( int i = 0; i < max; i++ ) {

            Image icon;
            if ( split ) {

                icon = i % 2 == 0 ? imageList.get( 0 ) : imageList.get( 1 );

            } else {
                icon = image;
            }
            //LOG.warn( "Gadi " + i );
            // We store the rating value beginning with 1 to avoid
            // problems with zero
            int rating = i + 1;

            iconList.add( new RatableIcon( icon, rating, ratedTo >= rating ) );
        }

    }

    private void createRatingWidget() {

        double w = image.getWidth();
        double h = image.getHeight();
        // if we split image we need to do here too
        if ( split ) {
            w = ( int ) w / 2D;
        }

        double offX = 5D;
        double layoutY = 3D;
        getChildren().setAll( iconList );

        for ( int i = 0; i < iconList.size(); i++ ) {

            RatableIcon splitIcon = iconList.get( i );
            double layoutX = offX + i * w;


            splitIcon.setLayoutX( layoutX );
            splitIcon.setLayoutY( layoutY );
        }

        setPrefWidth( max * w + 12D );
        setPrefHeight( h + 5D );


        // when the mouse exit widget we restore last rated icon
        setOnMouseExited( me -> fxHoverProp.setValue( fxRatingPosProp.getValue() ) );
    }

    /**
     * Image view rating.
     * <p>
     * If the user hover over icon we color the image. If the user hover away we make the image gray.
     * If the user click on icon we display glow if this icon was not rated. If it was rated we display it gray.
     * So the user get a visual cue of what happend.
     */
    private class RatableIcon extends ImageView {

        private final ColorAdjust grayScaled = new ColorAdjust( 0D, -1D, 0D, 0D );
        private final Glow glow = new Glow();

        private final int ratingIdx;
        private final ImageInput imageInput;
        private boolean rated;


        /**
         * Split Icon.
         *
         * @param image       icon
         * @param ratingIndex rating index &gt; 0
         * @param ratedProp   is this icon rated
         */
        RatableIcon( Image image, int ratingIndex, boolean ratedProp ) {

            super( image );
            this.ratingIdx = ratingIndex;
            this.rated = ratedProp;
            this.imageInput = new ImageInput( image );
            this.glow.setInput( imageInput );
            this.grayScaled.setInput( imageInput );
            // if not rated we gray scale it
            if ( !rated ) {
                setEffect( grayScaled );
            }

            RatingFx.this.fxRatingPosProp.addListener( this::onRatedChanged );
            RatingFx.this.fxHoverProp.addListener( this::onRatingHoverChanged );
            setOnMouseClicked( this::onMouseClicked );
            setOnMouseEntered( this::onMouseEntered );
            setOnMouseExited( this::onMouseExited );
        }

        private void onMouseEntered( MouseEvent me ) {

            RatingFx.this.fxHoverProp.setValue( ratingIdx );
        }


        private void onMouseClicked( MouseEvent me ) {
            // Attention: We need to set this here
            // to set this icon as unrated if it was rated
            // for all other icons left the rated status will be set
            // via listener
            this.rated = !rated;
            // if this icon was rated and we click it again
            // we unrate this
            if ( !rated && fxRatingPosProp.get() == ratingIdx ) {
                RatingFx.this.fxRatingPosProp.setValue( 0 );
            } else {
                RatingFx.this.fxRatingPosProp.setValue( ratingIdx );

            }


        }

        private void onMouseExited( MouseEvent me ) {

            RatingFx.this.fxHoverProp.setValue( ratingIdx );
        }

        private void onRatedChanged( ObservableValue<? extends Number> obs, Number oldRating, Number newPos ) {

            if ( null == newPos ) {

                return;
            }

            if ( newPos.intValue() >= ratingIdx ) {
                if ( !rated ) {
                    rated = true;

                }

                setEffect( glow );
            } else {
                if ( rated ) {
                    rated = false;


                }
                setEffect( grayScaled );
            }
        }

        private void onRatingHoverChanged( ObservableValue<? extends Number> obs, Number oldRating, Number newPos ) {

            if ( null == newPos || oldRating.equals( newPos ) ) {

                return;
            }

            if ( newPos.intValue() >= ratingIdx ) {
                //LOG.warn( "New onhover rating >= pos " + newPos.intValue() );

                setEffect( null );
            } else {
                // LOG.warn( "New onhover " + newPos.intValue() );
                if ( !rated && getEffect() == null ) {
                    setEffect( grayScaled );
                }
            }
        }
    }

}