package org.georgep7n.fintastech.lendingclub.filter

import spock.lang.Specification
import org.georgep7n.fintastech.lendingclub.*
import static org.georgep7n.fintastech.lendingclub.Loan.*

class LoanFilterTest extends Specification {

    def "DTIRatioFilter.include"() {
        setup:
            def loan = new Loan()
            LoanFilter filter = new ClosureFilter(
                { l -> l.attrs[DEBT_TO_INCOME_RATIO_INDEX] <= 10 }, "debt to income ratio <= 10")
        when:
            loan.attrs[DEBT_TO_INCOME_RATIO_INDEX] = 10; def result1 = filter.include(loan)
            loan.attrs[DEBT_TO_INCOME_RATIO_INDEX] = 9; def result2 = filter.include(loan)
            loan.attrs[DEBT_TO_INCOME_RATIO_INDEX] = 11; def result3 = filter.include(loan)
        then:
            result1 == true
            result2 == true
            result3 == false
    }

    def "GradeFilter.include"() {
        setup:
            def loan = new Loan()
            LoanFilter filter = new ElementFilter(GRADE_INDEX).add("A").add("B").add("C")
        when:
            loan.attrs[GRADE_INDEX] = "A";
            def true1 = filter.include(loan)
            loan.attrs[GRADE_INDEX] = "B";
            def true2 = filter.include(loan)
            loan.attrs[GRADE_INDEX] = "D";
            def false1 = filter.include(loan)
        then:
            true1; true2; !false1
    }

    def "AndFilter.include with no filters"() {
        setup:
            def loan = new Loan()
            LoanFilter filter = new AndFilter()
        when:
            def result = filter.include(loan)
        then:
            result == false
    }

    def "AndFilter.include with one filter which returns true"() {
        setup:
            def loan = new Loan()
            AndFilter filter = new AndFilter()
            filter.add(new NoFilter())
        when:
            def result = filter.include(loan)
        then:
            result == true
    }

    def "AndFilter.include with one filter which returns false"() {
        setup:
            def loan = new Loan()
            AndFilter filter = new AndFilter()
            filter.add(new ClosureFilter(
                { l -> l.attrs[INT_RATE_INDEX] >= 10 }, "interest rate >= 10"))
        when:
            loan.attrs[INT_RATE_INDEX] = 8
            def result = filter.include(loan)
        then:
            result == false
    }

    def "AndFilter.include with two filters which both return true"() {
        setup:
            def loan = new Loan()
            AndFilter filter = new AndFilter()
            filter.add(new ClosureFilter(
                { l -> l.attrs[INT_RATE_INDEX] >= 10 }, "interest rate >= 10"))
            filter.add(new ElementFilter(GRADE_INDEX).add("A"))
        when:
            loan.attrs[INT_RATE_INDEX] = 12
            loan.attrs[GRADE_INDEX] = "A"
            def result = filter.include(loan)
        then:
            result == true
    }

    def "AndFilter.include with two filters, one returning true"() {
        setup:
            def loan = new Loan()
            AndFilter filter = new AndFilter()
            filter.add(new ClosureFilter(
                { l -> l.attrs[INT_RATE_INDEX] >= 10 }, "interest rate >= 10"))
            filter.add(new ElementFilter(GRADE_INDEX).add("B"))
        when:
            loan.attrs[INT_RATE_INDEX] = 12
            loan.attrs[GRADE_INDEX] = "A"
            def result = filter.include(loan)
        then:
            result == false
    }

}
