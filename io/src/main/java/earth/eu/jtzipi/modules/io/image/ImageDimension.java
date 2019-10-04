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

package earth.eu.jtzipi.modules.io.image;

/**
 * Simple Value Object for image dimension.
 * <p>
 *     Since We do not want a dependency on JavaFX nor use a double
 *     value for dimension we create a simple value object for this.
 *     <br/>
 *     This class is immutable. So threadsafe.
 * </p>
 */
public final class ImageDimension implements Comparable<ImageDimension> {

    /**
     * Empty Dimension that is a dimension with no length.
     * <p>
     *     Width and Height are -1.
     * </p>
     */
    public static final ImageDimension EMPTY = new ImageDimension();

    private final int width;
    private final int height;

    /**
     * Image dimension.
     *
     * @param imageWidth width of image
     * @param imageHeight height of image
     */
    ImageDimension( final int imageWidth, final int imageHeight ) {
        this.width = imageWidth;
        this.height = imageHeight;
    }

    /**
     * No Access.
     */
    private ImageDimension() {
this(-1, -1 );
    }


    public static ImageDimension of( int width, int height ) {
        if( 0 > width || 0 > height ) {
            throw new IllegalArgumentException( "width[=+width+] or height[=+height+] are < 0" );
        }

        return new ImageDimension(width, height);
    }

    /**
     *
     * @return
     */
    public int getWidth() {
        return this.width;
    }

    /**
     *
     * @return
     */
    public int getHeight() {
        return this.height;
    }

    @Override
    public int hashCode() {
        int result = getWidth();
        result = 31 * result + getHeight();
        return result;
    }

    @Override
    public boolean equals( Object other ) {
        if( other == this ) {
            return true;
        }
        if( !(other instanceof ImageDimension)) {
            return false;
        }
        ImageDimension idim = (ImageDimension)other;
        return getWidth() == idim.getWidth() &&
                getHeight() == idim.getHeight();
    }

    @Override
    public String toString() {
        return "ImageDimension{" +
                "width=" + width +
                ", height=" + height +
                '}';
    }

    @Override
    public int compareTo( ImageDimension imageDimension ) {
        return 0;
    }
}
