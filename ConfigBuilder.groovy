import org.georgep7n.fintastech.lendingclub.filter.*
import static org.georgep7n.fintastech.lendingclub.Loan.*
import static org.georgep7n.fintastech.lendingclub.Analyze.*

//config.verbose = true
config.loanFilters.add(new NoFilter())
ElementFilter stateFilter = new ElementFilter(STATE_INDEX)
def goodStates = [
        "DC", "WY", "MT", "WV", "NH", "CO", "AK", "TX", "SC", "OR", "SD", "CT", "UT", "MA",
        "IL", "WA", "GA", "KS", "AZ", "WI", "CA", "DE", "MN"
]

goodStates.each { state -> stateFilter.add(state) }
config.loanFilters.add(stateFilter)
ElementFilter gradeFilter = new ElementFilter(GRADE_INDEX)
gradeFilter.add(GRADE_A).add(GRADE_B).add(GRADE_C).add(GRADE_D).add(GRADE_E).add(GRADE_F).add(GRADE_G)
config.loanFilters.add(gradeFilter)
ElementFilter termFilter = new ElementFilter(TERM_INDEX)
termFilter.add(TERM_36_MONTHS)
config.loanFilters.add(termFilter)
7.upto(7) {
    config.loanFilters.add(new ClosureFilter(
        { loan -> loan.attrs[INT_RATE_INDEX] >= it },
        "interest rate >= ${it}"))
}
0.upto(0) {
    config.loanFilters.add(new ClosureFilter(
        { loan -> loan.attrs[INQUIRIES_IN_LAST_SIX_MONTHS_INDEX] <= it },
        "inquiries in the last 6 months <= ${it}"))
}
20.upto(20) {
    config.loanFilters.add(new ClosureFilter(
        { loan -> loan.attrs[DEBT_TO_INCOME_RATIO_INDEX] <= it },
        "debt to income ratio <= ${it}"))
}
0.upto(0) {
    config.loanFilters.add(new ClosureFilter(
        { loan -> loan.attrs[DELINQ_LAST_TWO_YEARS_INDEX] <= it },
        "delinquencies in the last two years <= ${it}"))
}
def purposeFilter = new ElementFilter(PURPOSE_INDEX)
purposeFilter.add(PURPOSE_CAR)
purposeFilter.add(PURPOSE_WEDDING)
purposeFilter.add(PURPOSE_MAJOR_PURCHASE)
purposeFilter.add(PURPOSE_CREDIT_CARD)
purposeFilter.add(PURPOSE_HOME_IMPROVEMENT)
purposeFilter.add(PURPOSE_EDUCATIONAL)
purposeFilter.add(PURPOSE_VACATION)
purposeFilter.add(PURPOSE_HOUSE)
purposeFilter.add(PURPOSE_DEBT_CONSOLIDATION)
config.loanFilters.add(purposeFilter)
def homeOwnershipFilter = new ElementFilter(HOME_OWNERSHIP_INDEX)
homeOwnershipFilter.add(HOME_OWNERSHIP_MORTGAGE)
config.loanFilters.add(homeOwnershipFilter)
AndFilter allFilters = new AndFilter()
config.loanFilters.each { loanFilter -> allFilters.add(loanFilter) }
config.loanFilters.add(allFilters)
