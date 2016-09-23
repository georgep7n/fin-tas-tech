package org.georgep7n.fintastech.lendingclub.filter
import org.georgep7n.fintastech.lendingclub.*

/**
 */
class NoFilter implements LoanFilter {

    @Override boolean include(loan) { true }
    @Override String getDescription() { "unfiltered" }
}
