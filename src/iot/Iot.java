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

import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.Pair;
import iot.entities.Provider;
import iot.entities.Service;
import iot.entities.ServiceFactory;
import iot.entities.Trust;
import iot.utils.CustomRandom;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author Lorenzo Bossi
 */
public class Iot {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        DirectedSparseMultigraph<Provider, Trust> graph = createGraph(10, .2, 15, 20, new Random());
        toDot(graph, System.out);
    }

    private static DirectedSparseMultigraph<Provider, Trust> createGraph(int providersNumber, double reputationRatio, int trustRelationsNumber, int servicesNumber, Random random) {
        DirectedSparseMultigraph<Provider, Trust> graph = new DirectedOrderedSparseMultigraph<>();

        final int reputationProvidersNumber = (int) (providersNumber * reputationRatio);
        final int serviceProvidersNumber = providersNumber - reputationProvidersNumber;

        ServiceFactory serviceFactory = new ServiceFactory(servicesNumber);
        List<Provider> providers = new ArrayList<>();

        CustomRandom crnd = new CustomRandom(random);

        for (int i = 0; i < reputationProvidersNumber; i++) {
            Set<Service> services = crnd.randomSubset(serviceFactory.getServices(), crnd.getServiceNumber());
            providers.add(new Provider(String.format("RP%02d", i), true, services));

        }
        for (int i = 0; i < serviceProvidersNumber; i++) {
            Set<Service> services = crnd.randomSubset(serviceFactory.getServices(), crnd.getServiceNumber());
            providers.add(new Provider(String.format("SP%02d", i), false, services));
        }

        for (Provider p : providers) {
            graph.addVertex(p);
        }

        for (int i = 0; i < trustRelationsNumber; i++) {
            Pair<Provider> p = crnd.getPair(providers);
            float level = crnd.getTrustLevel();
            Service service = crnd.randomElement(p.getSecond().getServices());
            Trust trust = new Trust(service, level);
            graph.addEdge(trust, p);
        }

        return graph;
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
