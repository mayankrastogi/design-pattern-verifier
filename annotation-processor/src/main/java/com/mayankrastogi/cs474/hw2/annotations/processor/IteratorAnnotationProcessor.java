package com.mayankrastogi.cs474.hw2.annotations.processor;

import com.mayankrastogi.cs474.hw2.annotations.Iterator;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.stream.Stream;

public class IteratorAnnotationProcessor extends AbstractAnnotationProcessor {

    private static final String ITERATOR_ANNOTATION_NAME = "@" + Iterator.class.getName();
    private static final String CURRENT_ITEM_ANNOTATION_NAME = "@" + Iterator.CurrentItem.class.getName();
    private static final String IS_DONE_ANNOTATION_NAME = "@" + Iterator.IsDone.class.getName();
    private static final String NEXT_ITEM_ANNOTATION_NAME = "@" + Iterator.NextItem.class.getName();

    private Set<? extends Element> iterators;

    @Override
    protected Stream<Class> getSupportedAnnotations() {
        return Stream.of(Iterator.class, Iterator.CurrentItem.class, Iterator.IsDone.class, Iterator.NextItem.class);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        debug(String.format("process(annotations: %s, roundEnv: %s)", annotations.toString(), roundEnv.toString()));

        iterators = roundEnv.getElementsAnnotatedWith(Iterator.class);
        debug("iterators: " + iterators);
        var currentItems = roundEnv.getElementsAnnotatedWith(Iterator.CurrentItem.class);
        debug("currentItems: " + currentItems);
        var isDones = roundEnv.getElementsAnnotatedWith(Iterator.IsDone.class);
        debug("isDones: " + isDones);
        var nextItems = roundEnv.getElementsAnnotatedWith(Iterator.NextItem.class);
        debug("nextItems: " + nextItems);

        note("Processing elements annotated with " + ITERATOR_ANNOTATION_NAME);
        for (var iteratorElement : iterators) {
            debug("Processing iteratorElement", iteratorElement);

            var success = assertIteratorElementIsValid(iteratorElement) &&
                    assertIteratorElementContainsOnlyOneMethodAnnotatedWith(Iterator.CurrentItem.class, iteratorElement) &&
                    assertIteratorElementContainsOnlyOneMethodAnnotatedWith(Iterator.IsDone.class, iteratorElement) &&
                    assertIteratorElementContainsOnlyOneMethodAnnotatedWith(Iterator.NextItem.class, iteratorElement);
            debug("Processing successful: " + success);

            if (!success) return false;
        }

        note("Processing elements annotated with " + CURRENT_ITEM_ANNOTATION_NAME);
        for (var currentItemMethod : currentItems) {
            debug("Processing currentItemMethod", currentItemMethod);

            var success = assertMethodIsEnclosedWithinIterator(currentItemMethod, Iterator.CurrentItem.class) &&
                    assertMethodReturnsTypeSpecifiedInIteratorAnnotationValue(currentItemMethod, Iterator.CurrentItem.class) &&
                    assertMethodTakesNoParameters(currentItemMethod, Iterator.CurrentItem.class);
            debug("Processing successful: " + success);

            if (!success) return false;
        }

        note("Processing elements annotated with " + IS_DONE_ANNOTATION_NAME);
        for (var isDoneMethod : isDones) {
            debug("Processing isDoneMethod", isDoneMethod);

            var success = assertMethodIsEnclosedWithinIterator(isDoneMethod, Iterator.IsDone.class) &&
                    assertIsDoneMethodReturnsBooleanType(isDoneMethod) &&
                    assertMethodTakesNoParameters(isDoneMethod, Iterator.IsDone.class) &&
                    warnIfMethodIsPrivate(isDoneMethod, Iterator.IsDone.class);
            debug("Processing successful: " + success);

            if (!success) return false;
        }

        note("Processing elements annotated with " + NEXT_ITEM_ANNOTATION_NAME);
        for (var nextItemMethod : nextItems) {
            debug("Processing nextItemMethod", nextItemMethod);

            var success = assertMethodIsEnclosedWithinIterator(nextItemMethod, Iterator.NextItem.class) &&
                    assertMethodReturnsTypeSpecifiedInIteratorAnnotationValue(nextItemMethod, Iterator.NextItem.class) &&
                    assertMethodTakesNoParameters(nextItemMethod, Iterator.NextItem.class) &&
                    warnIfMethodIsPrivate(nextItemMethod, Iterator.NextItem.class);
            debug("Processing successful: " + success);

            if (!success) return false;
        }

        return true;
    }

    private boolean assertIteratorElementIsValid(Element element) {
        debug("assertIteratorElementIsValid...");

        if (element.getKind() != ElementKind.CLASS) {
            error("Only classes can be annotated with " + ITERATOR_ANNOTATION_NAME, element);
            return false;
        }
        debug("assertIteratorElementIsValid: true");
        return true;
    }

    private boolean assertIteratorElementContainsOnlyOneMethodAnnotatedWith(Class<? extends Annotation> annotation, Element enclosingElement) {
        debug("assertIteratorElementContainsOnlyOneMethodAnnotatedWith...");

        var count = enclosingElement
                .getEnclosedElements()
                .stream()
                .filter(e -> e.getAnnotation(annotation) != null)
                .count();

        debug("Found " + count + " element(s) annotated with @" + annotation.getName() + " within iterator");
        if (count == 1) {
            debug("assertIteratorElementContainsOnlyOneMethodAnnotatedWith: true");
            return true;
        }

        var quantity = count < 1 ? "a" : "only one";
        error("An iterator must have " + quantity + " method annotated with @" + annotation.getName(), enclosingElement);
        return false;
    }

    private boolean assertMethodIsEnclosedWithinIterator(Element element, Class<? extends Annotation> methodAnnotation) {
        debug("assertMethodIsEnclosedWithinIterator...");

        var enclosingElement = element.getEnclosingElement();
        // Since @Iterator.X's target is METHOD, it must always be enclosed in some element
        assert enclosingElement != null;

        if (iterators.contains(enclosingElement)) {
            debug(methodAnnotation.getSimpleName() + " is enclosed within an iterator");
            return true;
        } else {
            error("A method annotated with @" + methodAnnotation.getName() + " must be part of a class" +
                    " annotated with " + ITERATOR_ANNOTATION_NAME, element);
            return false;
        }
    }

    private boolean assertMethodReturnsTypeSpecifiedInIteratorAnnotationValue(Element element, Class<? extends Annotation> methodAnnotation) {
        debug("assertMethodReturnsTypeSpecifiedInIteratorAnnotationValue...");

        var enclosingElement = element.getEnclosingElement();
        // Since @Iterator.X's target is METHOD, it must always be enclosed in some element
        assert enclosingElement != null;

        var iteratorAnnotationValue = getAnnotationValueAsType(enclosingElement, Iterator.class);
        debug("iteratorAnnotationValue: " + iteratorAnnotationValue);

        var methodReturnType = ((ExecutableElement) element).getReturnType();

        if (typeUtils.isAssignable(iteratorAnnotationValue, methodReturnType)) {
            debug("The return type of the method annotated with @" + methodAnnotation.getName() + " is assignable to" +
                    " the type specified as value on its enclosing " + ITERATOR_ANNOTATION_NAME);
            return true;
        } else {
            error(String.format(
                    "The return type of the method annotated with @%s is `%s` but the iterator expects it to be `%s`.",
                    methodAnnotation.getName(), methodReturnType, iteratorAnnotationValue), element);
            return false;
        }
    }

    private boolean assertMethodTakesNoParameters(Element element, Class<? extends Annotation> methodAnnotation) {
        debug("assertMethodTakesNoParameters...");

        if (((ExecutableElement) element).getParameters().isEmpty()) {
            debug("assertMethodTakesNoParameters: true");
            return true;
        }
        error("A method annotated with @" + methodAnnotation.getName() + " must not take any parameters", element);
        return false;
    }

    private boolean assertIsDoneMethodReturnsBooleanType(Element element) {
        debug("assertIsDoneMethodReturnsBooleanType...");

        var methodReturnType = ((ExecutableElement) element).getReturnType();
        var expectedReturnType = typeUtils.getPrimitiveType(TypeKind.BOOLEAN);
        if (typeUtils.isAssignable(methodReturnType, expectedReturnType)) {
            debug("assertIsDoneMethodReturnsBooleanType: true");
            return true;
        }
        error(String.format(
                "The return type of the method annotated with %s is `%s` but the iterator expects it to be `%s`.",
                IS_DONE_ANNOTATION_NAME, methodReturnType, expectedReturnType), element);
        return false;
    }

    private boolean warnIfMethodIsPrivate(Element element, Class<? extends Annotation> methodAnnotation) {
        debug("warnIfMethodIsPrivate...");

        var treatWarningsAsErrors = shouldTreatWarningsAsErrors(element);

        var operationSuccessful = withTreatMandatoryWarningsAsErrors(treatWarningsAsErrors, () -> {
            var isPrivate = element.getModifiers().contains(Modifier.PRIVATE);
            if (isPrivate) {
                warning("A method annotated with @" + methodAnnotation.getName() + " was found to be private", element);
                return false;
            }
            debug("isPrivate: false");
            return true;
        });
        debug("operationSuccessful: " + operationSuccessful);

        return operationSuccessful || !treatWarningsAsErrors;
    }

    private boolean shouldTreatWarningsAsErrors(Element element) {
        debug("shouldTreatWarningsAsErrors...");

        var enclosingElement = element.getEnclosingElement();
        // Since @Iterator.X's target is METHOD, it must always be enclosed in some element
        assert enclosingElement != null;

        var iteratorAnnotation = enclosingElement.getAnnotation(Iterator.class);
        // Enclosing element will always be annotated with @Iterator since we test this before invoking this method
        assert iteratorAnnotation != null;

        var result = iteratorAnnotation.treatWarningsAsErrors();
        debug("shouldTreatWarningsAsErrors: " + result);
        return result;
    }
}
