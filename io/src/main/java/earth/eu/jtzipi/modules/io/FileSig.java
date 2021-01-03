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

package earth.eu.jtzipi.modules.io;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
public enum FileSig {

    GIF( Type.IMAGE, 0L, "47 49 46 38 37 61", "47 49 46 38 39 61" ),
    JPG( Type.IMAGE, 0L, "FF D8 FF" ),
    JPG_RAW( Type.IMAGE, 0L, "FF D8 FF DB" ),
    JPG_JFIF( Type.IMAGE, 0L, "FF D8 FF E0 00 10 4A 46 49 46 00 01" ),
    PNG( Type.IMAGE, 0L, "89 50 4E 47 0D 0A 1A 0A" ),
    BMP( Type.IMAGE, 0L, "" ),

    TTF( Type.FONT, 0L, "" ),

    UNKNOWN( Type.UNKNOWN, -1L, "" );

    private final Type type;
    private final long offset;
    private final List<String> codeL;


    /**
     * @param type    Type of file
     * @param offset  offset for code
     * @param hexCode signat
     */
    FileSig( final Type type, final long offset, final String... hexCode ) {
        this.type = type;
        this.offset = offset;
        this.codeL = Arrays.asList( hexCode );
    }

    /**
     * Test
     *
     * @param bytes gadi
     * @return if byte match
     */
    public boolean match( final byte[] bytes ) {

        if ( 0 == bytes.length ) {
            return false;
        }


        final StringBuilder builder = new StringBuilder();

        for ( int i = 0; i < bytes.length; i++ ) {
            builder.append( String.format( "%02X", bytes[i] ) );
        }

        final String chk = builder.toString();
        for ( final String code : codeL ) {
            if ( code.trim().startsWith( chk ) ) {
                return true;
            }
        }
        return false;
    }

    public long getOffset() {
        return offset;
    }

    public List<String> getHexList() {
        return codeL;
    }

    public Type type() {
        return type;
    }

    public enum Type {
        IMAGE,
        FONT,
        UNKNOWN
    }
}
