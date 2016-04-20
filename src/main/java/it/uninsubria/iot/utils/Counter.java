package it.uninsubria.iot.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple class to count objects.
 *
 * @author Lorenzo Bossi
 * @param <T> the type of object to count.
 */
public class Counter<T> {

    private final Map<T, Integer> _counter = new HashMap<>();
    private int _max = 0;

    /**
     * Increments the counter associated to the value.
     *
     * @param val the object to count.
     */
    public void add(T val) {
        Integer c = _counter.get(val);
        if (c == null) {
            c = 0;
        }
        c++;
        _max = Math.max(c, _max);
        _counter.put(val, c);
    }

    /**
     * Reset to 0 the value associated to val.
     *
     * @param val the object to reset the counter.
     */
    void reset(T val) {
        _counter.put(val, 0); // TODO: this breaks _max
    }

    /**
     * Returns the counter associated to the value.
     *
     * @param val the value to count.
     * @return the associated counter.
     */
    public int get(T val) {
        Integer c = _counter.get(val);
        return (c == null) ? 0 : c;
    }

    /**
     * Pretty formats the collected values in an ascii table.
     *
     * @return a formatted string with the values in a table.
     */
    public String toTable() {
        StringBuilder sb = new StringBuilder();

        ArrayList<Map.Entry<T, Integer>> sorted = new ArrayList<>(_counter.size());
        sorted.addAll(_counter.entrySet());
        sorted.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

        sb.append("   # | value\n");
        sb.append("-----+----- \n");
        for (Map.Entry<T, Integer> entrySet : sorted) {
            T val = entrySet.getKey();
            Integer num = entrySet.getValue();
            sb.append(String.format("%4d | %s\n", num, val));
        }
        return sb.toString();
    }

}
