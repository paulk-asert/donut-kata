/*
 * Copyright 2022 The original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package donutkata

import groovy.transform.CompileDynamic
import groovy.transform.ToString
import org.eclipse.collections.api.list.ListIterable
import org.eclipse.collections.api.list.MutableList
import org.eclipse.collections.api.set.SetIterable
import org.eclipse.collections.impl.factory.Lists

@ToString(includeNames = true, includes='name')
record Customer(String name, MutableList<Delivery> deliveries = Lists.mutable.empty()) {

    boolean named(String name) { name == this.name }

    void addDelivery(Delivery delivery) { deliveries.add(delivery) }

    ListIterable<Delivery> getDeliveries() {
        deliveries.asUnmodifiable()
    }

    long getTotalDonutsOrdered() {
        deliveries.sumOfInt(Delivery::getTotalDonuts)
    }

    @CompileDynamic
    SetIterable<DonutType> getDonutTypesOrdered() {
        deliveries.flatCollect(Delivery::donuts)
                .collect(Donut::type)
                .toSet()
    }
}
