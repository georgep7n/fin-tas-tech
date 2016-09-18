package org.georgep7n.lendingclub.analyze

/**
 *
 */
class PurposeFilter extends ElementFilter {

    @Override boolean include(Loan loan) {
        elements.contains(loan.purpose)
    }

}
