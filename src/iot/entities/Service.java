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
 *
 * @author Lorenzo Bossi
 */
public class Service {

    private final int _id;
    private final int _maxServices;
    private final int _high;
    private final String _name;

    protected Service(String name, int id, int max) {
        _name = name;
        _id = id;
        _maxServices = max;
        _high = (int) Math.ceil(Math.log(max) / Math.log(2));
    }

    public String getName() {
        return _name;
    }

    private int distance(Service other) {
        if (_maxServices != other._maxServices) {
            throw new IllegalArgumentException("Cannot compare two services created from two different factories");
        }
        int a = _id;
        int b = other._id;
        int distance = 0;

        while (a != b) {
            if (a > b) {
                a %= 2;
            } else {
                b %= 2;
            }
            distance++;
        }

        return distance;
    }

    public int similarity(Service other) {
        int d = distance(other);
        if (d > _high / 2) {
            return 0;
        }
        return _high - d / _high;
    }
}
