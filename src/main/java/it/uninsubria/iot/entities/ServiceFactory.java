package it.uninsubria.iot.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Creates a list of services. The services are logically organized in a binary
 * tree that is used to define the similarity function.
 *
 * @author Lorenzo Bossi
 */
public class ServiceFactory {

    private final List<Service> _services;

    public ServiceFactory(int howMany) {
        ArrayList<Service> services = new ArrayList<>(howMany);
        for (int i = 1; i <= howMany; i++) {
            services.add(new Service(String.format("S%02d", i), i, howMany));
        }
        _services = Collections.unmodifiableList(services);
    }

    public List<Service> getServices() {
        return _services;
    }

    public Service getRandomService(Random r) {
        return _services.get(r.nextInt(_services.size()));
    }
}
