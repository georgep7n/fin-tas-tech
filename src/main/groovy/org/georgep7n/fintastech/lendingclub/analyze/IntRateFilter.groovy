package org.georgep7n.fintastech.lendingclub.analyze

/**
 */
class IntRateFilter extends NumberFilter {

    @Override boolean include(Loan loan) {
        loan.intRate >= value
    }

    @Override String getDescription() {
        "interest rate >= " + value
    }
}
