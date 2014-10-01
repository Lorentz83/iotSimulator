package iot.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 *
 * @author Lorenzo <lbossi@purdue.edu>
 */
public class ProportionalRandomPickup<T> {

    private final List<Pair<T, Integer>> _elements;
    private final CustomRandom _rnd;
    private final int _cumulative;

    public ProportionalRandomPickup(CustomRandom rnd, List<T> elements, Function<T, Integer> proportion) {
        _rnd = rnd;
        _elements = new ArrayList<>(elements.size());
        int cumulative = 0;
        for (T el : elements) {
            cumulative += proportion.apply(el);
            _elements.add(new Pair(el, cumulative));
        }
        _cumulative = cumulative;
    }

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
