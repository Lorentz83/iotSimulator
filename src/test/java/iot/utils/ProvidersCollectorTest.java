/*
 * Copyright (C) 2014 Lorenzo Bossi
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package iot.utils;

import iot.entities.Provider;
import iot.entities.Service;
import iot.entities.ServiceFactory;
import iot.entities.WorkingUnit;
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
