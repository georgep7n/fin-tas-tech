package org.georgep7n.fintastech.lendingclub.analyze

/**
 *
 */
class StateFilter extends ElementFilter {

    @Override boolean include(Loan loan) {
        elements.contains(loan.state)
    }
}
