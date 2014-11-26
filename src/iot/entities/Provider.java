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

import java.util.Set;

/**
 *
 * @author Lorenzo Bossi
 */
public class Provider {

    private final String _name;
    private final Set<Service> _services;

    private boolean _onlyReputation;

    public Provider(String name, Set<Service> services) {
        _name = name;
        _onlyReputation = false;
        _services = services;
    }

    public String getName() {
        return _name;
    }

    public String getFullName() {
        return String.format("%s%s", _name, _onlyReputation ? "R" : "S");
    }

    public void setOnlyReputation(boolean providesOnlyRep) {
        _onlyReputation = providesOnlyRep;
    }

    public boolean isOnlyReputation() {
        return _onlyReputation;
    }

    public Set<Service> getServices() {
        return _services;
    }

    @Override
    public String toString() {
        return _name;
    }

    /**
     * Check if this provider provides the required service. Returns the maximum
     * similarity of the provided services from this provider compared to the
     * required service.
     *
     * @param service the required service.
     * @return 0 if this provider does not provide anything similar to the
     * required service, 1 if provides exactly the required service, a fraction
     * if provides some similar service.
     */
    public double provide(Service service) {
        double sim = 0;
        for (Service s : _services) {
            sim = Math.max(s.similarity(service), sim);
        }
        return sim;
    }
}
