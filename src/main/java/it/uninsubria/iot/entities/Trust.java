package it.uninsubria.iot.entities;

/**
 * Represents a trust level.
 *
 * @author Lorenzo Bossi
 */
public class Trust {

    public Service _service;
    public double _level;

    /**
     * Initializes the trust.
     *
     * @param service the service considered.
     * @param level the trust level.
     * @throws NullPointerException if the service is null
     * @throws IllegalArgumentException if the trust level is not in [-1,1]
     */
    public Trust(Service service, double level) {
        if (service == null) {
            throw new NullPointerException("service cannot be null");
        }
        if (level < -1 || level > 1) {
            throw new IllegalArgumentException("Trust level must be in [-1,1]");
        }
        _service = service;
        _level = level;
    }

    public Service getService() {
        return _service;
    }

    public double getLevel() {
        return _level;
    }

    public String getLabel() {
        return String.format("%s(%+.3f)", _service.getName(), _level);
    }

    @Override
    public String toString() {
        return getLabel();
    }
}
