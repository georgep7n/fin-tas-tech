package org.georgep7n.fintastech.lendingclub.analyze.filter

/**
 *
 */
interface LoanFilter {
    boolean include(loan)
    String getDescription()
}
