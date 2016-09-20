package org.georgep7n.fintastech.lendingclub.analyze

import java.text.NumberFormat

/**
 *
 */
public class Main {

    static class Run {
        LoanFilter loanFilter
        int numLoans
        double score
    }

    private static NumberFormat PCT_FORMAT = NumberFormat.getNumberInstance()
    static {
        PCT_FORMAT.setMinimumFractionDigits(1);
        PCT_FORMAT.setMaximumFractionDigits(1);
    }

    static List<LoanFilter> LOAN_FILTERS = []
    static {
        LOAN_FILTERS.add(new NoFilter())
        ElementFilter stateFilter = new ElementFilter("state")
        def goodStates = [
                "DC", "WY", "MT", "WV", "NH", "CO", "AK", "TX", "SC", "OR", "SD", "CT", "UT", "MA",
                "IL", "WA", "GA", "KS", "AZ", "WI", "CA", "DE", "MN"
        ]
//        def badStates = [
//                "VA", "HI", "MD", "KY", "PA", "RI", "AR", "OH", "NC", "LA",
//                "NJ", "MI", "MO", "OK", "NM", "FL", "AL", "NV", "IN", "TN"
//        ]
        goodStates.each { state -> stateFilter.add(state) }
        LOAN_FILTERS.add(stateFilter)
        ElementFilter gradeFilter = new ElementFilter("grade")
        gradeFilter.add("A").add("B").add("C").add("D").add("E").add("F").add("G")
        LOAN_FILTERS.add(gradeFilter)
        ElementFilter termFilter = new ElementFilter("term")
        termFilter.add("36 months");
        LOAN_FILTERS.add(termFilter)
        LOAN_FILTERS.add(new IntRateFilter().set(7.0))
        LOAN_FILTERS.add(new InqLast6MonthsFilter().set(0))
        LOAN_FILTERS.add(new DTIRatioFilter().set(20.0))
        def purposeFilter = new ElementFilter("purpose")
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
        def homeOwnershipFilter = new ElementFilter("home_ownership")
        homeOwnershipFilter.add("MORTGAGE")
        LOAN_FILTERS.add(homeOwnershipFilter)
//        homeOwnershipFilter = new HomeOwnershipFilter()
//        homeOwnershipFilter.add("OWN")
//        LOAN_FILTERS.add(homeOwnershipFilter)
//        homeOwnershipFilter = new HomeOwnershipFilter()
//        homeOwnershipFilter.add("RENT")
//        LOAN_FILTERS.add(homeOwnershipFilter)
    }

    public static void main(String[] args) throws IOException {
        def allLoans = LoanSlurper.slurpCSVFiles()
//        TreeSet<String> allStates = new TreeSet<>()
//        allLoans.each { loan ->
//            allStates.add((String) loan.state)
//        }
//        allStates.each { state ->
//            StateFilter filter = new StateFilter()
//            filter.add((String) state)
//            LOAN_FILTERS.add(filter)
//        }
        AndFilter allFilters = new AndFilter()
        LOAN_FILTERS.each { loanFilter -> allFilters.add(loanFilter) }
        LOAN_FILTERS.add(allFilters)
        List<Run> runs = []
        LOAN_FILTERS.each { loanFilter ->
            Run run = new Run()
            run.loanFilter = loanFilter
            def purposes = new TreeSet()
            def statuses = new TreeSet()
            def grades = new TreeSet()
            def states = new TreeSet()
            def countsByPurpose = [:]
            def countsByStatus = [:]
            def countsByPurposeAndStatus = [:]
            def countsByGradeAndStatus = [:]
            def countsByStateAndStatus = [:]
            println("Analyzing with filter: " + loanFilter.getDescription().toUpperCase())
            def loans = LoanSlurper.filter(allLoans, loanFilter)
            run.numLoans = loans.size()
            println("$run.numLoans loans to analyze")
            if (run.numLoans < 500) {
                println("not enough loans to analyze")
            } else {
                runs.add(run)
                loans.each { loan ->
                    purposes.add(loan.purpose)
                    statuses.add(loan.loan_status)
                    grades.add(loan.grade)
                    states.add(loan.state)
                };
                purposes.each { purpose -> countsByPurpose[purpose] = 0 }
                statuses.each { status -> countsByStatus[status] = 0 }
                purposes.each { purpose ->
                    statuses.each { status ->
                        countsByPurposeAndStatus[purpose + "." + status] = 0
                    }
                }
                grades.each { grade ->
                    statuses.each { status ->
                        countsByGradeAndStatus[grade + "." + status] = 0
                    }
                }
                states.each { state ->
                    statuses.each { status ->
                        countsByStateAndStatus[state + "." + status] = 0
                    }
                }
                loans.each { loan ->
                    def count = countsByPurpose[loan.purpose]
                    countsByPurpose[loan.purpose] = count + 1
                    count = countsByStatus[loan.loan_status]
                    countsByStatus[loan.loan_status] = count + 1
                    count = countsByPurposeAndStatus[loan.purpose + "." + loan.loan_status]
                    countsByPurposeAndStatus[loan.purpose + "." + loan.loan_status] = count + 1
                    count = countsByGradeAndStatus[loan.grade + "." + loan.loan_status]
                    countsByGradeAndStatus[loan.grade + "." + loan.loan_status] = count + 1
                    count = countsByStateAndStatus[loan.state + "." + loan.loan_status]
                    countsByStateAndStatus[loan.state + "." + loan.loan_status] = count + 1
                }
                //println(countsByPurpose)
                //println(countsByStatus)
                def numChargedOff = countsByStatus[Loan.CHARGED_OFF_LOAN_STATUS]
                def numFullyPaid = countsByStatus[Loan.FULLY_PAID_LOAN_STATUS]
                def numTotal = numChargedOff + numFullyPaid
                run.score = 100 * ((numFullyPaid / numTotal) as double)
                println("Overall " + Loan.FULLY_PAID_LOAN_STATUS + ": " + PCT_FORMAT.format(run.score) + "%")
                //println(countsByPurposeAndStatus)
                println()
                println("Fully Paid by Purpose")
                purposes.each { purpose ->
                    statsByFactorAndStatus(countsByPurposeAndStatus, (String) purpose)
                }
                println()
                println("Fully Paid by Grade")
                // println(countsByGradeAndStatus)
                grades.each { grade ->
                    statsByFactorAndStatus(countsByGradeAndStatus, (String) grade)
                }
                println("Fully Paid by State")
                println("State,% Fully Paid,Total")
                states.each { state ->
                    statsByFactorAndStatus(countsByStateAndStatus, (String) state)
                }
                println("---------------------")
            }
        }
        println("Run Results")
        runs.sort { a, b -> a.score == b.score ? 0 : a.score < b.score ? 1 : -1 }
        runs.each { run ->
            println(run.loanFilter.getDescription() + " " + run.numLoans + " " + PCT_FORMAT.format(run.score) + "%")
        }
    }

    private static void statsByFactorAndStatus(countsByFactorAndStatus, String factor) {
        def numChargedOff = countsByFactorAndStatus[factor + "." + Loan.CHARGED_OFF_LOAN_STATUS]
        def numFullyPaid = countsByFactorAndStatus[factor + "." + Loan.FULLY_PAID_LOAN_STATUS]
        def numTotal = numChargedOff + numFullyPaid
        def value = PCT_FORMAT.format(100 * ((numFullyPaid / numTotal) as double))
        println "$factor,$value,$numTotal"
    }
}
