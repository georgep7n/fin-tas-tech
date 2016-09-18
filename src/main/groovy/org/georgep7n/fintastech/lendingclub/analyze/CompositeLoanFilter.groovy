package org.georgep7n.lendingclub.analyze

/**
 *
 */
class CompositeLoanFilter implements LoanFilter {

    private List<LoanFilter> filters = []

    void add(LoanFilter loanFilter) { filters.add(loanFilter) }

    @Override
    boolean include(Loan loan) {
        def result = true
        filters.each { loanFilter ->
            if (!loanFilter.include(loan)) {
                result = false
            }
        }
        result
    }

    @Override
    String getDescription() {
        StringBuffer description = new StringBuffer()
        filters.each { loanFilter ->
            description.append(loanFilter.getDescription()).append(", ")
        }
        description.toString()
    }
}
