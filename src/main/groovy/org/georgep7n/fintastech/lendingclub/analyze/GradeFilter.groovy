package georgep.lendingclub.trademon

/**
 *
 */
class GradeFilter implements LoanFilter {

    List<String> grades = []
    GradeFilter add(String purpose) {
        grades.add(purpose)
        this
    }
    @Override boolean include(Loan loan) {
        grades.contains(loan.grade)
    }
    @Override String getDescription() {
        grades.toString()
    }
}
