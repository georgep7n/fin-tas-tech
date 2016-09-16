package georgep.lendingclub.trademon

/**
 *
 */
interface LoanFilter {
    boolean include(Loan loan)
    String getDescription()
}