package org.georgep7n.lendingclub.analyze

/**
 */
class InqLast6MonthsFilter implements LoanFilter {
    private def inqLast6Months

    InqLast6MonthsFilter(int inqLast6Months) {
        this.inqLast6Months = inqLast6Months
    }
    @Override boolean include(Loan loan) {
        loan.inq_last_6mths <= inqLast6Months
    }
    @Override String getDescription() {
        "inquiries in the last 6 months <= " + inqLast6Months
    }
}
