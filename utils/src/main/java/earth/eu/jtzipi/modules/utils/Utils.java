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

package earth.eu.jtzipi.modules.utils;

import java.util.Objects;

/**
 * Utils.
 *
 * @author jtzipi
 */
public final class Utils {

    private Utils() {
        throw new AssertionError();
    }

    /**
     * Clamp a given number to  [min max].
     *
     * @param val value
     * @param min min inclusive
     * @param max max inclusive
     * @param <T> type of number
     * @return clamped value
     * @throws NullPointerException if {@code val}|{@code min}|{@code max} are null
     */
    public static <T extends Number & Comparable<? super T>> T clamp( final T val, final T min, final T max ) {
        Objects.requireNonNull( val, "value is null" );
        Objects.requireNonNull( min, "min value is null" );
        Objects.requireNonNull( max, "max value is null" );

        final T ret;

        if ( max.compareTo( val ) < 0 ) {

            ret = max;
        } else if ( min.compareTo( val ) > 0 ) {
            ret = min;
        } else {
            ret = val;
        }

        return ret;
    }

    /**
     * Coerce an unchecked Throwable to a RuntimeException.
     * <p>
     * <p>
     * If the Throwable is an Error, throw it; if it is a
     * RuntimeException return it, otherwise throw IllegalStateException.
     * <p>
     * Author: Brian Goetz
     */
    public static RuntimeException launderThrowable( Throwable t ) {

        if ( t instanceof RuntimeException )
            return ( RuntimeException ) t;
        else if ( t instanceof Error )
            throw ( Error ) t;
        else
            throw new IllegalStateException( "Not unchecked", t );
    }
}
