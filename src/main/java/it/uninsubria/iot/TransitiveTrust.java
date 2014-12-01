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
package it.uninsubria.iot;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import it.uninsubria.iot.entities.Provider;
import it.uninsubria.iot.entities.Service;
import it.uninsubria.iot.entities.Trust;
import it.uninsubria.iot.entities.WorkingUnit;
import it.uninsubria.iot.utils.Average;
import it.uninsubria.iot.utils.Pair;
import it.uninsubria.iot.utils.ProvidersCollector;
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
     * @param minSimilarity the minimum similarity required to select a service.
     * @return a pair with the best working unit found and the overall unit
     * reputation or null if no working unit is be found.
     * @throws IllegalArgumentException if depth is less or equal than 1 or
     * minSimilarity is not in (0,1].
     */
    public Pair<WorkingUnit, Double> getWorkingUnit(Provider customer, Set<Service> workingPlan, int depth, double minSimilarity) {
        if (depth <= 1) {
            throw new IllegalArgumentException("depth must be greather than 1");
        }
        if (minSimilarity > 1 || minSimilarity <= 0) {
            throw new IllegalArgumentException("minSimilarity must be in (0,1]");
        }
        ProvidersCollector collector = new ProvidersCollector(workingPlan, minSimilarity);
        Set<Provider> visited = new HashSet<>();
        collectProviders(customer, collector, visited, depth);

        WorkingUnit bestWorkingUnit = null;
        double bestReputation = Double.MIN_VALUE;
        for (WorkingUnit wu : collector) {
            double reputation = calculateOverallReputation(wu);
            if (reputation > bestReputation) {
                bestReputation = reputation;
                bestWorkingUnit = wu;
            }
        }

        if (bestWorkingUnit == null) {
            return null;
        }
        return new Pair<>(bestWorkingUnit, bestReputation);
    }

    private void collectProviders(Provider start, ProvidersCollector collector, Set<Provider> visited, int depth) {
        if (!visited.add(start)) {
            return;
        }
        collector.addProvider(start);
        if (depth == 0) {
            return;
        }
        Collection<Provider> successors = _graph.getSuccessors(start);
        for (Provider next : successors) {
            collectProviders(next, collector, visited, depth - 1);
        }
    }

    /**
     * Calculates the overall reputation for the working unit.
     *
     * @param workingUnit the working unit
     * @return the overall reputation, as the average of the reputation of all
     * the member.
     * @throws IllegalArgumentException if the working unit contains less than
     * two service provider.
     */
    public double calculateOverallReputation(WorkingUnit workingUnit) {
        Average avg = new Average();
        Set<Provider> providers = workingUnit.getProviders();
        if (providers.size() <= 1) {
            throw new IllegalArgumentException("The working unit contains less than two providers");
        }
        for (Provider source : providers) {
            for (Map.Entry<Provider, List<Service>> sp : workingUnit.getServiceListPerProvider()) {
                Provider dest = sp.getKey();
                if (!source.equals(dest)) {
                    for (Service service : sp.getValue()) {
                        double rep = getReputation(source, dest, service);
                        avg.add(rep);
                    }
                }
            }
        }
        return avg.getAvg();
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
