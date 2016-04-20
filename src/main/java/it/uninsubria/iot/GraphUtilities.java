package it.uninsubria.iot;

import edu.uci.ics.jung.algorithms.shortestpath.MinimumSpanningForest;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.util.Pair;
import it.uninsubria.iot.entities.Provider;
import it.uninsubria.iot.entities.Service;
import it.uninsubria.iot.entities.Trust;
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
        if (trees != 1) {
            System.err.println("Discarding graph which has " + trees + " spanning trees");
            return false;
        }
        return true;
    }

}
