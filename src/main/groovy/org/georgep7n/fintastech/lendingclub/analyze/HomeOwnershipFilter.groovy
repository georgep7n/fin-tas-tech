package org.georgep7n.fintastech.lendingclub.analyze

/**
 */
class HomeOwnershipFilter extends ElementFilter {

    @Override boolean include(Loan loan) {
        elements.contains(loan.home_ownership)
    }

}
