package earth.eu.jtzipi.modules.io.image;


import earth.eu.jtzipi.modules.io.FileSig;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;


/**
 * Image Types readable by JDK.
 */
public enum ImageType {

    /**
     * GIF.
     */
    GIF( 1L, "Graphic", "gif" ),
    /**
     *
     */
    JPEG( 2L, "Joint Picture", "jpg", "jpeg", "jpe", "jif", "jfif", "jfi" ),
    /**
     *
     */
    PNG( 4L, "Portable", "png" ),
    /**
     *
     */
    BMP( 10L, "Bitmap", "bmp" ),
    /**
     *
     */
    UNKNOWN( 0L, "Unknown Format", "" );

    private final long i;
    private final String d;
    private final List<String> suffixL;

    /**
     * @param icon   id
     * @param desc   description
     * @param suffix one to many suffice
     */
    ImageType( long icon, final String desc, final String... suffix ) {
        this.i = icon;
        this.d = desc;
        this.suffixL = Arrays.asList( suffix );
    }

    /**
     * Return image type of File signature if match.
     *
     * @param fs signature
     * @return Type if matching else {@link ImageType#UNKNOWN}
     */
    public static ImageType of( FileSig fs ) {
        Objects.requireNonNull( fs );
        if ( fs.type() != FileSig.Type.IMAGE ) {

            return UNKNOWN;
        }

        ImageType itype;

        switch ( fs ) {
            case JPG:
            case JPG_RAW:
            case JPG_JFIF:
                itype = JPEG;
                break;
            case PNG:
                itype = PNG;
                break;
            case GIF:
                itype = GIF;
                break;
            case BMP:
                itype = BMP;
                break;
            default:
                itype = UNKNOWN;
        }

        return itype;
    }

    public String getDesc() {
        return d;
    }

    public List<String> getSuffixList() {
        return suffixL;
    }

}
