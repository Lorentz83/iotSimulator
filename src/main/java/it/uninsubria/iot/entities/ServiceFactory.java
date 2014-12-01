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
package it.uninsubria.iot.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Creates a list of services. The services are logically organized in a binary
 * tree that is used to define the similarity function.
 *
 * @author Lorenzo Bossi
 */
public class ServiceFactory {

    private final List<Service> _services;

    public ServiceFactory(int howMany) {
        ArrayList<Service> services = new ArrayList<>(howMany);
        for (int i = 1; i <= howMany; i++) {
            services.add(new Service(String.format("S%02d", i), i, howMany));
        }
        _services = Collections.unmodifiableList(services);
    }

    public List<Service> getServices() {
        return _services;
    }

    public Service getRandomService(Random r) {
        return _services.get(r.nextInt(_services.size()));
    }
}