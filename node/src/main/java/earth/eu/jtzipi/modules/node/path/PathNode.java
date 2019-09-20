package earth.eu.jtzipi.modules.node.path;


import earth.eu.jtzipi.modules.io.IOUtils;
import earth.eu.jtzipi.modules.node.INode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Default Path Node.
 *
 * @author jTzipi
 */
public class PathNode implements IPathNode, Comparable<IPathNode> {



    static final Comparator<IPathNode> COMP = Comparator.comparing(IPathNode::isDir).thenComparing( IPathNode::isReadable ).thenComparing( IPathNode::getName );
    private static final Logger LOG = LoggerFactory.getLogger( "");
    /** parent node. If null root. */
    IPathNode parent;
    /** path to this node. */
    Path path;
    /** sub nodes.*/
    List<? extends IPathNode> subNodeL;
    String desc;
    String type;
    long length;
    int depth;
    boolean link;
    boolean dir;
    boolean readable;
    /** Indicator for subnodes created */
    boolean subNodesCreated;
    private String name;
    /**
     * PathNode main.
     * @param path
     * @param parentPathNode
     */
    PathNode( final Path path, final IPathNode parentPathNode ) {

        this.parent = parentPathNode;
        this.path = path;
    }

    /**
     * Create new path node.
     * @param path
     * @param parentNode
     * @return
     */
    public static PathNode of( final Path path, final IPathNode parentNode )  {
        Objects.requireNonNull(path);

        PathNode pn = new PathNode( path, parentNode );

        pn.init( path );


        return pn;
    }

    private void init( final Path path )  {
        this.dir = Files.isDirectory( path );
        try {
            this.length = dir? DIR_LENGTH : Files.size( path );
            this.type = Files.probeContentType( path );
        } catch ( IOException e ) {

            this.length = 0L;
            this.type = "<Unknown>";
        }
        this.readable = Files.isReadable(path);

        this.subNodesCreated = false;
        this.name = IOUtils.getFileName( path );
        this.desc = IOUtils.getFileDescription( path );
        this.depth = path.getNameCount();



    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    @Override
    public boolean isLink() {
        return link;
    }

    @Override
    public boolean isDir() {
        return dir;
    }

    @Override
    public boolean isReadable() {
        return readable;
    }

    @Override
    public long getFileLength() {
        return length;
    }

    @Override
    public int getDepth() {

        return depth;
    }

    @Override
    public String getType() {
        return desc;
    }

    @Override
    public List<? extends IPathNode> getSubnodes() {

        if( !subNodesCreated ) {

            this.subNodeL = getSubnodes( IOUtils.PATH_ACCEPT_ALL );

        }

        return this.subNodeL;
    }

    @Override
    public List<? extends IPathNode> getSubnodes( Predicate<Path> pp ) {
        if(!isDir()) {
            return Collections.emptyList();
        }
        // if not created
        if( !subNodesCreated ) {


            this.subNodeL = IOUtils.lookupDir( getValue() )
                    .stream()
                    .filter(pp)
                    .sorted()
                    .map( sp -> PathNode.of( sp, PathNode.this ) )
                    .collect( Collectors.toList() );
            this.subNodesCreated = true;
        }

        return this.subNodeL;
    }

    @Override
    public INode<Path> getParent() {
        return parent;
    }

    @Override
    public boolean isLeaf() {
        return (!isDir() && !isLink()) || !isReadable();
    }

    @Override
    public Path getValue() {
        return path;
    }

    @Override
    public int hashCode() {
        int res = Objects.hashCode( getValue() );
        res= 79 * res + Long.hashCode( getFileLength() );
        res= 79 * res + Objects.hashCode( getDesc() );
        res = 79 * res + Objects.hashCode( getName() );

        return res;
    }

    @Override
    public boolean equals( Object object ) {
        if( this == object ) {
            return true;
        }
        if( !(object instanceof IPathNode) ) {
            return false;
        }

        IPathNode other = (IPathNode) object;
        Path thisPath = getValue();
        Path otherPath = other.getValue();


        return thisPath.normalize().equals( otherPath.normalize() );
    }

    @Override
    public int compareTo( IPathNode pathNode ) {
        return COMP.compare( this, pathNode );
    }
}
