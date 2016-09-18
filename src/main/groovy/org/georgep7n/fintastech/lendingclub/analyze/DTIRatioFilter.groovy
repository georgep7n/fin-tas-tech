package org.georgep7n.fintastech.lendingclub.analyze

/**
 */
class DTIRatioFilter extends NumberFilter {

    @Override boolean include(Loan loan) { loan.dti <= value }

    @Override String getDescription() {
        "debt-to-income ratio <= " + value
    }
}
