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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represent a Map between keys and a list of values.
 *
 * @author Lorenzo Bossi
 * @param <K> the type of the key.
 * @param <V> the type of the values.
 */
public class MapList<K, V> implements Iterable<Map.Entry<K, List<V>>> {

    private final Map<K, List<V>> _container = new HashMap<>();

    /**
     * Returns the unmodifiable list associated to the key. If nothing was added
     * to the key, returns an empty list.
     *
     * @param key
     * @return the unmodifiable list.
     */
    public List<V> get(K key) {
        List<V> ret = _container.get(key);
        if (ret == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(ret);
    }

    /**
     * Append the value to the list indexed by the key.
     *
     * @param key
     * @param value
     */
    public void append(K key, V value) {
        List<V> ret = _container.get(key);
        if (ret == null) {
            ret = new ArrayList<>();
            _container.put(key, ret);
        }
        ret.add(value);
    }

    /**
     * Returns a read only iterator. The List cannot be modified if accessed by
     * this iterator.
     *
     * @return a read only iterator.
     */
    @Override
    public Iterator<Map.Entry<K, List<V>>> iterator() {
        return new UnmodifiableIterator(_container.entrySet().iterator());
    }

    /**
     * Returns a read only set of keys.
     *
     * @return
     */
    public Set<K> keySet() {
        return Collections.unmodifiableSet(_container.keySet());
    }

}

class UnmodifiableIterator<K, V> implements Iterator<Map.Entry<K, List<V>>> {

    private final Iterator<Map.Entry<K, List<V>>> _realIterator;

    public UnmodifiableIterator(Iterator<Map.Entry<K, List<V>>> iterator) {
        _realIterator = iterator;
    }

    @Override
    public boolean hasNext() {
        return _realIterator.hasNext();
    }

    @Override
    public Map.Entry<K, List<V>> next() {
        return new UnmodifiableEntry(_realIterator.next());
    }

    private class UnmodifiableEntry implements Map.Entry<K, List<V>> {

        private final Map.Entry<K, List<V>> _realEntry;

        private UnmodifiableEntry(Map.Entry<K, List<V>> entry) {
            _realEntry = entry;
        }

        @Override
        public K getKey() {
            return _realEntry.getKey();
        }

        @Override
        public List<V> getValue() {
            return Collections.unmodifiableList(_realEntry.getValue());
        }

        @Override
        public List<V> setValue(List<V> value) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }
}
