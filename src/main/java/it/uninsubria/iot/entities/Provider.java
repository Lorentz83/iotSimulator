package it.uninsubria.iot.entities;

import java.io.Serializable;
import java.util.Set;

/**
 * Representation of a Provider. Two providers are equals if their references
 * are equals.
 *
 * @author Lorenzo Bossi
 */
public class Provider implements Serializable {

    private final String _name;
    private final Set<Service> _services;

    private boolean _onlyReputation;

    /**
     * Initializes the class.
     *
     * @param name the name of the provider.
     * @param services the set of services.
     * @throws NullPointerException if the name is null
     * @throws IllegalArgumentException if the services set contains less than 1
     * service.
     */
    public Provider(String name, Set<Service> services) {
        if (name == null) {
            throw new NullPointerException("Missing provider name");
        }
        if (services.size() < 1) {
            throw new IllegalArgumentException("At least 1 service must be provided");
        }
        _name = name;
        _onlyReputation = false;
        _services = services;
    }

    public String getName() {
        return _name;
    }

    /**
     * Append to the name R if provides only reputation S if provides both.
     *
     * @return a decorated name.
     */
    public String getFullName() {
        return String.format("%s%s", _name, _onlyReputation ? "R" : "S");
    }

    public void setOnlyReputation(boolean providesOnlyRep) {
        _onlyReputation = providesOnlyRep;
    }

    public boolean isOnlyReputation() {
        return _onlyReputation;
    }

    public Set<Service> getServices() {
        return _services;
    }

    @Override
    public String toString() {
        return _name;
    }

    /**
     * Checks if this provider provides the required service. Returns the
     * maximum similarity of the provided services from this provider compared
     * to the required service.
     *
     * @param service the required service.
     * @return 0 if this provider does not provide anything similar to the
     * required service, 1 if provides exactly the required service, a fraction
     * if provides some similar service.
     */
    public double provide(Service service) {
        double sim = 0;
        for (Service s : _services) {
            sim = Math.max(s.similarity(service), sim);
        }
        return sim;
    }
}
