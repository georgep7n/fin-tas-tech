package org.georgep7n.lendingclub.analyze

import java.util.zip.*;
import au.com.bytecode.opencsv.CSVReader

/**
 *
 */
class LoanSlurper {
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

    private static final def STATE_INDEX = 23
    private static final def TERM_INDEX = 5
    private static final def INT_RATE_INDEX = 6
    private static final def GRADE_INDEX = 8
    private static final def HOME_OWNERSHIP_INDEX = 12
    private static final def LOAN_STATUS_INDEX = 16
    private static final def PURPOSE_INDEX = 20
    private static final def DEBT_TO_INCOME_RATIO = 24
    private static final def DELINQ_LAST_2_YEARS = 25
    private static final def INQUIRIES_IN_LAST_SIX_MONTHS = 27
    private static final def MONTHS_SINCE_LAST_DELINQ = 28

    static List<Loan> filter(List<Loan> loans, LoanFilter loanFilter) {
        def filteredLoans = []
        loans.each { loan ->
            if (loanFilter.include(loan)) {
                filteredLoans.add(loan)
            }
        }
        return filteredLoans
    }

    static List<Loan> slurpCSVFiles() {
        def loans = []
        slurpCSVFile(loans, "LoanStats3a.csv.gz")
        slurpCSVFile(loans, "LoanStats3b.csv.gz")
        slurpCSVFile(loans, "LoanStats3c.csv.gz")
        slurpCSVFile(loans, "LoanStats3d.csv.gz")
        slurpCSVFile(loans, "LoanStats2016Q1.csv.gz")
        slurpCSVFile(loans, "LoanStats2016Q2.csv.gz")
        return loans
    }

    private static void slurpCSVFile(List<Loan> loans, String csvFileName) throws IOException {
        CSVReader reader = new CSVReader(new InputStreamReader(
          new GZIPInputStream(Analyze.class.getResourceAsStream("/lendingclub/" + csvFileName))))
        reader.readNext() // descriptor row
        reader.readNext() // header row
        String[] columns
        int line = 2;
        while ((columns = reader.readNext()) != null) {
            line++;
            def loan = new Loan()
            loan.state = columns[STATE_INDEX].trim()
            loan.term = columns[TERM_INDEX].trim()
            loan.intRate = Double.valueOf(columns[INT_RATE_INDEX].tokenize("%").get(0))
            loan.grade = columns[GRADE_INDEX]
            loan.purpose = columns[PURPOSE_INDEX]
            loan.loan_status = columns[LOAN_STATUS_INDEX]
            loan.home_ownership = columns[HOME_OWNERSHIP_INDEX]
            loan.inq_last_6mths = Integer.valueOf(columns[INQUIRIES_IN_LAST_SIX_MONTHS])
            loan.delinq_2yrs = Integer.valueOf(columns[DELINQ_LAST_2_YEARS])
            try {
                loan.mths_since_last_delinq = Integer.valueOf(columns[MONTHS_SINCE_LAST_DELINQ])
                //println(Arrays.toString(columns))
            } catch (ignored) {}
            loan.dti = Double.valueOf(columns[DEBT_TO_INCOME_RATIO])
            if ((Loan.FULLY_PAID_LOAN_STATUS == loan.loan_status ||
                    Loan.CHARGED_OFF_LOAN_STATUS == loan.loan_status ||
                    Loan.DEFAULT_LOAN_STATUS == loan.loan_status)) {
                if (Loan.DEFAULT_LOAN_STATUS == loan.loan_status)
                    // Count defaults as charge-offs.
                    loan.loan_status = Loan.CHARGED_OFF_LOAN_STATUS
                loans.add(loan)
            }
        }
    }
}
