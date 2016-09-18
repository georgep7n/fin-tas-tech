package org.georgep7n.lendingclub.analyze

/**
 */
class DTILoanFilter extends NumberFilter {

    @Override boolean include(Loan loan) {
        loan.dti <= value
    }

    @Override String getDescription() {
        "debt-to-income ratio <= " + value
    }
}
