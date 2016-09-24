package org.georgep7n.fintastech.lendingclub

/**
 * Represents a loan with storage for the attributes and indexes
 * which directly correspond to LendingClub's CSV format for loans.
 */
public final class Loan {

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

    static final def FULLY_PAID_LOAN_STATUS = "Fully Paid"
    static final def CHARGED_OFF_LOAN_STATUS = "Charged Off"
    static final def DEFAULT_LOAN_STATUS = "Default"

    final def attrs = []

    Loan() {}
    Loan(loanCSV) {
        // copy everything as is from csv after trimming string
        for (int i = 0; i<loanCSV.length; i++) {
            attrs[i] = loanCSV[i].trim()
        }
        // change numeric loan attrs to numbers as needed, etc.
        attrs[INT_RATE_INDEX] = Double.valueOf(
            attrs[INT_RATE_INDEX].tokenize("%").get(0))
        attrs[INQUIRIES_IN_LAST_SIX_MONTHS] = Double.valueOf(
            attrs[INQUIRIES_IN_LAST_SIX_MONTHS])
        attrs[DEBT_TO_INCOME_RATIO] = Double.valueOf(
            attrs[DEBT_TO_INCOME_RATIO])

        // modify attrs as needed
        if (DEFAULT_LOAN_STATUS == attrs[LOAN_STATUS_INDEX]) {
            // Count defaults as charge-offs.
            attrs[LOAN_STATUS_INDEX] = CHARGED_OFF_LOAN_STATUS
        }
    }
}
