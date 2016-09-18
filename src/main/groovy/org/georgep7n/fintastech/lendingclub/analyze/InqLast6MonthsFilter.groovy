package org.georgep7n.fintastech.lendingclub.analyze

/**
 */
class InqLast6MonthsFilter extends NumberFilter {

    @Override boolean include(Loan loan) {
        loan.inq_last_6mths <= value
    }
    @Override String getDescription() {
        "inquiries in the last 6 months <= " + value
    }
}
