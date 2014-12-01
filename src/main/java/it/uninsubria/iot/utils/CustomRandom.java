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
package it.uninsubria.iot.utils;

import edu.uci.ics.jung.graph.util.Pair;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomDataGenerator;

/**
 * Customized pseudo random number generator.
 *
 * @author Lorenzo Bossi
 */
public class CustomRandom {

    private final JDKRandomGenerator _rnd;
    private final RandomDataGenerator _rndData;

    /**
     * Initializes the class using a standard random number generator.
     *
     * @param rnd the random number generator.
     */
    public CustomRandom(JDKRandomGenerator rnd) {
        _rnd = rnd;
        _rndData = new RandomDataGenerator(_rnd);
    }

    /**
     * Selects an element randomly from a collection
     *
     * @param <T>
     * @param set the collection with the elements.
     * @return a random element.
     */
    public <T> T randomElement(Collection<T> set) {
        int pos = _rnd.nextInt(set.size());
        Iterator<T> it = set.iterator();
        while (pos-- > 0) {
            it.next();
        }
        return it.next();
    }

    /**
     * Selects an element randomly from a list
     *
     * @param <T>
     * @param list the list with the elements.
     * @return a random element.
     */
    public <T> T randomElement(List<T> list) {
        int pos = _rnd.nextInt(list.size());
        return list.get(pos);
    }

    /**
     * Returns a subset with random elements.
     *
     * @param <T>
     * @param list the list to select the elements.
     * @param howMany the size of the set to return.
     * @return an unmodifiable set of random elements.
     * @throws IllegalArgumentException if are required more elements than the
     * one available in the list.
     */
    public <T> Set<T> randomSubset(List<T> list, int howMany) {
        if (howMany > list.size()) {
            throw new IllegalArgumentException("Requested " + howMany + " random elements from a list of " + list.size() + " elements");
        }
        Set<T> retVal = new HashSet<>();
        while (retVal.size() < howMany) {
            retVal.add(randomElement(list));
        }
        return Collections.unmodifiableSet(retVal);
    }

    /**
     * Returns a random pair of different elements selected from the list.
     *
     * @param <T>
     * @param list the list to select the elements.
     * @return a random pair of different elements.
     * @throws IllegalArgumentException if the list contains less than 2
     * elements.
     */
    public <T> Pair<T> getPair(List<T> list) {
        if (list.size() < 2) {
            throw new IllegalArgumentException("The list contains less than 2 elements");
        }
        T el1 = randomElement(list);
        T el2;
        do {
            el2 = randomElement(list);
        } while (el1 == el2);
        return new Pair<>(el1, el2);
    }

    /**
     * Returns a trust level. The value is randomly computed using a beta
     * distribution.
     *
     * @return a value in [-1, 1]
     */
    public double getTrustLevel() {
        return _rndData.nextBeta(19, 7) * 2 - 1;
    }

    /**
     * Returns a number of services. The value is randomly computed using a
     * geometric distribution. In case the distribution would result in a higher
     * result the algorithm is reiterated instead of clipping the value (because
     * this would create a bias to the max value).
     *
     * @param max the maximum number of services.
     * @return a value in [1, max].
     */
    public int getServiceNumber(int max) { //geometric(.75) ?
        int res = 1;
        while (_rnd.nextDouble() < .75) {
            res++;
        }
        return (res > max) ? getServiceNumber(max) : res;
    }

    /**
     * Returns a random integer uniformly distributed between 0 (inclusive) and
     * the specified value (exclusive).
     *
     * @param bound the limit.
     * @return a random integer in [0,bound)
     */
    int nextInt(int bound) {
        return _rnd.nextInt(bound);
    }
}
