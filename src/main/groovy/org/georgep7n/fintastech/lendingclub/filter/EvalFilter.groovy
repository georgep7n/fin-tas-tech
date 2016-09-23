package org.georgep7n.fintastech.lendingclub.filter
import org.georgep7n.fintastech.lendingclub.*

/**
 */
class EvalFilter implements LoanFilter {

    def expr

    EvalFilter(expr) {
        this.expr = expr
    }

    @Override boolean include(loan) {
        def scriptlet = "import static org.georgep7n.fintastech.lendingclub.Analyze.*; $expr"
        Eval.me("loan", loan, scriptlet)
    }
    @Override String getDescription() {
        expr
    }
}
