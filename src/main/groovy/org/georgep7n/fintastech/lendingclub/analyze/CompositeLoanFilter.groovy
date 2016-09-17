package org.georgep7n.lendingclub.analyze

/**
 *
 */
class CompositeLoanFilter implements LoanFilter {

    private List<LoanFilter> loanFilters = []
    void add(LoanFilter loanFilter) { loanFilters.add(loanFilter); }
    boolean include(Loan loan) {
        def result = true
        loanFilters.each { loanFilter ->
            if (!loanFilter.include(loan)) {
                result = false
            }
        }
        result
    }
    String getDescription() {
        StringBuffer description = new StringBuffer("");
        loanFilters.each { loanFilter ->
            description.append(loanFilter.getDescription()).append(", ")
        }
        return description.toString()
    }
}
