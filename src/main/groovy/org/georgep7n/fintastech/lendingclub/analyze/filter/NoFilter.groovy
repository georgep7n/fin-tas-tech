package org.georgep7n.fintastech.lendingclub.analyze.filter
import org.georgep7n.fintastech.lendingclub.analyze.*

/**
 */
class NoFilter implements LoanFilter {

    @Override boolean include(loan) { true }
    @Override String getDescription() { "unfiltered" }
}
