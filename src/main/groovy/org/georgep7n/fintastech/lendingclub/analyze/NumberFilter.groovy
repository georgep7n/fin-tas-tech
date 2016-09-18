package org.georgep7n.fintastech.lendingclub.analyze

/**
 */
abstract class NumberFilter implements LoanFilter {

    double value

    void set(double value) {
        this.value = value
    }

}
