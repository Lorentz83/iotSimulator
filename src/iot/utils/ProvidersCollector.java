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
package iot.utils;

import iot.entities.Provider;
import iot.entities.Service;
import iot.entities.WorkingUnit;
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
     * (0,1].
     */
    public ProvidersCollector(Set<Service> workingPlan, double minimumSimilarity) throws IllegalArgumentException {
        if (minimumSimilarity <= 0 || minimumSimilarity > 1) {
            throw new IllegalArgumentException("minimumSimilarity must be in (0,1]");
        }
        _workingPlan = workingPlan;
        _providersPerService = new HashMap<>();
        for (Service service : workingPlan) {
            _providersPerService.put(service, new ArrayList<>());
        }
        _minimumSimilarity = minimumSimilarity;
    }

    /**
     * Collects the available providers.
     *
     * @param provider the provider.
     */
    public void addProvider(Provider provider) {
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
        _workingPlan = new ArrayList(providersPerService.keySet()); //the set has no order, we must rely on this iterator
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
            wu.add(service, providersList.get(pos));

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
}
