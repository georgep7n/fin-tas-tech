package org.georgep7n.fintastech.lendingclub

import java.util.zip.*;
import au.com.bytecode.opencsv.CSVReader
import java.text.NumberFormat
import static org.georgep7n.fintastech.lendingclub.Loan.*

/**
 *
 */
public final class Analyze {

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

    private static def LOAN_FILTERS = []

    public static void main(String[] args) throws IOException {
        def configFileName = args[0]
        GroovyShell shell = new GroovyShell()
        shell.evaluate(new File(configFileName)) // populates LOAN_FILTERS

        def allLoans = slurpCSVFiles()
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
                    purposes.add(loan.attrs[PURPOSE_INDEX])
                    statuses.add(loan.attrs[LOAN_STATUS_INDEX])
                    grades.add(loan.attrs[GRADE_INDEX])
                    states.add(loan.attrs[STATE_INDEX])
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
                    def count = countsByPurpose[loan.attrs[PURPOSE_INDEX]]
                    countsByPurpose[loan.attrs[PURPOSE_INDEX]] = count + 1
                    count = countsByStatus[loan.attrs[LOAN_STATUS_INDEX]]
                    countsByStatus[loan.attrs[LOAN_STATUS_INDEX]] = count + 1
                    count = countsByPurposeAndStatus[loan.attrs[PURPOSE_INDEX] + "." + loan.attrs[LOAN_STATUS_INDEX]]
                    countsByPurposeAndStatus[loan.attrs[PURPOSE_INDEX] + "." + loan.attrs[LOAN_STATUS_INDEX]] = count + 1
                    count = countsByGradeAndStatus[loan.attrs[GRADE_INDEX] + "." + loan.attrs[LOAN_STATUS_INDEX]]
                    countsByGradeAndStatus[loan.attrs[GRADE_INDEX] + "." + loan.attrs[LOAN_STATUS_INDEX]] = count + 1
                    count = countsByStateAndStatus[loan.attrs[STATE_INDEX] + "." + loan.attrs[LOAN_STATUS_INDEX]]
                    countsByStateAndStatus[loan.attrs[STATE_INDEX] + "." + loan.attrs[LOAN_STATUS_INDEX]] = count + 1
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
          new GZIPInputStream(Analyze.class.getResourceAsStream("/lendingclub/" + csvFileName))))
        reader.readNext() // descriptor row
        reader.readNext() // header row
        def loanCSV
        while ((loanCSV = reader.readNext()) != null) {
            def loan = new Loan()
            // copy everything as is from csv after trimming string
            for (int i = 0; i<loanCSV.length; i++) {
                loan.attrs[i] = loanCSV[i].trim()
            }
            // change numeric loan attrs to numbers as needed, etc.
            loan.attrs[INT_RATE_INDEX] = Double.valueOf(
                loan.attrs[INT_RATE_INDEX].tokenize("%").get(0))
            loan.attrs[INQUIRIES_IN_LAST_SIX_MONTHS] = Double.valueOf(
                loan.attrs[INQUIRIES_IN_LAST_SIX_MONTHS])
            loan.attrs[DEBT_TO_INCOME_RATIO] = Double.valueOf(
                loan.attrs[DEBT_TO_INCOME_RATIO])

            // only include fully paid and charged off loans in the subsequent analysis.
            if ((FULLY_PAID_LOAN_STATUS == loan.attrs[LOAN_STATUS_INDEX] ||
                    CHARGED_OFF_LOAN_STATUS == loan.attrs[LOAN_STATUS_INDEX] ||
                    DEFAULT_LOAN_STATUS == loan.attrs[LOAN_STATUS_INDEX])) {
                if (DEFAULT_LOAN_STATUS == loan.attrs[LOAN_STATUS_INDEX]) {
                    // Count defaults as charge-offs.
                    loan.attrs[LOAN_STATUS_INDEX] = CHARGED_OFF_LOAN_STATUS
                }
                loans.add(loan)
            }
        }
    }
}
