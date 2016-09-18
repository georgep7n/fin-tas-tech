package org.georgep7n.lendingclub.analyze

/**
 *
 */
class GradeFilter extends ElementFilter {

    @Override boolean include(Loan loan) {
        grades.contains(loan.grade)
    }

}
