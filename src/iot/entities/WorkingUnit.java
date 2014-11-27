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
package iot.entities;

import iot.utils.MapList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a working unit. A working unit is defined as a set of providers
 * each required by a service.
 *
 * @author Lorenzo Bossi
 */
public class WorkingUnit {

    private final Map<Service, Provider> _components = new HashMap<>();
    private final MapList<Provider, Service> _providers = new MapList();

    /**
     * Add the provider to the working unit.
     *
     * @param provider the provider to add.
     * @param service the service that is providing.
     * @throws IllegalStateException if there is already a provider for the
     * specified service.
     * @throws IllegalArgumentException if the provider provides only
     * reputation.
     * @throws NullPointerException if the provider or the service is null.
     */
    public void add(Provider provider, Service service) {
        if (_components.containsKey(service)) {
            throw new IllegalStateException("The serice " + service + "is already present in the working unit");
        }
        if (provider.isOnlyReputation()) {
            throw new IllegalArgumentException();
        }
        if (provider == null || service == null) {
            throw new NullPointerException();
        }
        _components.put(service, provider);
        _providers.append(provider, service);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("-- Working unit --\n");
        for (Map.Entry<Service, Provider> entrySet : _components.entrySet()) {
            Service key = entrySet.getKey();
            Provider value = entrySet.getValue();
            sb.append(String.format("Service %s -> Provider %s;\n", key, value));
        }
        sb.append("------------------\n");
        return sb.toString();
    }

    /**
     * Returns a readonly iterator.
     *
     * @return
     */
    public Iterable<Map.Entry<Service, Provider>> getProviderPerService() {
        return Collections.unmodifiableSet(_components.entrySet());
    }

    /**
     * Returns a readonly iterator.
     *
     * @return
     */
    public Iterable<Map.Entry<Provider, List<Service>>> getServiceListPerProvider() {
        return _providers;
    }

    /**
     * Returns the set of providers.
     *
     * @return a readonly set.
     */
    public Set<Provider> getProviders() {
        return _providers.keySet();
    }
}
