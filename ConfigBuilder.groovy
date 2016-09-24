import org.georgep7n.fintastech.lendingclub.filter.*
import static org.georgep7n.fintastech.lendingclub.Loan.*
import static org.georgep7n.fintastech.lendingclub.Analyze.*

LOAN_FILTERS.add(new NoFilter())
ElementFilter stateFilter = new ElementFilter(STATE_INDEX)
def goodStates = [
        "DC", "WY", "MT", "WV", "NH", "CO", "AK", "TX", "SC", "OR", "SD", "CT", "UT", "MA",
        "IL", "WA", "GA", "KS", "AZ", "WI", "CA", "DE", "MN"
]

goodStates.each { state -> stateFilter.add(state) }
LOAN_FILTERS.add(stateFilter)
ElementFilter gradeFilter = new ElementFilter(GRADE_INDEX)
gradeFilter.add("A").add("B").add("C").add("D").add("E").add("F").add("G")
LOAN_FILTERS.add(gradeFilter)
ElementFilter termFilter = new ElementFilter(TERM_INDEX)
termFilter.add("36 months")
LOAN_FILTERS.add(termFilter)
LOAN_FILTERS.add(new ClosureFilter(
    { loan -> loan.attrs[INT_RATE_INDEX] >= 7 },
    "interest rate >= 7"))
LOAN_FILTERS.add(new ClosureFilter(
    { loan -> loan.attrs[INQUIRIES_IN_LAST_SIX_MONTHS_INDEX] <= 0 },
    "inquiries in the last 6 months <= 0"))
LOAN_FILTERS.add(new ClosureFilter(
    { loan -> loan.attrs[DEBT_TO_INCOME_RATIO_INDEX] <= 20 },
    "debt to income ratio <= 20"))
def purposeFilter = new ElementFilter(PURPOSE_INDEX)
purposeFilter.add("car")
purposeFilter.add("wedding")
purposeFilter.add("major_purchase")
purposeFilter.add("credit_card")
purposeFilter.add("home_improvement")
purposeFilter.add("educational")
purposeFilter.add("vacation")
purposeFilter.add("house")
purposeFilter.add("debt_consolidation")
LOAN_FILTERS.add(purposeFilter)
def homeOwnershipFilter = new ElementFilter(HOME_OWNERSHIP_INDEX)
homeOwnershipFilter.add("MORTGAGE")
LOAN_FILTERS.add(homeOwnershipFilter)
AndFilter allFilters = new AndFilter()
LOAN_FILTERS.each { loanFilter -> allFilters.add(loanFilter) }
LOAN_FILTERS.add(allFilters)
