package iot.utils;

import java.util.Objects;

/**
 *
 * @author Lorenzo <lbossi@purdue.edu>
 */
class Pair<T1, T2> {

    public final T1 first;
    public final T2 second;

    public Pair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public int hashCode() {
        return first.hashCode() + 17 * second.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Pair<?, ?> other = (Pair<?, ?>) obj;
        return Objects.equals(this.first, other.first)
                && Objects.equals(this.second, other.second);
    }

}
