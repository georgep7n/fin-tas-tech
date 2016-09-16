package georgep.lendingclub.trademon

/**
 *
 */
class PurposeFilter implements LoanFilter {

    List<String> purposes = []
    void add(String purpose) { purposes.add(purpose) }
    @Override boolean include(Loan loan) {
        purposes.contains(loan.purpose)
    }
    @Override String getDescription() {
        purposes.toString()
    }
}