package org.georgep7n.fintastech.lendingclub.analyze

/**
 */
abstract class ElementFilter implements LoanFilter {

    final List<String> elements = []

    final ElementFilter add(String element) {
        elements.add(element)
        this
    }

    final @Override String getDescription() {
        elements.toString()
    }
}
