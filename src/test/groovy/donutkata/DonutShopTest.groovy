/*
 * Copyright 2022 The author or original authors.
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

import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

import org.eclipse.collections.impl.factory.Lists
import org.eclipse.collections.impl.test.Verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples.pair
import static org.junit.jupiter.api.Assertions.assertEquals

class DonutShopTest {
    private final Clock clock = Clock.fixed(Instant.now(), ZoneOffset.UTC)
    private final LocalDate today = LocalDate.now(clock)
    private final LocalDate tomorrow = today + 1
    private final LocalDate yesterday = today - 1
    private DonutShop donutShop

    @BeforeEach
    void setUp() {
        donutShop = new DonutShop().tap {
            makeDonuts(DonutType.BOSTON_CREAM, 10)
            makeDonuts(DonutType.BAVARIAN_CREAM, 10)
            makeDonuts(DonutType.BLUEBERRY, 10)
            makeDonuts(DonutType.GLAZED, 10)
            makeDonuts(DonutType.OLD_FASHIONED, 10)
            makeDonuts(DonutType.PUMPKIN, 10)
            makeDonuts(DonutType.JELLY, 10)
            makeDonuts(DonutType.VANILLA_FROSTED, 10)
        }

        donutShop.with {
            var delivery1 = deliverOrder("Ted Smith", today, "BC:2,BA:1,B:2")
            assert delivery1.totalPrice == 6.75
            var delivery2 = deliverOrder("Mary Williams", today, "BC:1,G:1")
            assert delivery2.totalPrice == 2.7
            var delivery3 = deliverOrder("Sally Prince", tomorrow, "BC:6,P:2,B:2,OF:2")
            assert delivery3.totalPrice == 12.0
            var delivery4 = deliverOrder("Donnie Dapper", yesterday, "BC:6,P:2,B:2,OF:2,G:10")
            assert delivery4.totalPrice == 20.9
        }
    }

    @AfterEach
    void tearDown() {
        donutShop = null
    }

    @Test
    void getTop2Donuts() {
        var expected = Lists.mutable.empty()
                .with(pair(DonutType.BOSTON_CREAM, 15))
                .with(pair(DonutType.GLAZED, 11))
        assert expected == donutShop.getTopDonuts(2)
    }

    @Test
    void totalDeliveryValueByDate() {
        assert donutShop.getTotalDeliveryValueFor(today) == 9.45
        assert donutShop.getTotalDeliveryValueFor(tomorrow) == 12.0
        assert donutShop.getTotalDeliveryValueFor(yesterday) == 20.9
    }

    @Test
    void getTopCustomer() {
        assert donutShop.topCustomer.name == 'Donnie Dapper'
    }

    @Test
    void getCustomersByDonutTypesOrdered() {
        var multimap = donutShop.customersByDonutTypesOrdered
        assert multimap.keySet().size() == 6
        Verify.assertIterableSize(1, multimap.get(DonutType.BAVARIAN_CREAM))
        Verify.assertAllSatisfy(
                multimap.get(DonutType.BAVARIAN_CREAM),
                customer -> customer.named('Ted Smith'))
    }

    @Test
    void getDonutPriceStatistics() {
        donutShop.getDonutPriceStatistics(today, today).with {
            assertEquals(9.45d, sum, 0.01)
            assertEquals(1.35d, average, 0.01)
            assertEquals(7, count, 0.01)
        }

        donutShop.getDonutPriceStatistics(tomorrow, tomorrow).with {
            assertEquals(12.0d, sum, 0.01)
            assertEquals(1.0d, average, 0.01)
            assertEquals(12, count, 0.01)
        }

        donutShop.getDonutPriceStatistics(yesterday, yesterday).with {
            assertEquals(20.9d, sum, 0.001)
            assertEquals(0.95d, average, 0.01)
            assertEquals(22, count, 0.01)
        }

        donutShop.getDonutPriceStatistics(yesterday, tomorrow).with {
            assertEquals(42.35d, sum, 0.01)
            assertEquals(1.03d, average, 0.01)
            assertEquals(41, count, 0.01)
            assertEquals(0.95, min, 0.01)
            assertEquals(1.35, max, 0.01)
        }
    }
}
