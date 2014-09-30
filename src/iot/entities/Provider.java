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

    private final boolean _onlyReputation;

    public Provider(String name, boolean onlyReputation, Set<Service> services) {
        _name = name;
        _onlyReputation = onlyReputation;
        _services = services;
    }

    public String getName() {
        return _name;
    }

    public boolean isOnlyReputation() {
        return _onlyReputation;
    }

    public Set<Service> getServices() {
        return _services;
    }

    public int provide(Service service) {
        int sim = 0;
        for (Service s : _services) {
            sim = Math.max(s.similarity(service), sim);
        }
        return sim;
    }
}
