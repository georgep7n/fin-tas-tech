package org.georgep7n.fintastech.lendingclub.filter

import spock.lang.Specification
import org.georgep7n.fintastech.lendingclub.*
import static org.georgep7n.fintastech.lendingclub.Loan.*

class LoanTest extends Specification {

    def "attr access"() {
        setup:
            def loan = new Loan()
        when:
            loan.attrs[DEBT_TO_INCOME_RATIO_INDEX] = 10
        then:
            loan.attrs[DEBT_TO_INCOME_RATIO_INDEX] - 1 == 9
            loan.attrs[DEBT_TO_INCOME_RATIO_INDEX] + 1 == 11
    }

}
