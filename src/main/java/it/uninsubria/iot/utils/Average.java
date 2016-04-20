package it.uninsubria.iot.utils;

/**
 * Computes the average.
 *
 * @author Lorenzo Bossi
 */
public class Average {

    private double _sum = 0;
    private int _num = 0;

    public void add(double val) {
        _sum += val;
        _num++;
    }

    public double getAvg() {
        return _sum / _num;
    }
}
