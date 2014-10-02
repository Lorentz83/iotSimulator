package iot.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Lorenzo <lbossi@purdue.edu>
 */
public class Counter<T> {

    private final Map<T, Integer> _counter = new HashMap<>();
    private int _max = 0;

    public void add(T val) {
        Integer c = _counter.get(val);
        if (c == null) {
            c = 0;
        }
        c++;
        _max = Math.max(c, _max);
        _counter.put(val, c);
    }

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
