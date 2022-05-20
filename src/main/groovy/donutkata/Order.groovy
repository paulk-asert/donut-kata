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

import java.time.LocalDate

import org.eclipse.collections.api.bag.MutableBag
import org.eclipse.collections.api.tuple.primitive.ObjectIntPair
import org.eclipse.collections.impl.factory.Bags
import org.eclipse.collections.impl.list.fixed.ArrayAdapter
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples

record Order(Customer customer, LocalDate date, MutableBag<DonutType> counts) {
    Order(Customer customer, LocalDate date, String donutTypeCounts) {
        this(customer, date, Bags.mutable.<DonutType>empty())
        ArrayAdapter.adapt(donutTypeCounts.split(","))
                .asLazy()
                .collect(pair -> pair.split(":"))
                .collect(pair -> PrimitiveTuples.pair(DonutType.forAbbreviation(pair[0]), pair[1] as Integer))
                .each( pair -> add(pair))
    }

    private void add(ObjectIntPair<DonutType> pair) {
        counts.addOccurrences(pair.one, pair.two)
    }

    @Override
    int hashCode() {
        System.identityHashCode(this)
    }

    String toString() {
        "Order(customer=$customer, date=$date, counts=${counts.toStringOfItemToCount()})"
    }
}
