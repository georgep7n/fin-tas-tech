package org.georgep7n.fintastech.lendingclub.analyze.filter

import spock.lang.Specification
import static org.georgep7n.fintastech.lendingclub.analyze.Main.*

class LoanFilterTest extends Specification {

    def "DTIRatioFilter.include"() {
        setup:
            def loan = []
            LoanFilter filter = new ClosureFilter(
                { l -> l[DEBT_TO_INCOME_RATIO] <= 10 }, "debt to income ratio <= 10")
        when:
            loan[DEBT_TO_INCOME_RATIO] = 10; def result1 = filter.include(loan)
            loan[DEBT_TO_INCOME_RATIO] = 9; def result2 = filter.include(loan)
            loan[DEBT_TO_INCOME_RATIO] = 11; def result3 = filter.include(loan)
        then:
            result1 == true
            result2 == true
            result3 == false
    }

    def "GradeFilter.include"() {
        setup:
            def loan = []
            LoanFilter filter = new ElementFilter(GRADE_INDEX).add("A").add("B").add("C")
        when:
            loan[GRADE_INDEX] = "A";
            def true1 = filter.include(loan)
            loan[GRADE_INDEX] = "B";
            def true2 = filter.include(loan)
            loan[GRADE_INDEX] = "D";
            def false1 = filter.include(loan)
        then:
            true1; true2; !false1
    }

    def "AndFilter.include with no filters"() {
        setup:
            def loan = []
            LoanFilter filter = new AndFilter()
        when:
            def result = filter.include(loan)
        then:
            result == false
    }

    def "AndFilter.include with one filter which returns true"() {
        setup:
            def loan = []
            AndFilter filter = new AndFilter()
            filter.add(new NoFilter())
        when:
            def result = filter.include(loan)
        then:
            result == true
    }

    def "AndFilter.include with one filter which returns false"() {
        setup:
            def loan = []
            AndFilter filter = new AndFilter()
            filter.add(new ClosureFilter(
                { l -> l[INT_RATE_INDEX] >= 10 }, "interest rate >= 10"))
        when:
            loan[INT_RATE_INDEX] = 8
            def result = filter.include(loan)
        then:
            result == false
    }

    def "AndFilter.include with two filters which both return true"() {
        setup:
            def loan = []
            AndFilter filter = new AndFilter()
            filter.add(new ClosureFilter(
                { l -> l[INT_RATE_INDEX] >= 10 }, "interest rate >= 10"))
            filter.add(new ElementFilter(GRADE_INDEX).add("A"))
        when:
            loan[INT_RATE_INDEX] = 12
            loan[GRADE_INDEX] = "A"
            def result = filter.include(loan)
        then:
            result == true
    }

    def "AndFilter.include with two filters, one returning true"() {
        setup:
            def loan = []
            AndFilter filter = new AndFilter()
            filter.add(new ClosureFilter(
                { l -> l[INT_RATE_INDEX] >= 10 }, "interest rate >= 10"))
            filter.add(new ElementFilter(GRADE_INDEX).add("B"))
        when:
            loan[INT_RATE_INDEX] = 12
            loan[GRADE_INDEX] = "A"
            def result = filter.include(loan)
        then:
            result == false
    }

}
