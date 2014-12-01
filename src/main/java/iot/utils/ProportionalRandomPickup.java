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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Randomly pick up an element with probability proportionally to a scale.
 *
 * @author Lorenzo Bossi
 */
public class ProportionalRandomPickup<T> {

    private final List<Pair<T, Integer>> _elements;
    private final CustomRandom _rnd;
    private final int _cumulative;

    /**
     * Set up the class.
     *
     * @param rnd the random number generator.
     * @param elements the elements list from which a random element will be
     * selected.
     * @param proportion the function that associates for each element the value
     * that will define the probability to being selected.
     */
    public ProportionalRandomPickup(CustomRandom rnd, List<T> elements, Function<T, Integer> proportion) {
        _rnd = rnd;
        _elements = new ArrayList<>(elements.size());
        int cumulative = 0;
        for (T el : elements) {
            cumulative += proportion.apply(el);
            _elements.add(new Pair<>(el, cumulative));
        }
        _cumulative = cumulative;
    }

    /**
     * Returns the next random element.
     *
     * @return the next random element.
     */
    public T getNext() {
        int r = _rnd.nextInt(_cumulative);
        T last = _elements.get(0).first;

        for (Pair<T, Integer> c : _elements) {
            if (c.second > r) {
                return last;
            }
            last = c.first;
        }
        return last; //tmch?
    }

}