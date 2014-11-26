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

/**
 * Represents a service.
 *
 * @author Lorenzo Bossi
 */
public class Service {

    private final int _id;
    private final int _maxServices;
    private final int _height;
    private final String _name;

    protected Service(String name, int id, int max) {
        _name = name;
        _id = id;
        _maxServices = max;
        _height = (int) Math.ceil(Math.log(max) / Math.log(2));
    }

    public String getName() {
        return _name;
    }

    @Override
    public String toString() {
        return _name;
    }

    /**
     * Returns how far are the services counting the minimum number of hops in
     * the service tree.
     *
     * @param other the service to compare.
     * @return the distance of the service. This number is 0 if the services are
     * the same, greater than 0 otherwise.
     */
    protected int distance(Service other) {
        if (_maxServices != other._maxServices) {
            throw new IllegalArgumentException("Cannot compare two services created from two different factories");
        }
        int a = _id;
        int b = other._id;
        int distance = 0;

        while (a != b) {
            if (a > b) {
                a /= 2;
            } else {
                b /= 2;
            }
            distance++;
        }

        return distance;
    }

    /**
     * Returns the similarity of the services. The similarity is defined as
     * min(0 , 1 - distance / height), where distance is the minimum number of
     * edges that separates the services in the service tree and height is the
     * height of the services tree.
     *
     * @param other the service to compare.
     * @return the similarity [0,1].
     */
    public double similarity(Service other) {
        double d = distance(other);
        double sim = 1 - d / _height;
        return sim < 0 ? 0 : sim;
    }
}
