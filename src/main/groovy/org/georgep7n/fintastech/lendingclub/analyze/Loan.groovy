package org.georgep7n.fintastech.lendingclub.analyze

/**
 *
 */
class Loan {
    static def FULLY_PAID_LOAN_STATUS = "Fully Paid"
    static def CHARGED_OFF_LOAN_STATUS = "Charged Off"
    static def DEFAULT_LOAN_STATUS = "Default"

    private def desc
    private def state
    private def grade
    private def intRate
    private def term
    private def purpose
    private def loan_status
    private def inq_last_6mths
    private def mths_since_last_delinq
    private def delinq_2yrs
    private def dti
    private def home_ownership
    @Override public String toString() { return state + " " + purpose + " " + grade + " " + term + " " + loan_status }
}
