package com.mayankrastogi.cs474.hw2.annotations.processor;

import com.mayankrastogi.cs474.hw2.annotations.IterableAggregate;
import com.mayankrastogi.cs474.hw2.annotations.Iterator;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.Set;
import java.util.stream.Stream;

public class IterableAggregateAnnotationProcessor extends AbstractAnnotationProcessor {

    private static final String ITERABLE_AGGREGATE_ANNOTATION_NAME = "@" + IterableAggregate.class.getName();
    private static final String ITERATOR_FACTORY_ANNOTATION_NAME = "@" + IterableAggregate.IteratorFactory.class.getName();

    private Set<? extends Element> iterableAggregates;
    private Set<? extends Element> iteratorFactories;

    @Override
    protected Stream<Class> getSupportedAnnotations() {
        return Stream.of(IterableAggregate.class, IterableAggregate.IteratorFactory.class);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        debug(String.format("process(annotations: %s, roundEnv: %s)", annotations.toString(), roundEnv.toString()));

        iterableAggregates = roundEnv.getElementsAnnotatedWith(IterableAggregate.class);
        debug("iterableAggregates: " + iterableAggregates);
        iteratorFactories = roundEnv.getElementsAnnotatedWith(IterableAggregate.IteratorFactory.class);
        debug("iteratorFactories: " + iteratorFactories);

        note("Processing elements annotated with " + ITERABLE_AGGREGATE_ANNOTATION_NAME);
        for (var iterableAggregateElement : iterableAggregates) {
            debug("Processing iterableAggregateElement", iterableAggregateElement);

            var success = assertIterableAggregateElementIsAppliedOnClass(iterableAggregateElement) &&
                    assertIterableAggregateAnnotationValueIsAnnotatedWithIterator(iterableAggregateElement, roundEnv) &&
                    assertIterableAggregateElementContainsAtLeastOneIteratorFactory(iterableAggregateElement);
            debug("Processing successful: " + success);

            if (!success) return false;
        }

        note("Processing elements annotated with " + ITERATOR_FACTORY_ANNOTATION_NAME);
        for (var iteratorFactoryMethod : iteratorFactories) {
            debug("Processing iteratorFactoryMethod", iteratorFactoryMethod);

            var success = assertIteratorFactoryMethodIsEnclosedWithinIterableAggregate(iteratorFactoryMethod) &&
                    assertIteratorFactoryMethodReturnsTypeSpecifiedInIterableAggregateAnnotationValue(iteratorFactoryMethod);
            debug("Processing successful: " + success);

            if (!success) return false;
        }

        return true;
    }

    private boolean assertIterableAggregateElementIsAppliedOnClass(Element element) {
        debug("assertIterableAggregateElementIsAppliedOnClass...");

        if (element.getKind() != ElementKind.CLASS) {
            error("Only classes can be annotated with " + ITERABLE_AGGREGATE_ANNOTATION_NAME, element);
            return false;
        } else {
            debug("assertIterableAggregateElementIsAppliedOnClass: true");
            return true;
        }
    }

    private boolean assertIterableAggregateAnnotationValueIsAnnotatedWithIterator(Element element, RoundEnvironment roundEnvironment) {
        debug("assertIterableAggregateAnnotationValueIsAnnotatedWithIterator...");

        var annotationValue = typeUtils.asElement(getAnnotationValueAsType(element, IterableAggregate.class));
        var iterators = roundEnvironment.getElementsAnnotatedWith(Iterator.class);

        if (!iterators.contains(annotationValue)) {
            error(String.format(
                    "%s is not an iterator. Value of %s must be a class annotated with @%s.",
                    annotationValue, ITERABLE_AGGREGATE_ANNOTATION_NAME, Iterator.class.getName()));
            return false;
        }
        debug("assertIterableAggregateAnnotationValueIsAnnotatedWithIterator: true");
        return true;
    }

    private boolean assertIterableAggregateElementContainsAtLeastOneIteratorFactory(Element element) {
        debug("assertIterableAggregateElementContainsAtLeastOneIteratorFactory...");

        var count = element
                .getEnclosedElements()
                .stream()
                .filter(iteratorFactories::contains)
                .count();

        if (count > 0) {
            debug("Found " + count + " element(s) annotated with " + ITERATOR_FACTORY_ANNOTATION_NAME + " within iterable aggregate");
            return true;
        } else {
            error("An iterable aggregate must have at least one method annotated with " + ITERATOR_FACTORY_ANNOTATION_NAME, element);
            return false;
        }
    }

    private boolean assertIteratorFactoryMethodIsEnclosedWithinIterableAggregate(Element element) {
        debug("assertIteratorFactoryMethodIsEnclosedWithinIterableAggregate...");

        var enclosingElement = element.getEnclosingElement();
        // Since @IteratorFactory's target is METHOD, it must always be enclosed in some element
        assert enclosingElement != null;

        if (iterableAggregates.contains(enclosingElement)) {
            debug("Iterable factory method is enclosed within an iterable aggregate");
            return true;
        } else {
            error("An iterator factory method must be part of a class annotated with " + ITERABLE_AGGREGATE_ANNOTATION_NAME, element);
            return false;
        }
    }

    private boolean assertIteratorFactoryMethodReturnsTypeSpecifiedInIterableAggregateAnnotationValue(Element element) {
        debug("assertIteratorFactoryMethodReturnsTypeSpecifiedInIterableAggregateAnnotationValue...");

        var enclosingElement = element.getEnclosingElement();
        // Since @IteratorFactory's target is METHOD, it must always be enclosed in some element
        assert enclosingElement != null;

        var iterableAggregateAnnotationValue = getAnnotationValueAsType(enclosingElement, IterableAggregate.class);
        debug("iterableAggregateAnnotationValue: " + iterableAggregateAnnotationValue);

        var iteratorFactoryMethodReturnType = ((ExecutableElement) element).getReturnType();

        if (typeUtils.isAssignable(iterableAggregateAnnotationValue, iteratorFactoryMethodReturnType)) {
            debug("The return type of the iterator factory method is assignable to the type specified as value on its enclosing " + ITERABLE_AGGREGATE_ANNOTATION_NAME);
            return true;
        } else {
            error(String.format(
                    "The return type of the iterator factory method is `%s` but the iterable aggregate expects it to be `%s`.",
                    iteratorFactoryMethodReturnType, iterableAggregateAnnotationValue), element);
            return false;
        }
    }
}
