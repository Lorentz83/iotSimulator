package it.uninsubria.iot.utils;

import it.uninsubria.iot.entities.Provider;
import it.uninsubria.iot.entities.Service;
import it.uninsubria.iot.entities.WorkingUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Produces a set of working units from a set of providers and a working plan.
 * The working units are accessed through the iterator.
 *
 * @author Lorenzo Bossi
 */
public class ProvidersCollector implements Iterable<WorkingUnit> {

    private final Map<Service, List<Provider>> _providersPerService;
    private final double _minimumSimilarity;
    private final Set<Service> _workingPlan;

    /**
     * Constructs the object.
     *
     * @param workingPlan the required working plan.
     * @param minimumSimilarity the minimum similarity required to select a
     * provider for a service.
     * @throws IllegalArgumentException if the similarity is not in the range
     * (0,1] or the working plan contains less than two services.
     */
    public ProvidersCollector(Set<Service> workingPlan, double minimumSimilarity) throws IllegalArgumentException {
        if (minimumSimilarity <= 0 || minimumSimilarity > 1) {
            throw new IllegalArgumentException("minimumSimilarity must be in (0,1]");
        }
        if (workingPlan.size() < 2) {
            throw new IllegalArgumentException("The working plan have to contain at least two services");
        }

        _workingPlan = workingPlan;
        _providersPerService = new HashMap<>();
        for (Service service : workingPlan) {
            _providersPerService.put(service, new ArrayList<>());
        }
        _minimumSimilarity = minimumSimilarity;
    }

    /**
     * Collects the provider if it is useful for the current set up (working
     * unit and minimum similarity). Only the service providers are collected,
     * providers which provide only reputation are silently discarded.
     *
     * @param provider the provider.
     */
    public void addProvider(Provider provider) {
        if (provider.isOnlyReputation()) {
            return;
        }
        for (Service required : _workingPlan) {
            List<Provider> listOfProviders = _providersPerService.get(required);
            if (provider.provide(required) >= _minimumSimilarity) {
                listOfProviders.add(provider);
            }
        }
    }

    @Override
    public Iterator<WorkingUnit> iterator() {
        return new WorkingUnitIterator(_providersPerService);
    }

}

class WorkingUnitIterator implements Iterator<WorkingUnit> {

    private final HashMap<Service, Integer> _max;
    private final Counter<Service> _counter;
    private final List<Service> _workingPlan;
    private boolean _empty;
    private final Map<Service, List<Provider>> _providersPerService;
    private int _serviceToInc;

    WorkingUnitIterator(Map<Service, List<Provider>> providersPerService) {
        _max = new HashMap<>();
        _providersPerService = providersPerService;
        _workingPlan = new ArrayList<>(providersPerService.keySet()); //the set has no order, we must rely on this iterator
        _empty = false;
        _counter = new Counter<>();
        for (Map.Entry<Service, List<Provider>> pair : providersPerService.entrySet()) {
            Service service = pair.getKey();
            int size = pair.getValue().size();
            if (size == 0) {
                _empty = true;
            }
            _max.put(service, size);
        }
    }

    @Override
    public boolean hasNext() {
        return !_empty;
    }

    @Override
    public WorkingUnit next() {
        WorkingUnit wu = new WorkingUnit();
        if (_empty) {
            throw new NoSuchElementException();
        }

        _serviceToInc = 0;
        for (int i = 0; i < _workingPlan.size(); i++) {
            Service service = _workingPlan.get(i);
            int pos = _counter.get(service);
            List<Provider> providersList = _providersPerService.get(service);
            wu.add(providersList.get(pos), service);

            if (_serviceToInc == i) {
                if (pos < providersList.size() - 1) {
                    _counter.add(service);
                } else {
                    _counter.reset(service);
                    _serviceToInc++;
                }
            }
        }
        _empty = _serviceToInc == _workingPlan.size();
        return wu;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported");
    }
}
