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
    static final def DEBT_TO_INCOME_RATIO_INDEX = 24
    static final def DELINQ_LAST_2_YEARS_INDEX = 25
    static final def INQUIRIES_IN_LAST_SIX_MONTHS_INDEX = 27
    static final def MONTHS_SINCE_LAST_DELINQ_INDEX = 28

    static final def FULLY_PAID_LOAN_STATUS = "Fully Paid"
    static final def CHARGED_OFF_LOAN_STATUS = "Charged Off"
    static final def DEFAULT_LOAN_STATUS = "Default"

    static final def ATTR_TYPE_STRING = "string"
    static final def ATTR_TYPE_NUMBER = "number"
    static final class AttrDescriptor {
        def index
        def type
        def parser
        AttrDescriptor(index, type) { this.index = index; this.type = type }
    }

    static final def attrDescriptors = []
    static {
        AttrDescriptor desc = new AttrDescriptor(INT_RATE_INDEX, ATTR_TYPE_NUMBER)
        desc.parser = { value -> value.tokenize("%").get(0) }
        attrDescriptors[INT_RATE_INDEX] = desc
        desc = new AttrDescriptor(INQUIRIES_IN_LAST_SIX_MONTHS_INDEX, ATTR_TYPE_NUMBER)
        attrDescriptors[INQUIRIES_IN_LAST_SIX_MONTHS_INDEX] = desc
        desc = new AttrDescriptor(DEBT_TO_INCOME_RATIO_INDEX, ATTR_TYPE_NUMBER)
        attrDescriptors[DEBT_TO_INCOME_RATIO_INDEX] = desc
        desc = new AttrDescriptor(LOAN_STATUS_INDEX, ATTR_TYPE_STRING)
        desc.parser = { value -> value == DEFAULT_LOAN_STATUS ? CHARGED_OFF_LOAN_STATUS : value }
        attrDescriptors[LOAN_STATUS_INDEX] = desc
    }
    final def attrs = []

    Loan() {}
    Loan(loanCSV) {
        for (int i = 0; i<loanCSV.length; i++) {
            // copy attribute as-is after trimming
            attrs[i] = loanCSV[i].trim()
            // call csv parser for attr if there is one
            if (attrDescriptors[i] != null && attrDescriptors[i].parser != null) {
                attrs[i] = attrDescriptors[i].parser.call(attrs[i])
            }
            // convert to number if needed
            if (attrDescriptors[i] != null && attrDescriptors[i].type == ATTR_TYPE_NUMBER) {
                attrs[i] = Double.valueOf(attrs[i])
            }
        }
    }
}
