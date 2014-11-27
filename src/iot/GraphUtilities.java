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

import edu.uci.ics.jung.algorithms.shortestpath.MinimumSpanningForest;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.util.Pair;
import iot.entities.Provider;
import iot.entities.Service;
import iot.entities.Trust;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A set of static methods to inspect and print the graphs.
 *
 * @author Lorenzo Bossi
 */
public class GraphUtilities {

    /**
     * Print the graph in dot format.
     *
     * @param graph the graph to save.
     * @param out the stream to write the graph.
     */
    public static void toDot(DirectedSparseMultigraph<Provider, Trust> graph, PrintStream out) {
        out.println("digraph {");
        Set<Provider> printedProviders = new HashSet<>();
        for (Trust t : graph.getEdges()) {
            Pair<Provider> p = graph.getEndpoints(t);
            printedProviders.add(p.getFirst());
            printedProviders.add(p.getSecond());
            out.printf("  %s -> %s [label=\"%s\"];\n", p.getFirst().getFullName(), p.getSecond().getFullName(), t.getLabel());
        }
        for (Provider p : graph.getVertices()) {
            if (!printedProviders.contains(p)) {
                out.printf("  %s;\n", p.getFullName());
            }
        }
        out.println("}");
    }

    /**
     * Export the graph in a custom format. The format is:<br/>
     * node file:<br/>
     * node_id,service[,service] <br/>
     * edge file: <br/>
     * node_1,node_2,service=trust[,service=trust]
     *
     * @param graph the graph to export.
     * @param node the stream to write nodes.
     * @param edge the stream to write edges.
     */
    public static void export(DirectedSparseMultigraph<Provider, Trust> graph, PrintWriter node, PrintWriter edge) {
        for (Provider p : graph.getVertices()) {
            node.print(p.getName());
            for (Service s : p.getServices()) {
                node.printf(",%s_1", s.getName());
            }
            node.println();
        }
        for (Provider p1 : graph.getVertices()) {
            for (Provider p2 : graph.getVertices()) {
                Collection<Trust> es = graph.findEdgeSet(p1, p2);
                if (!es.isEmpty()) {
                    edge.printf("%s,%s", p1.getFullName(), p2.getFullName());
                    for (Trust t : es) {
                        edge.printf(",%s=%f", t.getService().getName(), t.getLevel());
                    }
                    edge.println();
                }
            }
        }
    }

    /**
     * Checks if the random generated graph is good to test our algorithm. Since
     * we need small graphs for some tests, not all the random graphs are
     * suitable. For example we want to filter out that graphs that are not
     * fully connected (because it would mean to have two smaller communities
     * instead of the one we want to test). This method checks if the random
     * graph match all our assumptions.
     *
     * @param graph the graph to check.
     * @return true if the graph is good for our test.
     */
    public static boolean isGood(DirectedSparseMultigraph<Provider, Trust> graph) {
        Forest<Provider, Trust> forest = new DelegateForest<>();
        int trees = new MinimumSpanningForest<>(graph, forest, graph.getVertices().iterator().next()).getForest().getTrees().size();
        return trees == 1;
    }

}
