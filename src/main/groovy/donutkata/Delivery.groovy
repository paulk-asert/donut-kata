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

import java.time.LocalDate

import org.eclipse.collections.api.list.ImmutableList

record Delivery(Order order, ImmutableList<Donut> donuts) {
    Delivery {
        order.customer().addDelivery(this)
    }

    Customer getCustomer() {
        order.customer()
    }

    LocalDate getDate() {
        order.date()
    }

    boolean deliveredOn(LocalDate date) {
        getDate() == date
    }

    int getTotalDonuts() {
        donuts.size()
    }

    @CompileDynamic
    Number getTotalPrice() {
        donuts.sum(Donut::price)
    }

    @Override
    String toString() {
        "Delivery(order=$order, donuts=${donuts.toBag().toStringOfItemToCount()})"
    }
}
