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
     * @param bytes
     * @return
     */
    public boolean match( byte[] bytes ) {

        if ( 0 == bytes.length ) {
            return false;
        }


        StringBuilder builder = new StringBuilder();

        for ( int i = 0; i < bytes.length; i++ ) {
            builder.append( String.format( "%02X", bytes[i] ) );
        }

        String chk = builder.toString();
        for ( String code : codeL ) {
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
        UNKNOWN;
    }
}
