/*
 * Copyright (C) 2014  Lorenzo Bossi
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
package it.uninsubria.iot.entities;

import it.uninsubria.iot.entities.Service;
import it.uninsubria.iot.entities.ServiceFactory;
import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author Lorenzo Bossi
 */
public class ServiceTest {

    @Test
    public void similarityProperties() {

        List<Service> services = new ServiceFactory(5).getServices();
        for (Service s1 : services) {
            assertThat(s1.similarity(s1), is(1.0));

            for (Service s2 : services) {
                System.out.println(String.format("Similarity %s, %s = %s", s1, s2, s1.similarity(s2)));
                assertThat(s1.similarity(s2), greaterThanOrEqualTo(0.0));
                assertThat(s1.similarity(s2), lessThanOrEqualTo(1.0));
                assertThat(s1.similarity(s2), is(s2.similarity(s1)));
            }
        }
        assertThat(services.get(0).similarity(services.get(1)), is(0.6666666666666667));
        assertThat(services.get(0).similarity(services.get(3)), is(0.33333333333333337));
        assertThat(services.get(2).similarity(services.get(3)), is(0.0));
    }

    @Test
    public void distanceProperties() {
        List<Service> services = new ServiceFactory(5).getServices();
        for (Service s1 : services) {
            assertThat(s1.distance(s1), is(0));
            for (Service s2 : services) {
                System.out.println(String.format("Distance %s, %s = %s", s1, s2, s1.distance(s2)));
                assertThat(s1.distance(s2), greaterThanOrEqualTo(0));
                assertThat(s1.distance(s2), is(s2.distance(s1)));
            }
        }
        assertThat(services.get(0).distance(services.get(0)), is(0));
        assertThat(services.get(0).distance(services.get(3)), is(2));
        assertThat(services.get(0).distance(services.get(2)), is(1));
    }
}
