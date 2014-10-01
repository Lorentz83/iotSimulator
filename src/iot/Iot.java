/*
 * Copyright (C) 2014 Lorenzo Bossi
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package iot;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.Pair;
import iot.entities.Provider;
import iot.entities.ServiceFactory;
import iot.entities.Trust;
import iot.utils.CustomRandom;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.math3.random.JDKRandomGenerator;

/**
 *
 * @author Lorenzo Bossi
 */
public class Iot {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        int serviceProvidersNumber = 20;
        int reputationProvidersNumber = 5;
        int connectionsNumber = 30;
        int iterations = 100;
        int serviceNumber = 10;

        JDKRandomGenerator rnd = new JDKRandomGenerator(serviceNumber);
        //rnd.setSeed(27592);
        CustomRandom crnd = new CustomRandom(rnd);
        ServiceFactory serviceFactory = new ServiceFactory(10);
        GraphGenerator generator = new GraphGenerator(new CustomRandom(rnd), serviceFactory.getServices());
        DirectedSparseMultigraph<Provider, Trust> graph = generator.createGraph(serviceProvidersNumber, reputationProvidersNumber, connectionsNumber, iterations);
        toDot(graph, System.out);
    }

    private static void toDot(DirectedSparseMultigraph<Provider, Trust> graph, PrintStream out) {
        out.println("digraph {");
        Set<Provider> printedProviders = new HashSet<>();
        for (Trust t : graph.getEdges()) {
            Pair<Provider> p = graph.getEndpoints(t);
            printedProviders.add(p.getFirst());
            printedProviders.add(p.getSecond());

            //A1 -> A2 [label=f];
            out.printf("  %s -> %s [label=\"%s\"];\n", p.getFirst().getName(), p.getSecond().getName(), t.getLabel());
        }

        for (Provider p : graph.getVertices()) {
            if (!printedProviders.contains(p)) {
                out.printf("  %s;\n", p.getName());
            }
        }

        out.println("}");
    }

}
