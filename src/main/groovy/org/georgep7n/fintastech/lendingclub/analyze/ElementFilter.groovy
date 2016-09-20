package org.georgep7n.fintastech.lendingclub.analyze

/**
 */
final class ElementFilter implements LoanFilter {

    private String propertyName
    final List<String> elements = []

    ElementFilter(String propertyName) {
        this.propertyName = propertyName
    }

    ElementFilter add(String element) {
        elements.add(element)
        this
    }

    @Override boolean include(Loan loan) {
        elements.contains(loan[propertyName])
    }

    @Override String getDescription() {
        elements.toString()
    }
}
