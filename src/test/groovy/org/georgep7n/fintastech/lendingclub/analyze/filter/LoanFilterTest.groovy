package org.georgep7n.fintastech.lendingclub.analyze.filter

import spock.lang.Specification
import org.georgep7n.fintastech.lendingclub.analyze.*

class LoanFilterTest extends Specification {

    def "DTIRatioFilter.include"() {
        setup:
            Loan loan = new Loan()
            LoanFilter filter = new ClosureFilter(
                { l -> l.dti <= 10 }, "debt to income ratio <= 10")
        when:
            loan.dti = 10; def result1 = filter.include(loan)
            loan.dti = 9; def result2 = filter.include(loan)
            loan.dti = 11; def result3 = filter.include(loan)
        then:
            result1 == true
            result2 == true
            result3 == false
    }

    def "GradeFilter.include"() {
        setup:
            Loan loan = new Loan()
            LoanFilter filter = new ElementFilter("grade").add("A").add("B").add("C")
        when:
            loan.grade = "A"; def true1 = filter.include(loan)
            loan.grade = "B"; def true2 = filter.include(loan)
            loan.grade = "D"; def false1 = filter.include(loan)
        then:
            true1; true2; !false1
    }

    def "AndFilter.include with no filters"() {
        setup:
            Loan loan = new Loan()
            LoanFilter filter = new AndFilter()
        when:
            def result = filter.include(loan)
        then:
            result == false
    }

    def "AndFilter.include with one filter which returns true"() {
        setup:
            Loan loan = new Loan()
            AndFilter filter = new AndFilter()
            filter.add(new NoFilter())
        when:
            def result = filter.include(loan)
        then:
            result == true
    }

    def "AndFilter.include with one filter which returns false"() {
        setup:
            Loan loan = new Loan()
            AndFilter filter = new AndFilter()
            filter.add(new ClosureFilter(
                { l -> l.intRate >= 10 }, "interest rate >= 10"))
        when:
            loan.intRate = 8
            def result = filter.include(loan)
        then:
            result == false
    }

    def "AndFilter.include with two filters which both return true"() {
        setup:
            Loan loan = new Loan()
            AndFilter filter = new AndFilter()
            filter.add(new ClosureFilter(
                { l -> l.intRate >= 10 }, "interest rate >= 10"))
            filter.add(new ElementFilter("grade").add("A"))
        when:
            loan.intRate = 12
            loan.grade = "A"
            def result = filter.include(loan)
        then:
            result == true
    }

    def "AndFilter.include with two filters, one returning true"() {
        setup:
            Loan loan = new Loan()
            AndFilter filter = new AndFilter()
            filter.add(new ClosureFilter(
                { l -> l.intRate >= 10 }, "interest rate >= 10"))
            filter.add(new ElementFilter("grade").add("B"))
        when:
            loan.intRate = 12
            loan.grade = "A"
            def result = filter.include(loan)
        then:
            result == false
    }

}
