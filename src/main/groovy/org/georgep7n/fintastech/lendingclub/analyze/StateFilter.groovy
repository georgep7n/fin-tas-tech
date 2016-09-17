package georgep.lendingclub.trademon

/**
 *
 */
class StateFilter implements LoanFilter {

    List<String> states = []
    StateFilter add(String state) {
        states.add(state)
        this
    }
    @Override boolean include(Loan loan) {
        states.contains(loan.state)
    }
    @Override String getDescription() {
        states.toString()
    }
}
