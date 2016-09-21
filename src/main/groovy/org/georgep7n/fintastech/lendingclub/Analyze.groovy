package org.georgep7n.fintastech.lendingclub

import java.util.zip.*;
import au.com.bytecode.opencsv.CSVReader
import java.text.NumberFormat
import org.georgep7n.fintastech.lendingclub.filter.*

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

    static def LOAN_FILTERS = []
    static {
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
            { loan -> toNum(loan, INT_RATE_INDEX) >= 7 }, "interest rate >= 7"))
        LOAN_FILTERS.add(new ClosureFilter(
            { loan -> toNum(loan, INQUIRIES_IN_LAST_SIX_MONTHS) <= 0 }, "inquiries in the last 6 months <= 0"))
        LOAN_FILTERS.add(new ClosureFilter(
            { loan -> toNum(loan, DEBT_TO_INCOME_RATIO) <= 20 }, "debt to income ratio <= 20"))
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
    }

    public static void main(String[] args) throws IOException {
        def allLoans = slurpCSVFiles()
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
            def loans = filter(allLoans, loanFilter)
            run.numLoans = loans.size()
            println("$run.numLoans loans to analyze")
            if (run.numLoans < 500) {
                println("not enough loans to analyze")
            } else {
                runs.add(run)
                loans.each { loan ->
                    purposes.add(loan[PURPOSE_INDEX])
                    statuses.add(loan[LOAN_STATUS_INDEX])
                    grades.add(loan[GRADE_INDEX])
                    states.add(loan[STATE_INDEX])
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
                    def count = countsByPurpose[loan[PURPOSE_INDEX]]
                    countsByPurpose[loan[PURPOSE_INDEX]] = count + 1
                    count = countsByStatus[loan[LOAN_STATUS_INDEX]]
                    countsByStatus[loan[LOAN_STATUS_INDEX]] = count + 1
                    count = countsByPurposeAndStatus[loan[PURPOSE_INDEX] + "." + loan[LOAN_STATUS_INDEX]]
                    countsByPurposeAndStatus[loan[PURPOSE_INDEX] + "." + loan[LOAN_STATUS_INDEX]] = count + 1
                    count = countsByGradeAndStatus[loan[GRADE_INDEX] + "." + loan[LOAN_STATUS_INDEX]]
                    countsByGradeAndStatus[loan[GRADE_INDEX] + "." + loan[LOAN_STATUS_INDEX]] = count + 1
                    count = countsByStateAndStatus[loan[STATE_INDEX] + "." + loan[LOAN_STATUS_INDEX]]
                    countsByStateAndStatus[loan[STATE_INDEX] + "." + loan[LOAN_STATUS_INDEX]] = count + 1
                }

                def numChargedOff = countsByStatus[CHARGED_OFF_LOAN_STATUS]
                def numFullyPaid = countsByStatus[FULLY_PAID_LOAN_STATUS]
                def numTotal = numChargedOff + numFullyPaid
                run.score = 100 * ((numFullyPaid / numTotal) as double)
                println("Overall " + FULLY_PAID_LOAN_STATUS + ": " + PCT_FORMAT.format(run.score) + "%")
                println()
                println("Fully Paid by Purpose")
                purposes.each { purpose ->
                    statsByFactorAndStatus(countsByPurposeAndStatus, (String) purpose)
                }
                println()
                println("Fully Paid by Grade")
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
        def numChargedOff = countsByFactorAndStatus[factor + "." + CHARGED_OFF_LOAN_STATUS]
        def numFullyPaid = countsByFactorAndStatus[factor + "." + FULLY_PAID_LOAN_STATUS]
        def numTotal = numChargedOff + numFullyPaid
        def value = PCT_FORMAT.format(100 * ((numFullyPaid / numTotal) as double))
        println "$factor,$value,$numTotal"
    }

    static Object filter(loans, loanFilter) {
        def filteredLoans = []
        loans.each { loan ->
            if (loanFilter.include(loan)) {
                filteredLoans.add(loan)
            }
        }
        return filteredLoans
    }

    /**
     0 = id
     1 = member_id
     2 = loan_amnt
     3 = funded_amnt
     4 = funded_amnt_inv
     5 = term
     6 = int_rate
     7 = installment
     8 = grade
     9 = sub_grade
     10 = emp_title
     11 = emp_length
     12 = home_ownership
     13 = annual_inc
     14 = verification_status
     15 = issue_d
     16 = loan_status
     17 = pymnt_plan
     18 = url
     19 = desc
     20 = purpose
     21 = title
     22 = zip_code
     23 = addr_state
     24 = inqLast6Months
     25 = delinq_2yrs
     26 = earliest_cr_line
     27 = inq_last_6mths
     28 = mths_since_last_delinq
     29 = mths_since_last_record
     30 = open_acc
     31 = pub_rec
     32 = revol_bal
     33 = revol_util
     34 = total_acc
     35 = initial_list_status
     36 = out_prncp
     37 = out_prncp_inv
     38 = total_pymnt
     39 = total_pymnt_inv
     40 = total_rec_prncp
     41 = total_rec_int
     42 = total_rec_late_fee
     43 = recoveries
     44 = collection_recovery_fee
     45 = last_pymnt_d
     46 = last_pymnt_amnt
     47 = next_pymnt_d
     48 = last_credit_pull_d
     49 = collections_12_mths_ex_med
     50 = mths_since_last_major_derog
     51 = policy_code
     52 = application_type
     53 = annual_inc_joint
     54 = dti_joint
     55 = verification_status_joint
     */

    static final def DESCRIPTION_INDEX = 19
    static final def STATE_INDEX = 23
    static final def TERM_INDEX = 5
    static final def INT_RATE_INDEX = 6
    static final def GRADE_INDEX = 8
    static final def HOME_OWNERSHIP_INDEX = 12
    static final def LOAN_STATUS_INDEX = 16
    static final def PURPOSE_INDEX = 20
    static final def DEBT_TO_INCOME_RATIO = 24
    static final def DELINQ_LAST_2_YEARS = 25
    static final def INQUIRIES_IN_LAST_SIX_MONTHS = 27
    static final def MONTHS_SINCE_LAST_DELINQ = 28

    static def FULLY_PAID_LOAN_STATUS = "Fully Paid"
    static def CHARGED_OFF_LOAN_STATUS = "Charged Off"
    static def DEFAULT_LOAN_STATUS = "Default"

    static Object slurpCSVFiles() {
        def loans = []
        slurpCSVFile(loans, "LoanStats3a.csv.gz")
        slurpCSVFile(loans, "LoanStats3b.csv.gz")
        slurpCSVFile(loans, "LoanStats3c.csv.gz")
        slurpCSVFile(loans, "LoanStats3d.csv.gz")
        slurpCSVFile(loans, "LoanStats2016Q1.csv.gz")
        slurpCSVFile(loans, "LoanStats2016Q2.csv.gz")
        return loans
    }

    private static void slurpCSVFile(loans, csvFileName) throws IOException {
        CSVReader reader = new CSVReader(new InputStreamReader(
          new GZIPInputStream(Main.class.getResourceAsStream("/lendingclub/" + csvFileName))))
        reader.readNext() // descriptor row
        reader.readNext() // header row
        def loan
        while ((loan = reader.readNext()) != null) {
            for (int i = 0; i<loan.length; i++) {
                loan[i] = loan[i].trim()
            }
            if ((FULLY_PAID_LOAN_STATUS == loan[LOAN_STATUS_INDEX] ||
                    CHARGED_OFF_LOAN_STATUS == loan[LOAN_STATUS_INDEX] ||
                    DEFAULT_LOAN_STATUS == loan[LOAN_STATUS_INDEX])) {
                if (DEFAULT_LOAN_STATUS == loan[LOAN_STATUS_INDEX]) {
                    // Count defaults as charge-offs.
                    loan[LOAN_STATUS_INDEX] = CHARGED_OFF_LOAN_STATUS
                }
                loans.add(loan)
            }
        }
    }

    static Double toNum(loan, propertyIndex) {
        def value = loan[propertyIndex]
        switch (propertyIndex) {
            case INT_RATE_INDEX:
                value = value.tokenize("%").get(0)
                break
        }
        Double.valueOf(value)
    }
}
