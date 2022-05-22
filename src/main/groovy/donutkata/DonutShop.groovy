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

import org.eclipse.collections.api.bag.Bag
import org.eclipse.collections.api.bag.MutableBag
import org.eclipse.collections.api.list.ImmutableList
import org.eclipse.collections.api.list.MutableList
import org.eclipse.collections.api.multimap.Multimap
import org.eclipse.collections.api.set.MutableSet
import org.eclipse.collections.api.tuple.Pair
import org.eclipse.collections.api.tuple.primitive.ObjectIntPair
import org.eclipse.collections.impl.factory.Bags
import org.eclipse.collections.impl.factory.Lists
import org.eclipse.collections.impl.factory.Sets
import org.eclipse.collections.impl.list.primitive.IntInterval
import org.eclipse.collections.impl.tuple.Tuples

import java.time.LocalDate

/**
 * A DonutShop has a Bag of DonutTypes, a Set of Customers, a List of Orders, and a List of Deliveries.
 * Prices for donuts are determined by how many donuts are ordered.  Deliveries are always made the same
 * day as an Order.  If there are not enough donuts to fill an order, more donuts are made.
 */
class DonutShop {
    private static final int SINGLE = 1
    private static final int DOUBLE = 2
    private static final int HALF_DOZEN = 6
    private static final int DOZEN = 12
    private static final int BAKERS_DOZEN = 13
    private static final ImmutableList<Pair<IntInterval, Number>> PRICES =
            Lists.immutable.with(
                    Tuples.pair(IntInterval.zeroTo(SINGLE), 1.50),
                    Tuples.pair(IntInterval.fromTo(DOUBLE, HALF_DOZEN - 1), 1.35),
                    Tuples.pair(IntInterval.fromTo(HALF_DOZEN, DOZEN - 1), 1.25),
                    Tuples.pair(IntInterval.fromTo(DOZEN, DOZEN), 1.00),
                    Tuples.pair(IntInterval.fromTo(BAKERS_DOZEN, DOZEN * 100), 0.95))

    private MutableBag<DonutType> donuts = Bags.mutable.empty()
    private MutableList<Order> orders = Lists.mutable.empty()
    private MutableSet<Customer> customers = Sets.mutable.empty()
    private MutableList<Delivery> deliveries = Lists.mutable.empty()

    void makeDonuts(DonutType type, int count) {
        donuts.addOccurrences(type, count)
    }

    private void makeMissingDonuts(DonutType type, int count) {
        int inventory = donuts.occurrencesOf(type)
        if (inventory < count) {
            int missing = count - inventory
            makeDonuts(type, missing)
        }
    }

    Delivery deliverOrder(String customerName, LocalDate date, String donutTypeCounts) {
        var customer = getOrCreateCustomer(customerName)
        var order = createOrder(customer, date, donutTypeCounts)
        fillOrder(order)
    }

    private Delivery fillOrder(Order order) {
        order.counts().forEachWithOccurrences(this::makeMissingDonuts)
        var price = calculatePricePerDonut(order.counts().size())
        var delivery = createDelivery(order, price)
        order.counts().forEachWithOccurrences(donuts::removeOccurrences)
        return delivery
    }

    private Delivery createDelivery(Order order, Number price) {
        var donutList = order.counts().asLazy().collect(type -> new Donut(type, price)).toImmutableList()
        var delivery = new Delivery(order, donutList)
        deliveries.add(delivery)
        return delivery
    }

    private Number calculatePricePerDonut(int orderSize) {
        return PRICES.detectIfNone(
                pair -> pair.one.contains(orderSize),
                () -> { throw new IllegalArgumentException("This order cannot be satisfied"); })
                .two
    }

    private Order createOrder(Customer customer, LocalDate date, String donutTypeCounts) {
        var order = new Order(customer, date, donutTypeCounts)
        orders.add(order)
        order
    }

    private Customer getOrCreateCustomer(String customerName) {
        var customer = customers.detectWithIfNone(
                Customer::named, customerName,
                () -> new Customer(customerName))
        if (customer !in customers) {
            customers.add(customer)
        }
        customer
    }

    Bag<DonutType> getDonuts() {
        donuts.asUnmodifiable()
    }

    MutableList<ObjectIntPair<DonutType>> getTopDonuts(int n) {
        deliveries
                .flatCollect(delivery -> delivery.donuts)
                .countBy(donut -> donut.type)
                .topOccurrences(n)
    }

    Number getTotalDeliveryValueFor(LocalDate date) {
        deliveries
                .selectWith(Delivery::deliveredOn, date)
                .sum(Delivery::getTotalPrice)
    }

    Customer getTopCustomer() {
        customers.maxBy(Customer::getTotalDonutsOrdered)
    }

    Multimap<DonutType, Customer> getCustomersByDonutTypesOrdered() {
        customers.groupByEach(Customer::getDonutTypesOrdered)
    }

    DoubleSummaryStatistics getDonutPriceStatistics(LocalDate fromDate, LocalDate toDate) {
        deliveries
                .select(each -> (each.deliveredOn(fromDate) || each.date.isAfter(fromDate)))
                .select(each -> (each.deliveredOn(toDate) || each.date.isBefore(toDate)))
                .flatCollect(delivery -> delivery.donuts)
                .summarizeDouble(donut -> donut.price.doubleValue())
    }

    @Override
    String toString() {
        "DonutShop(donuts=${donuts.toStringOfItemToCount()}, deliveries=$deliveries)"
    }
}
