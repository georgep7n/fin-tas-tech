package org.georgep7n.fintastech.lendingclub

import java.util.zip.*
import java.util.regex.*
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

    private static final NumberFormat PCT_FORMAT = NumberFormat.getNumberInstance()
    static {
        PCT_FORMAT.setMinimumFractionDigits(1);
        PCT_FORMAT.setMaximumFractionDigits(1);
    }

    private static final def LOANS = [] // loan instances created from raw data

    public static void main(String[] args) throws IOException {
        def configFileName = args[0]
        def loansDir = args[1]
        slurpLoans(new File(loansDir))
        def scanner = new Scanner(System.in)
        def okToContinue = true
        while (okToContinue) {
            analyze(configFileName)
            println("Enter 'a' to analyze again, anything else to quit")
            def response = scanner.next()
            if (response != "a") {
                okToContinue = false
            }
        }
    }

    private static void analyze(configFileName) {
        def loanFilters = []
        Binding binding = new Binding()
        binding.setProperty("loanFilters", loanFilters)
        GroovyShell shell = new GroovyShell(binding)
        shell.evaluate(new File(configFileName)) // populates loanFilters

        List<Run> runs = []
        loanFilters.each { loanFilter ->
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
            def loans = filter(LOANS, loanFilter)
            run.numLoans = loans.size()
            println("Analyzing $run.numLoans loans with filter: " + loanFilter.getDescription().toUpperCase())
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
                // TODO if verbose specified in config
                /*
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
                */
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

    static void slurpLoans(loansDir) {
        loansDir.eachFile { file ->
            if (file.getName().endsWith(".csv.gz")) {
                slurpLoansFromCSVFile(file)
            }
        }
    }

    private static void slurpLoansFromCSVFile(csvFileName) throws IOException {
        def start = System.currentTimeMillis()
        def count=0
        CSVReader reader = new CSVReader(new InputStreamReader(
            new GZIPInputStream(new FileInputStream(csvFileName))))
        reader.readNext() // descriptor row
        reader.readNext() // header row
        def loanCSV
        while ((loanCSV = reader.readNext()) != null) {
            def loan = new Loan(loanCSV)
            // only include fully paid and charged off loans in the subsequent analysis.
            if ((FULLY_PAID_LOAN_STATUS == loan.attrs[LOAN_STATUS_INDEX] ||
                    CHARGED_OFF_LOAN_STATUS == loan.attrs[LOAN_STATUS_INDEX] ||
                    DEFAULT_LOAN_STATUS == loan.attrs[LOAN_STATUS_INDEX])) {
                LOANS.add(loan)
            }
            count++
        }
        def stop = System.currentTimeMillis()
        println (count + " loans slurped in " + (stop - start) + " millis")
    }
}
