package org.georgep7n.fintastech.lendingclub.analyze

/**
 */
class TermFilter extends ElementFilter {

    @Override boolean include(Loan loan) {
        elements.contains(loan.term)
    }

}
