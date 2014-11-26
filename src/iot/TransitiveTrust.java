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

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import iot.entities.Provider;
import iot.entities.Service;
import iot.entities.Trust;
import iot.entities.WorkingUnit;
import iot.utils.Pair;
import iot.utils.ProvidersCollector;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Calculates the transitive closure of trust.
 *
 * @author Lorenzo Bossi
 */
public class TransitiveTrust {

    private final DirectedSparseMultigraph<Provider, Trust> _graph;
    private final Map<Service, DijkstraShortestPath<Provider, Trust>> shortestPath;

    /**
     *
     * @param graph the graph to use.
     * @param services the allowed list of services.
     */
    public TransitiveTrust(DirectedSparseMultigraph<Provider, Trust> graph, List<Service> services) {
        _graph = graph;
        shortestPath = new HashMap<>();
        for (Service service : services) {
            //Since results are cached and the weight depends on the target service, we
            //need a class for each Service we are looking for.
            shortestPath.put(service, new DijkstraShortestPath<>(graph, new Weight(service), true));
        }
    }

    /**
     * Get the reputation (transitive closure of trust).
     *
     * @param from the starting node (the observer).
     * @param to the node to compute the reputation.
     * @param service the service we are considering.
     * @return the reputation [-1,1];
     */
    public double getReputation(Provider from, Provider to, Service service) {
        List<Trust> path = shortestPath.get(service).getPath(from, to);
        if (path == null || path.isEmpty()) {
            return 0;
        }
        double reputation = Double.MAX_VALUE;

        for (Trust edge : path) {
            reputation = Math.min(reputation, service.similarity(edge.getService()) * edge.getLevel());
        }
        return reputation;
    }

    /**
     * Select the best working unit to fulfill the given working set.
     *
     * @param customer the node that starts the search.
     * @param workingPlan the required services.
     * @param depth how far search.
     * @return a pair with the best working unit found and the overall unit
     * reputation.
     */
    public Pair<WorkingUnit, Double> getWorkingUnit(Provider customer, Set<Service> workingPlan, int depth, double minSimilarity) {
        ProvidersCollector collector = new ProvidersCollector(workingPlan, minSimilarity);
        Set<Provider> visited = new HashSet<>();

        collectProviders(customer, collector, visited, depth);
        throw new UnsupportedOperationException("not yet implemented");
        // todo the collector must check if is a service provider
    }

    private void collectProviders(Provider start, ProvidersCollector collector, Set<Provider> visited, int depth) {
        if (depth == 0) {
            visited.add(start);
            collector.addProvider(start);
            return;
        }
        Collection<Provider> succ = _graph.getSuccessors(start);
        for (Provider p : succ) {

            collectProviders(p, collector, visited, depth - 1);
        }
        throw new UnsupportedOperationException("not yet implemented");
    }

}

/**
 * Converts edges into weights to calculate the shortest path.
 *
 * @author Lorenzo Bossi
 */
class Weight implements org.apache.commons.collections15.Transformer<Trust, Number> {

    private final Service _currentService;

    /**
     *
     * @param service the service we are considering to calculate the weight.
     */
    Weight(Service service) {
        _currentService = service;
    }

    /**
     * Calculates the weight of an edge. According to the service passed to the
     * constructor.
     *
     * @param edge the edge to weight.
     * @return the weight > 0;
     */
    @Override
    public Number transform(Trust edge) {
        //w_s(e)= 1 / [ l(s, s(e)) · (2 + r(e)) ]
        double den = edge.getService().similarity(_currentService) * (2 + edge.getLevel());
        return 1 / den;
    }

}
