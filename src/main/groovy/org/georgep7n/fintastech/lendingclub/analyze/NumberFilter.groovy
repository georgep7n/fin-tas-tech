package org.georgep7n.lendingclub.analyze

/**
 */
abstract class NumberFilter implements LoanFilter {

    double value

    void set(double value) {
        this.value = value
    }

}
