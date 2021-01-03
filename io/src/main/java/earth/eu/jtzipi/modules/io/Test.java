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


import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;


public final class Test {


    public static void main( final String[] args ) {


        final Path images = IOUtils.getHomeDir();

//        try {
//            Watcher watch = Watcher.create();
//            FutureTask<?> f = watch.watch( images.resolve( "_Gadi" ));
//
//
//            Executors.newCachedThreadPool().submit(f);
//
//        } catch ( IOException ioE ) {
//
//            System.out.println("Fehler " + ioE.getLocalizedMessage() );
//        }



        System.out.println("ENDE");

        final int[] bla = {0, 12, 3, 12, 1, 1, 1, 1, 1, 1, 1};
        System.err.println("Habe Duplikat ? " + getDuplicate( bla )  );

    }


    public static boolean getDuplicate( final int[] numberArray ) {
        // Wenn null oder keine Zahlen
        if ( null == numberArray || numberArray.length == 0 ) {
            return false;
        }

        // Speichere alle Vorkommen der Zahlen
        final Map<Integer, Long> cmap = new HashMap<>();

        // Pro Zahl erhöhe das Vorkommen um 1 wenn vorhanden oder setze auf 1 wenn noch nicht vorhanden war
        for ( final int temp : numberArray ) {

            //
            // Das Bedeutet:
            // Wenn 'temp' noch nicht da war füge 1 hinzu
            // sonst erhöhe die Zahl der Vorkommen von 'temp' um 1
            cmap.merge( temp, 1L, ( i1, i2 ) -> i1 = i1 + 1 );
        }
        // Wie viele Zahl
        final int size = numberArray.length;

        // 1. Streame die Einträge der Map
        // 2. Filtere nach alle Vorkommen von Zahlen die mehr als die Hälfte der Elemente

       return cmap.entrySet()
               .stream()
               .filter( ent -> ent.getValue() > size/2 )

               .count() > 0;
    }
}
