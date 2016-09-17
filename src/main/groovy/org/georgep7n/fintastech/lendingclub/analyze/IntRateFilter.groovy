package georgep.lendingclub.trademon

/**
 */
class IntRateFilter implements LoanFilter {
    private def intRate

    IntRateFilter(double intRate) {
        this.intRate = intRate
    }
    @Override boolean include(Loan loan) {
        loan.intRate >= intRate
    }
    @Override String getDescription() {
        "interest rate >= " + intRate
    }
}
