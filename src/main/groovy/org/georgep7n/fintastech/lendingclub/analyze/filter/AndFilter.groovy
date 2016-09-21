package org.georgep7n.fintastech.lendingclub.analyze.filter

/**
 *
 */
class AndFilter extends CompositeFilter {

    @Override
    boolean include(loan) {
        if (filters.size() == 0) {
            return false
        }
        def result = true
        filters.each { loanFilter ->
            if (!loanFilter.include(loan)) {
                result = false
            }
        }
        result
    }

}
