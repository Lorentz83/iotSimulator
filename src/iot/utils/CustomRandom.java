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

import edu.uci.ics.jung.graph.util.Pair;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author Lorenzo Bossi
 */
public class CustomRandom {

    private final Random _rnd;

    public CustomRandom(Random rnd) {
        _rnd = rnd;
    }

    public int getServiceNumber() {
        return _rnd.nextInt(10) + 1; //todo fixme
    }

    public <T> T randomElement(Set<T> set) {
        int pos = _rnd.nextInt(set.size());
        Iterator<T> it = set.iterator();
        while (pos-- > 0) {
            it.next();
        }
        return it.next();
    }

    public <T> T randomElement(List<T> list) {
        int pos = _rnd.nextInt(list.size());
        return list.get(pos);
    }

    public <T> Set<T> randomSubset(List<T> list, int howMany) {
        if (howMany >= list.size()) {
            throw new IllegalArgumentException("Requested " + howMany + " random elements from a list of " + list.size() + " elements");
        }
        Set<T> retVal = new HashSet<>();
        while (retVal.size() < howMany) {
            retVal.add(randomElement(list));
        }
        return Collections.unmodifiableSet(retVal);
    }

    public <T> Pair<T> getPair(List<T> list) {
        T el1 = randomElement(list);
        T el2;
        do {
            el2 = randomElement(list);
        } while (el1 == el2);
        return new Pair(el1, el2);
    }

    public float getTrustLevel() {
        return _rnd.nextFloat();//todo
    }

}