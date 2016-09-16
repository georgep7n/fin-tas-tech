package georgep.lendingclub.trademon

/**
 */
class NoFilter implements LoanFilter {

    @Override boolean include(Loan loan) { true }
    @Override String getDescription() { "unfiltered" }
}
