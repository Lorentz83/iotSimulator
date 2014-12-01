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

/**
 * Represents a trust level.
 *
 * @author Lorenzo Bossi
 */
public class Trust {

    public Service _service;
    public double _level;

    /**
     * Initializes the trust.
     *
     * @param service the service considered.
     * @param level the trust level.
     * @throws NullPointerException if the service is null
     * @throws IllegalArgumentException if the trust level is not in [-1,1]
     */
    public Trust(Service service, double level) {
        if (service == null) {
            throw new NullPointerException("service cannot be null");
        }
        if (level < -1 || level > 1) {
            throw new IllegalArgumentException("Trust level must be in [-1,1]");
        }
        _service = service;
        _level = level;
    }

    public Service getService() {
        return _service;
    }

    public double getLevel() {
        return _level;
    }

    public String getLabel() {
        return String.format("%s(%+.3f)", _service.getName(), _level);
    }

    @Override
    public String toString() {
        return getLabel();
    }
}
