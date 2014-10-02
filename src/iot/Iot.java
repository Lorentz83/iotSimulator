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
import iot.entities.Service;
import iot.entities.ServiceFactory;
import iot.entities.Trust;
import iot.utils.CustomRandom;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Collection;
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
        String nodeFile = null;
        String edgeFile = null;
        if (args.length == 2) {
            nodeFile = args[1];
            edgeFile = args[0];
        }
        int serviceProvidersNumber = 20;
        int reputationProvidersNumber = 5;
        int connectionsNumber = 30;
        int iterations = 100;
        int serviceNumber = 10;

        JDKRandomGenerator rnd = new JDKRandomGenerator();
        //rnd.setSeed(27592);
        CustomRandom crnd = new CustomRandom(rnd);

        ServiceFactory serviceFactory = new ServiceFactory(serviceNumber);
        GraphGenerator generator = new GraphGenerator(new CustomRandom(rnd), serviceFactory.getServices());
        DirectedSparseMultigraph<Provider, Trust> graph = generator.createGraph(serviceProvidersNumber, reputationProvidersNumber, connectionsNumber, iterations);
        toDot(graph, System.out);

        if (edgeFile != null && nodeFile != null) {
            try (PrintWriter node = new PrintWriter(nodeFile);
                    PrintWriter edge = new PrintWriter(edgeFile);) {

                export(graph, node, edge);

            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

    private static void toDot(DirectedSparseMultigraph<Provider, Trust> graph, PrintStream out) {
        out.println("digraph {");
        Set<Provider> printedProviders = new HashSet<>();
        for (Trust t : graph.getEdges()) {
            Pair<Provider> p = graph.getEndpoints(t);
            printedProviders.add(p.getFirst());
            printedProviders.add(p.getSecond());

            //A1 -> A2 [label=f];
            out.printf("  %s -> %s [label=\"%s\"];\n", p.getFirst().getFullName(), p.getSecond().getFullName(), t.getLabel());
        }

        for (Provider p : graph.getVertices()) {
            if (!printedProviders.contains(p)) {
                out.printf("  %s;\n", p.getFullName());
            }
        }

        out.println("}");
    }

    private static void export(DirectedSparseMultigraph<Provider, Trust> graph, PrintWriter node, PrintWriter edge) {
        //nodes file
        for (Provider p : graph.getVertices()) { //id,skill_val1,skill_val2,skill_val3,...,skill_val10
            node.print(p.getName());
            for (Service s : p.getServices()) {
                node.printf(",%s_1", s.getName());
            }
            node.println();
        }
        //edges file
        for (Provider p1 : graph.getVertices()) {
            for (Provider p2 : graph.getVertices()) {
                Collection<Trust> es = graph.findEdgeSet(p1, p2);
                if (!es.isEmpty()) { //id_u,id_v,skill_id1=val1,skill_id2=val2,skill_idn=valn
                    edge.printf("%s,%s", p1.getFullName(), p2.getFullName());
                    for (Trust t : es) {
                        edge.printf(",%s=%f", t.getService().getName(), t.getLevel());
                    }
                    edge.println();
                }
            }
        }
    }

}
