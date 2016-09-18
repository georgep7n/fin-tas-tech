package org.georgep7n.fintastech.lendingclub.analyze

/**
 */
class HomeOwnershipFilter extends ElementFilter {

    @Override boolean include(Loan loan) {
        ownershipTypes.contains(loan.home_ownership)
    }

}
