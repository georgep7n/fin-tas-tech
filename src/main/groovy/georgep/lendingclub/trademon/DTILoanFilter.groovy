package georgep.lendingclub.trademon

/**
 */
class DTILoanFilter implements LoanFilter {
    private def dti
    DTILoanFilter(double dti) {
        this.dti = dti
    }
    @Override boolean include(Loan loan) {
        loan.dti <= dti
    }
    @Override String getDescription() {
        "debt-to-income ratio <= " + dti
    }
}
