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
import iot.entities.Provider;
import iot.entities.Service;
import iot.entities.Trust;
import iot.utils.CustomRandom;
import iot.utils.ProportionalRandomPickup;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Graph generator class. This class generates graphs according to how is
 * described in "A Steady State Model for Graph Power Laws" (2002) by David
 * Eppstein , Joseph Wang
 *
 * @author Lorenzo Bossi
 */
public class GraphGenerator {

    private final CustomRandom _rnd;
    private final List<Service> _serviceList;
    private List<Provider> _providers;

    public GraphGenerator(CustomRandom rnd, List<Service> serviceList) {
        _rnd = rnd;
        _serviceList = serviceList;
    }

    /**
     * Generates a random graph. "We generate an initial sparse random graph G
     * with m edges and n vertices, by randomly adding edges between vertices
     * until we have m edges. As discussed below, the initial random
     * distribution of edges is unimportant for our model."
     *
     * @param providersNumber
     * @param connectionsNumber
     * @return
     */
    private DirectedSparseMultigraph<Provider, Object> createRandomGraph(int providersNumber, int connectionsNumber) {
        _providers = new ArrayList<>(providersNumber);

        DirectedSparseMultigraph<Provider, Object> graph = new DirectedSparseMultigraph<Provider, Object>();
        List<Provider> providers = new ArrayList(providersNumber);
        for (int i = 0; i < providersNumber; i++) {
            Set<Service> services = _rnd.randomSubset(_serviceList, _rnd.getServiceNumber(_serviceList.size()));
            Provider provider = new Provider(String.format("P%02d", i), services);
            _providers.add(provider);
            providers.add(provider);
        }
        while (graph.getEdgeCount() < connectionsNumber) {
            graph.addEdge(new Object(), _rnd.getPair(providers));
        }
        return graph;
    }

    /**
     * Redistributes the edges of the graph according to power law. We then
     * iterate the following steps r times on G, where r is a parameter to our
     * model. <br/>
     * 1. Pick a vertex v at random. If there is no edge incident upon v, we
     * repeat this step until v has nonzero degree.  <br/>
     * 2. Pick an edge (u, v) ∈ G at random.  <br/>
     * 3. Pick a vertex x at random.  <br/>
     * 4. Pick a vertex y with probability proportional to degree.  <br/>
     * 5. If (x, y) is not an edge in G and x is not equal to y, then remove
     * edge (u, v) and add edge (x, y).
     *
     * @param graph
     */
    private void createPowerLawGraph(final DirectedSparseMultigraph<Provider, Object> graph, int iterations) {
        ProportionalRandomPickup<Provider> propRnd = new ProportionalRandomPickup<>(_rnd, _providers, p -> {
            Collection<Provider> pred = graph.getPredecessors(p);
            return pred == null ? 0 : pred.size();
        });

        while (iterations-- > 0) {
            //1. Pick a vertex v at random. If there is no edge incident upon v, we repeat this step until v has nonzero degree.
            Provider v;
            Collection<Object> inEdges;
            do {
                v = _rnd.randomElement(_providers);
                inEdges = graph.getInEdges(v);
            } while (inEdges == null || inEdges.isEmpty()); // getInEdges sometimes returns null instead of an empty set;

            //2. Pick an edge (u, v) ∈ G at random.
            Object edge = _rnd.randomElement(inEdges);

            //3. Pick a vertex x at random.
            Provider x = _rnd.randomElement(_providers);
            //4. Pick a vertex y with probability proportional to degree.

            Provider y = propRnd.getNext();

            //5. If (x, y) is not an edge in G and x is not equal to y, then remove edge (u, v) and add edge (x, y).
            if (x != y) { // graph.findEdge(x, y) == null We accept multiple edges between to nodes
                graph.removeEdge(edge);
                graph.addEdge(new Object(), x, y);
            }
        }
    }

    /**
     * Creates a graph which follows power law distribution. Sets the reputation
     * providers as the vertex with most outgoing reputations.
     *
     * @param serviceProvidersNumber
     * @param reputationProvidersNumber
     * @param connectionsNumber
     * @param iterations
     * @return
     */
    public DirectedSparseMultigraph<Provider, Trust> createGraph(int serviceProvidersNumber, int reputationProvidersNumber, int connectionsNumber, int iterations) {
        DirectedSparseMultigraph<Provider, Object> skelGraph = createRandomGraph(serviceProvidersNumber + reputationProvidersNumber, connectionsNumber);
        createPowerLawGraph(skelGraph, iterations);
        final DirectedSparseMultigraph<Provider, Trust> graph = new DirectedOrderedSparseMultigraph<>();
        for (Provider p : _providers) {
            graph.addVertex(p);
        }
        for (Provider to : _providers) {
            for (Object edge : skelGraph.getInEdges(to)) {
                Provider from = skelGraph.getSource(edge);
                Service service = _rnd.randomElement(to.getServices());
                double trust = _rnd.getTrustLevel();
                Trust t = new Trust(service, trust);
                graph.addEdge(t, from, to);
            }
        }

        _providers.sort((Provider o1, Provider o2) -> Integer.compare(graph.outDegree(o2), graph.outDegree(o1)));
        for (int i = 0; i < _providers.size(); i++) {
            _providers.get(i).setOnlyReputation(i < reputationProvidersNumber);
        }
        return graph;
    }
}
