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

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a working unit. A working unit is defined as a set of providers
 * each required by a service.
 *
 * @author Lorenzo Bossi
 */
public class WorkingUnit {

    private Map<Service, Provider> _components = new HashMap<>();

    public void add(Service s, Provider p) {
        if (_components.containsKey(s)) {
            throw new IllegalStateException("The serice " + s + "is already present in the working unit");
        }
        _components.put(s, p);
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

}
