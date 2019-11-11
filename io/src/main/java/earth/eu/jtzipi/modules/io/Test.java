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

package earth.eu.jtzipi.modules.io;



import earth.eu.jtzipi.modules.io.watcher.Watcher;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;


public class Test {


    public static void main( String[] args ) {


        Path images = IOUtils.getHomeDir();

        try {
            Watcher watch = Watcher.create();
            FutureTask<?> f = watch.watch( images.resolve( "_Gadi" ));


            Executors.newCachedThreadPool().submit( f ).get();

        } catch ( IOException | InterruptedException | ExecutionException ioE ) {

            System.out.println("Fehler " + ioE.getLocalizedMessage() );
        }

        System.out.println("ENDE");
    }
}
