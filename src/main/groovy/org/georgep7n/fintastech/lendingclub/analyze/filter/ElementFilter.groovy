package org.georgep7n.fintastech.lendingclub.analyze.filter
import org.georgep7n.fintastech.lendingclub.analyze.*

/**
 */
final class ElementFilter implements LoanFilter {

    private def propertyIndex
    final List<String> elements = []

    ElementFilter(propertyIndex) {
        this.propertyIndex = propertyIndex
    }

    ElementFilter add(String element) {
        elements.add(element)
        this
    }

    @Override boolean include(loan) {
        elements.contains(loan[propertyIndex])
    }

    @Override String getDescription() {
        elements.toString()
    }
}
