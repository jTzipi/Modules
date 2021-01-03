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

/**
 * Operation System.
 */
public enum OS {

    /**
     * Linux Unix.
     */
    LINUX( "/" ),
    /**
     * Windows.
     */
    WINDOWS( System.getenv( "COMPUTERNAME" ) ),
    /**
     * DOS.
     */
    DOS( "C:" ),
    /**
     * MacOS.
     */
    MAC( "/" ),
    /**
     * Solaris.
     */
    SOLARIS( "/" ),
    /**
     * Other.
     */
    OTHER( null );
    // root path
    private final String path;

    /**
     * Operating System.
     *
     * @param rootPathStr path to root
     */
    OS( final String rootPathStr ) {
        this.path = rootPathStr;
    }

    /**
     * Try to determine <b>this</b> OS reading System property 'os.name'.
     *
     * @return OS
     */
    public static OS getSystemOS() {
        final String ostr = System.getProperty( "os.name" ).toLowerCase();

        final OS os;
        // Linux Unix
        if ( ostr.matches( ".*(nix|nux|aix).*" ) ) {
            os = LINUX;
        } else if ( ostr.matches( ".*sunos.*" ) ) {
            os = SOLARIS;
        } else if ( ostr.matches( ".*mac.*" ) ) {
            os = MAC;
        } else if ( ostr.matches( ".*win.*" ) ) {
            os = WINDOWS;
        } else if ( ostr.matches( ".*dos.*" ) ) {
            os = DOS;
        } else {
            os = OTHER;
        }

        return os;
    }

    /**
     * Root path.
     *
     * @return path to system root
     */
    public String getRootPath() {
        return path;
    }
}
