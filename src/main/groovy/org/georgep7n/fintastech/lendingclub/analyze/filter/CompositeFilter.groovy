package org.georgep7n.fintastech.lendingclub.analyze.filter

/**
 *
 */
abstract class CompositeFilter implements LoanFilter {

    protected List<LoanFilter> filters = []

    CompositeFilter add(LoanFilter loanFilter) {
        filters.add(loanFilter)
        this
    }

    @Override final String getDescription() {
        StringBuffer description = new StringBuffer()
        filters.each { loanFilter ->
            description.append(loanFilter.getDescription()).append(", ")
        }
        description.toString()
    }
}
