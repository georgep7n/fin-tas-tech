package org.georgep7n.lendingclub.analyze

/**
 *
 */
interface LoanFilter {
    boolean include(Loan loan)
    String getDescription()
}
