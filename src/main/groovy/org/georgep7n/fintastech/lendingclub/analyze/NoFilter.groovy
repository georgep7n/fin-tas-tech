package org.georgep7n.fintastech.lendingclub.analyze

/**
 */
class NoFilter implements LoanFilter {

    @Override boolean include(Loan loan) { true }
    @Override String getDescription() { "unfiltered" }
}
