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

package earth.eu.jtzipi.modules.node.path;

import earth.eu.jtzipi.modules.io.IOUtils;
import earth.eu.jtzipi.modules.io.ZipUtils;
import earth.eu.jtzipi.modules.node.INode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

/**
 * Zip Path Node.
 * <p>
 * <p>
 * Zip path node is a directory wrapper for a zip archive.
 *
 * </p>
 */
public class ZipPathNode implements IPathNode {

    /**
     * parent node.
     */
    IPathNode parent;
    /**
     * relative path .
     */
    Path relPath;
    /**
     * Path to zip file.
     */
    Path zip;

    List<IPathNode> subNodeL;

    private boolean subNodesCreated;

    private String name;
    private String desc;

    private boolean dir;
    private boolean leaf;
    private boolean symlink;
    private boolean readable;
    private boolean hidden;

    private long length;

    /**
     * ZipPathNode main.
     *
     * @param rootPath       root path of zip
     * @param relPath        relative path to root
     * @param parentPathNode parent
     */
    ZipPathNode( final Path rootPath, final Path relPath, final IPathNode parentPathNode ) {
        this.zip = rootPath;
        this.relPath = relPath;
        this.parent = parentPathNode;
        this.subNodesCreated = false;
    }


    public static ZipPathNode of( final Path path, final IPathNode parentNode ) {
        Objects.requireNonNull( path );
        final Path zipRoot;
        final Path relPath;
        // if paren is zip path too forward zip root
        if ( parentNode instanceof ZipPathNode ) {

            final ZipPathNode zipParent = ( ZipPathNode ) parentNode;
            zipRoot = zipParent.getZipRoot();
            relPath = path;
        } else {
            // This is zip root file
            zipRoot = path;
            relPath = Paths.get( "/" );
        }



        // create new instance
        final ZipPathNode zpn = new ZipPathNode( zipRoot, relPath, parentNode );
        // init
        zpn.init( path, relPath );
        return zpn;
    }



    Path getZipRoot() {
        return zip;
    }

    /**
     * Init this node.
     *
     * @param zipPath path to zip file
     * @param relPath relative path to this node
     */
    private void init( final Path zipPath, final Path relPath ) {


        this.name = relPath.getFileName().toString();
        this.desc = "";
        try {
            final BasicFileAttributes bfa = ZipUtils.readZipAttributes( zipPath, relPath );
            this.dir = bfa.isDirectory();
            this.leaf = !dir;
            this.length = bfa.size();
            this.symlink = bfa.isSymbolicLink();
            this.readable = true;
            this.hidden = Files.isHidden(relPath);

        } catch ( final IOException ioE ) {
            this.dir = false;
            this.leaf = true;
            this.length = 0L;
            this.symlink = false;
            this.readable = false;

        }


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
        return symlink;
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
        return 0;
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public boolean isHidden() {

        return hidden;
    }

    @Override
    public boolean isCreatedSubNode() {

        return subNodesCreated;
    }


    @Override
    public List<IPathNode> getSubnodes( final Predicate<? super Path> predicate, boolean streamProp ) {

        if ( !isCreatedSubNode() ) {

            this.subNodeL = IOUtils.streamZip( getZipRoot(), getValue() )
                    .stream()
                    .filter( predicate )
                    .sorted()
                    .map( zipp -> of( zipp, ZipPathNode.this ) )
                    .collect( toList() );
            this.subNodesCreated = true;
        }
        return subNodeL;
    }

    @Override
    public Optional<FileTime> getCreated() {
        return Optional.empty();
    }

    @Override
    public INode<Path> getParent() {
        return parent;
    }

    @Override
    public boolean isLeaf() {
        return leaf;
    }

    @Override
    public Path getValue() {

        return relPath;
    }


    @Override
    public int compareTo( final IPathNode pathNode ) {

        return COMP.compare( this, pathNode );
    }

}