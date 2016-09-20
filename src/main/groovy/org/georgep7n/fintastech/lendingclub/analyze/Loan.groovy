package org.georgep7n.fintastech.lendingclub.analyze

/**
 *
 */
class Loan {
    static def FULLY_PAID_LOAN_STATUS = "Fully Paid"
    static def CHARGED_OFF_LOAN_STATUS = "Charged Off"
    static def DEFAULT_LOAN_STATUS = "Default"

    def desc
    def state
    def grade
    def intRate
    def term
    def purpose
    def loan_status
    def inq_last_6mths
    def mths_since_last_delinq
    def delinq_2yrs
    def dti
    def home_ownership
    @Override public String toString() { return state + " " + purpose + " " + grade + " " + term + " " + loan_status }
}
