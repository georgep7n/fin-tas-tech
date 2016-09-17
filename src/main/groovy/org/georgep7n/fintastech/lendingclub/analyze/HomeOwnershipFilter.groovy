package org.georgep7n.lendingclub.analyze

/**
 */
class HomeOwnershipFilter implements LoanFilter {
    List<String> ownershipTypes = []
    void add(String ownershipType) { ownershipTypes.add(ownershipType) }
    @Override boolean include(Loan loan) {
        ownershipTypes.contains(loan.home_ownership)
    }
    @Override String getDescription() {
        ownershipTypes.toString()
    }
}
