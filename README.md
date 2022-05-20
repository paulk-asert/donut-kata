# donut-kata

A Groovy solution for:
https://github.com/BNYMellon/CodeKatas/tree/master/donut-kata-solutions

Currently, Groovy 4 is used and should work on JDK 8 through 18.

Native records will be used for JDK16+ and _record-like_ classes on earlier JDKs.
Record-like classes behave in a very similar manner to records but don't extend `java.lang.Record`
nor follow Java record serialization conventions (but rather traditional conventions) and won't
be seen by Java as a record.

Differences:
* The Groovy solution tries to avoid assuming `double` as the type for the price of donuts.
Groovy provides good support for using `BigDecimal` in such scenarios which avoids rounding issues.
The `DonutShop#getDonutPriceStatistics` method still uses `double` to show off that aspect of the
[Eclipse Collections](https://github.com/eclipse/eclipse-collections) library.
