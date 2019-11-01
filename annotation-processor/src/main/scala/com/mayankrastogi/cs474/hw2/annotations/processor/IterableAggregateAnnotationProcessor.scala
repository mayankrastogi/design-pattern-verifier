package com.mayankrastogi.cs474.hw2.annotations.processor

import java.lang.annotation.Annotation
import java.util

import com.mayankrastogi.cs474.hw2.annotations.{IterableAggregate, Iterator}
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.{Element, ElementKind, ExecutableElement, TypeElement}

import scala.jdk.CollectionConverters._

class IterableAggregateAnnotationProcessor extends AbstractAnnotationProcessor {

  private val ITERABLE_AGGREGATE_ANNOTATION_NAME = "@" + classOf[IterableAggregate].getName
  private val ITERATOR_FACTORY_ANNOTATION_NAME = "@" + classOf[IterableAggregate.IteratorFactory].getName

  private var iterableAggregates: Set[Element] = _
  private var iteratorFactories: Set[Element] = _

  override def getSupportedAnnotations: List[Class[_ <: Annotation]] = List(
    classOf[IterableAggregate],
    classOf[IterableAggregate.IteratorFactory]
  )

  override def process(annotations: util.Set[_ <: TypeElement], roundEnv: RoundEnvironment): Boolean = {
    debug(String.format("process(annotations: %s, roundEnv: %s)", annotations.toString, roundEnv.toString))

    iterableAggregates = roundEnv.getElementsAnnotatedWith(classOf[IterableAggregate]).asScala.toSet
    debug("iterableAggregates: " + iterableAggregates)

    iteratorFactories = roundEnv.getElementsAnnotatedWith(classOf[IterableAggregate.IteratorFactory]).asScala.toSet
    debug("iteratorFactories: " + iteratorFactories)

    note("Processing elements annotated with " + ITERABLE_AGGREGATE_ANNOTATION_NAME)

    iterableAggregates.foreach { iterableAggregateElement =>
      debug("Processing iterableAggregateElement", iterableAggregateElement)

      val success =
        assertIterableAggregateElementIsAppliedOnClass(iterableAggregateElement) &&
          assertIterableAggregateAnnotationValueIsAnnotatedWithIterator(iterableAggregateElement, roundEnv) &&
          assertIterableAggregateElementContainsAtLeastOneIteratorFactory(iterableAggregateElement)

      debug("Processing successful: " + success)
      if (!success) return false
    }

    note("Processing elements annotated with " + ITERATOR_FACTORY_ANNOTATION_NAME)

    iteratorFactories.foreach { iteratorFactoryMethod =>
      debug("Processing iteratorFactoryMethod", iteratorFactoryMethod)

      val success =
        assertIteratorFactoryMethodIsEnclosedWithinIterableAggregate(iteratorFactoryMethod) &&
          assertIteratorFactoryMethodReturnsTypeSpecifiedInIterableAggregateAnnotationValue(iteratorFactoryMethod)

      debug("Processing successful: " + success)
      if (!success) return false
    }
    true
  }

  private def assertIterableAggregateElementIsAppliedOnClass(element: Element): Boolean = {
    debug("assertIterableAggregateElementIsAppliedOnClass...")

    if (element.getKind != ElementKind.CLASS) {
      error("Only classes can be annotated with " + ITERABLE_AGGREGATE_ANNOTATION_NAME, element)
      false
    }
    else {
      debug("assertIterableAggregateElementIsAppliedOnClass: true")
      true
    }
  }

  private def assertIterableAggregateAnnotationValueIsAnnotatedWithIterator(element: Element, roundEnvironment: RoundEnvironment): Boolean = {
    debug("assertIterableAggregateAnnotationValueIsAnnotatedWithIterator...")

    val annotationValue = typeUtils.asElement(getAnnotationValueAsType(element, classOf[IterableAggregate]))
    val iterators = roundEnvironment.getElementsAnnotatedWith(classOf[Iterator])

    if (!iterators.contains(annotationValue)) {
      error(s"$annotationValue is not an iterator. Value of $ITERABLE_AGGREGATE_ANNOTATION_NAME must be a class annotated with @${classOf[Iterator].getName}.")
      false
    }
    else {
      debug("assertIterableAggregateAnnotationValueIsAnnotatedWithIterator: true")
      true
    }
  }

  private def assertIterableAggregateElementContainsAtLeastOneIteratorFactory(element: Element): Boolean = {
    debug("assertIterableAggregateElementContainsAtLeastOneIteratorFactory...")

    val count =
      element
        .getEnclosedElements.asScala
        .count(iteratorFactories.contains(_))

    if (count > 0) {
      debug(s"Found $count element(s) annotated with $ITERATOR_FACTORY_ANNOTATION_NAME within iterable aggregate")
      true
    }
    else {
      error("An iterable aggregate must have at least one method annotated with " + ITERATOR_FACTORY_ANNOTATION_NAME, element)
      false
    }
  }

  private def assertIteratorFactoryMethodIsEnclosedWithinIterableAggregate(element: Element): Boolean = {
    debug("assertIteratorFactoryMethodIsEnclosedWithinIterableAggregate...")

    val enclosingElement = element.getEnclosingElement
    // Since @IteratorFactory's target is METHOD, it must always be enclosed in some element
    assert(enclosingElement != null)

    if (iterableAggregates.contains(enclosingElement)) {
      debug("Iterable factory method is enclosed within an iterable aggregate")
      true
    }
    else {
      error("An iterator factory method must be part of a class annotated with " + ITERABLE_AGGREGATE_ANNOTATION_NAME, element)
      false
    }
  }

  private def assertIteratorFactoryMethodReturnsTypeSpecifiedInIterableAggregateAnnotationValue(element: Element): Boolean = {
    debug("assertIteratorFactoryMethodReturnsTypeSpecifiedInIterableAggregateAnnotationValue...")

    val enclosingElement = element.getEnclosingElement
    assert(enclosingElement != null)

    val iterableAggregateAnnotationValue = getAnnotationValueAsType(enclosingElement, classOf[IterableAggregate])
    debug("iterableAggregateAnnotationValue: " + iterableAggregateAnnotationValue)

    val iteratorFactoryMethodReturnType = element.asInstanceOf[ExecutableElement].getReturnType
    if (typeUtils.isAssignable(iterableAggregateAnnotationValue, iteratorFactoryMethodReturnType)) {
      debug("The return type of the iterator factory method is assignable to the type specified as value on its enclosing " + ITERABLE_AGGREGATE_ANNOTATION_NAME)
      true
    }
    else {
      error(s"The return type of the iterator factory method is `$iteratorFactoryMethodReturnType` but the iterable aggregate expects it to be `$iterableAggregateAnnotationValue`.", element)
      false
    }
  }
}
