package org.georgep7n.lendingclub.analyze

/**
 */
class NoFilter implements LoanFilter {

    @Override boolean include(Loan loan) { true }
    @Override String getDescription() { "unfiltered" }
}
