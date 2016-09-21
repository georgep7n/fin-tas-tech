package org.georgep7n.fintastech.lendingclub.filter

/**
 *
 */
interface LoanFilter {
    boolean include(loan)
    String getDescription()
}
