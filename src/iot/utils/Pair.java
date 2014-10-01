package iot.utils;

/**
 *
 * @author Lorenzo <lbossi@purdue.edu>
 */
class Pair<T1, T2> {

    public final T1 first;
    public final T2 second;
    public int cumulative;

    public Pair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }
}
