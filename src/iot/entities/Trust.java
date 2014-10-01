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
public class Trust {

    public Service _service;
    public double _level;

    public Trust(Service service, double level) {
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
