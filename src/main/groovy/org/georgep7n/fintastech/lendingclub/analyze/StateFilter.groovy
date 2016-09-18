package org.georgep7n.lendingclub.analyze

/**
 *
 */
class StateFilter extends ElementFilter {

    @Override boolean include(Loan loan) {
        elements.contains(loan.state)
    }
}
