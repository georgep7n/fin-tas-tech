package org.georgep7n.lendingclub.analyze

/**
 */
class TermFilter implements LoanFilter {
    List<String> termTypes = []
    void add(String termType) { termTypes.add(termType) }
    @Override boolean include(Loan loan) {
        return termTypes.contains(loan.term)
    }
    @Override String getDescription() {
        termTypes.toString()
    }
}
