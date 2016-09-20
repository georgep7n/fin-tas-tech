package org.georgep7n.fintastech.lendingclub.analyze

/**
 */
class ClosureFilter implements LoanFilter {

    def includeClosure
    def desc
    InqLast6MonthsFilter(closure, desc) {
        this.includeClosure = closure
        this.desc = desc
    }

    @Override boolean include(Loan loan) {
        includeClosure.call(loan)
    }
    @Override String getDescription() {
        desc
    }
}
