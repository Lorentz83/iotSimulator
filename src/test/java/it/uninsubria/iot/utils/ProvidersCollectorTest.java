package it.uninsubria.iot.utils;

import it.uninsubria.iot.entities.Provider;
import it.uninsubria.iot.entities.Service;
import it.uninsubria.iot.entities.ServiceFactory;
import it.uninsubria.iot.entities.WorkingUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author Lorenzo Bossi
 */
public class ProvidersCollectorTest {

    @Test
    public void iteratorEmpty() {
        List<Service> services = new ServiceFactory(10).getServices();

        Set<Service> workingPlan = new HashSet<Service>();
        workingPlan.add(services.get(3));
        workingPlan.add(services.get(4));

        Set<Service> usefulService = new HashSet<Service>();
        usefulService.add(services.get(1));

        Set<Service> uselessService = new HashSet<Service>();
        uselessService.add(services.get(2));

        ProvidersCollector collector = new ProvidersCollector(workingPlan, 1);
        collector.addProvider(new Provider("p1", usefulService));
        collector.addProvider(new Provider("p2", uselessService));
        collector.addProvider(new Provider("p4", uselessService));

        int counter = 0;

        for (WorkingUnit wu : collector) {
            counter++;
            System.out.println(wu);
        }
        assertThat(counter, is(0));
    }

    @Test
    public void iteratorTest() {
        List<Service> services = new ServiceFactory(10).getServices();

        Set<Service> workingPlan = new HashSet<Service>();
        workingPlan.add(services.get(0));
        workingPlan.add(services.get(1));

        Set<Service> firstService = new HashSet<Service>();
        firstService.add(services.get(0));

        Set<Service> uselessService = new HashSet<Service>();
        uselessService.add(services.get(9));

        ProvidersCollector collector = new ProvidersCollector(workingPlan, 1);
        collector.addProvider(new Provider("p1", firstService));
        collector.addProvider(new Provider("p2", workingPlan));
        collector.addProvider(new Provider("p3", workingPlan));
        collector.addProvider(new Provider("p4", uselessService));

        int counter = 0;
        for (WorkingUnit wu : collector) {
            counter++;
            System.out.println(wu);
        }
        assertThat(counter, is(6));

    }
}
