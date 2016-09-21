package org.georgep7n.fintastech.lendingclub.analyze.filter

/**
 */
class NoFilter implements LoanFilter {

    @Override boolean include(loan) { true }
    @Override String getDescription() { "unfiltered" }
}
