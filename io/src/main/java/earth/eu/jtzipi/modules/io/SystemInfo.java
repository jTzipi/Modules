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

public final class SystemInfo {

    /**
     * No Access.
     */
    private SystemInfo() {

        throw new AssertionError();
    }

    /**
     * Return system dependent path separator.
     *
     * @return '/' on linux like systems and '\' else
     */
    public static String getPathSeparator() {

        return System.getProperty( "path.separator" );
    }

    /**
     * Return name of currently logged in user.
     *
     * @return user name
     */
    public static String getUser() {

        return System.getProperty( "user.name" );
    }

    /**
     * Return user home directory.
     *
     * @return user home
     */
    public static String getUserHome() {

        return System.getProperty( "user.home" );
    }

    /**
     * Return user application dir.
     *
     * @return user application dir.
     */
    public static String getUserDir() {

        return System.getProperty( "user.dir" );
    }

    /**
     * Return name of currently logged in user.
     *
     * @return user name
     */
    public static String getOSName() {

        return System.getProperty( "os.name" );
    }

    /**
     * Return name of os arch.
     *
     * @return os arch name
     */
    public static String getOSArch() {

        return System.getProperty( "os.arch" );
    }

    /**
     * Return name of currently logged in user.
     *
     * @return user name
     */
    public static String getOSVersion() {

        return System.getProperty( "os.version" );
    }

    /**
     * Return java home directory.
     *
     * @return user name
     */
    public static String getJavaHome() {

        return System.getProperty( "java.home" );
    }

    /**
     * @return
     */
    public static String getJavaVersion() {

        return System.getProperty( "java.version" );
    }

    /**
     * @return
     */
    public static String getJavaVendor() {

        return System.getProperty( "java.vendor" );
    }
}
