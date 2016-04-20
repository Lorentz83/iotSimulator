package it.uninsubria.iot;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import it.uninsubria.iot.entities.Provider;
import it.uninsubria.iot.entities.Service;
import it.uninsubria.iot.entities.ServiceFactory;
import it.uninsubria.iot.entities.Trust;
import it.uninsubria.iot.utils.CustomRandom;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
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
        int connectionsNumber = 200;
        int iterations = 100;
        int serviceNumber = 10;

        JDKRandomGenerator rnd = new JDKRandomGenerator();
        //rnd.setSeed(27592);
        CustomRandom crnd = new CustomRandom(rnd);

        ServiceFactory serviceFactory = new ServiceFactory(serviceNumber);
        int maxTry = 20;
        DirectedSparseMultigraph<Provider, Trust> graph;
        do {
            List<Service> services = serviceFactory.getServices();
            GraphGenerator generator = new GraphGenerator(crnd, services);
            graph = generator.createGraph(serviceProvidersNumber, reputationProvidersNumber, connectionsNumber, iterations);
            if (!GraphUtilities.isGood(graph)) {
                graph = null;
            }
        } while (maxTry-- > 0 && graph == null);

        if (graph == null) {
            System.err.println("It wasn't possible to generate a suitable graph with the parameters selected");
            System.exit(1);
        }

        GraphUtilities.toDot(graph, System.out);

        if (edgeFile != null && nodeFile != null) {
            try (PrintWriter node = new PrintWriter(nodeFile);
                    PrintWriter edge = new PrintWriter(edgeFile);) {

                GraphUtilities.export(graph, node, edge);

            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

}
