package org.georgep7n.fintastech.lendingclub.analyze.filter
import org.georgep7n.fintastech.lendingclub.analyze.*

/**
 */
class ClosureFilter implements LoanFilter {

    def includeClosure
    def desc

    ClosureFilter(closure, desc) {
        this.includeClosure = closure
        this.desc = desc
    }

    @Override boolean include(loan) {
        includeClosure.call(loan)
    }
    @Override String getDescription() {
        desc
    }
}
