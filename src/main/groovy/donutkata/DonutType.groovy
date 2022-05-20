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

import org.eclipse.collections.impl.utility.ArrayIterate

enum DonutType {
    BOSTON_CREAM("BC"),
    GLAZED("G"),
    OLD_FASHIONED("OF"),
    CHOCOLATE_GLAZED("CG"),
    VANILLA_FROSTED("VF"),
    PUMPKIN("P"),
    BLUEBERRY("B"),
    JELLY("J"),
    BAVARIAN_CREAM("BA")

    private String abbreviation

    private DonutType(String abbreviation) {
        this.abbreviation = abbreviation
    }

    private boolean abbreviationEquals(String abbreviation) {
        this.abbreviation == abbreviation
    }

    static DonutType forAbbreviation(String abbreviation) {
        ArrayIterate.detectWith(values(), DonutType::abbreviationEquals, abbreviation)
    }
}
