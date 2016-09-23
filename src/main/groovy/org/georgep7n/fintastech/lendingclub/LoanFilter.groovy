package org.georgep7n.fintastech.lendingclub

/**
 *
 */
interface LoanFilter {
    boolean include(loan)
    String getDescription()
}
