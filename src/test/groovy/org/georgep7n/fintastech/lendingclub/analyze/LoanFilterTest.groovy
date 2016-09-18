package org.georgep7n.fintastech.lendingclub.analyze

import spock.lang.Specification

class LoanFilterTest extends Specification {

    def "DTIRatioFilter.include"() {
        setup:
            Loan loan = new Loan()
            LoanFilter filter = new DTIRatioFilter().set(10)
        when:
            loan.dti = 10; def true1 = filter.include(loan)
            loan.dti = 9; def true2 = filter.include(loan)
            loan.dti = 11; def false1 = filter.include(loan)
        then:
            true1 == true
            true2 == true
            false1 == false
    }

}
