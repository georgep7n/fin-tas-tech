import org.georgep7n.fintastech.lendingclub.filter.*
import static org.georgep7n.fintastech.lendingclub.Loan.*
import static org.georgep7n.fintastech.lendingclub.Analyze.*

loanFilters.add(new NoFilter())
ElementFilter stateFilter = new ElementFilter(STATE_INDEX)
def goodStates = [
        "DC", "WY", "MT", "WV", "NH", "CO", "AK", "TX", "SC", "OR", "SD", "CT", "UT", "MA",
        "IL", "WA", "GA", "KS", "AZ", "WI", "CA", "DE", "MN"
]

goodStates.each { state -> stateFilter.add(state) }
loanFilters.add(stateFilter)
ElementFilter gradeFilter = new ElementFilter(GRADE_INDEX)
gradeFilter.add(GRADE_A).add(GRADE_B).add(GRADE_C).add(GRADE_D).add(GRADE_E).add(GRADE_F).add(GRADE_G)
loanFilters.add(gradeFilter)
ElementFilter termFilter = new ElementFilter(TERM_INDEX)
termFilter.add(TERM_36_MONTHS)
loanFilters.add(termFilter)
//loanFilters.add(new EvalFilter("loan.attrs[INT_RATE_INDEX] >= 7"))
loanFilters.add(new ClosureFilter(
    { loan -> loan.attrs[INT_RATE_INDEX] >= 7 },
    "interest rate >= 7"))
loanFilters.add(new ClosureFilter(
    { loan -> loan.attrs[INQUIRIES_IN_LAST_SIX_MONTHS_INDEX] <= 0 },
    "inquiries in the last 6 months <= 0"))
loanFilters.add(new ClosureFilter(
    { loan -> loan.attrs[DEBT_TO_INCOME_RATIO_INDEX] <= 20 },
    "debt to income ratio <= 20"))
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
loanFilters.add(purposeFilter)
def homeOwnershipFilter = new ElementFilter(HOME_OWNERSHIP_INDEX)
homeOwnershipFilter.add(HOME_OWNERSHIP_MORTGAGE)
loanFilters.add(homeOwnershipFilter)
AndFilter allFilters = new AndFilter()
loanFilters.each { loanFilter -> allFilters.add(loanFilter) }
loanFilters.add(allFilters)
