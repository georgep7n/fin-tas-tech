package org.georgep7n.fintastech.lendingclub.analyze

/**
 *
 */
interface LoanFilter {
    boolean include(Loan loan)
    String getDescription()
}
