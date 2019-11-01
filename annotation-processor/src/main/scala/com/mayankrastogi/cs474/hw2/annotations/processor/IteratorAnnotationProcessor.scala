package com.mayankrastogi.cs474.hw2.annotations.processor

import java.lang.annotation.Annotation
import java.util

import com.mayankrastogi.cs474.hw2.annotations.Iterator
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.`type`.TypeKind
import javax.lang.model.element._

import scala.jdk.CollectionConverters._

class IteratorAnnotationProcessor extends AbstractAnnotationProcessor {

  private val ITERATOR_ANNOTATION_NAME = "@" + classOf[Iterator].getName
  private val CURRENT_ITEM_ANNOTATION_NAME = "@" + classOf[Iterator.CurrentItem].getName
  private val IS_DONE_ANNOTATION_NAME = "@" + classOf[Iterator.IsDone].getName
  private val NEXT_ITEM_ANNOTATION_NAME = "@" + classOf[Iterator.NextItem].getName

  private var iterators: Set[Element] = _

  override def getSupportedAnnotations: List[Class[_ <: Annotation]] = List(
    classOf[Iterator],
    classOf[Iterator.CurrentItem],
    classOf[Iterator.IsDone],
    classOf[Iterator.NextItem]
  )

  override def process(annotations: util.Set[_ <: TypeElement], roundEnv: RoundEnvironment): Boolean = {
    debug(String.format("process(annotations: %s, roundEnv: %s)", annotations.toString, roundEnv.toString))

    iterators = roundEnv.getElementsAnnotatedWith(classOf[Iterator]).asScala.toSet
    debug("iterators: " + iterators)

    val currentItems = roundEnv.getElementsAnnotatedWith(classOf[Iterator.CurrentItem]).asScala.toSet
    debug("currentItems: " + currentItems)

    val isDones = roundEnv.getElementsAnnotatedWith(classOf[Iterator.IsDone]).asScala.toSet
    debug("isDones: " + isDones)

    val nextItems = roundEnv.getElementsAnnotatedWith(classOf[Iterator.NextItem]).asScala.toSet
    debug("nextItems: " + nextItems)

    note("Processing elements annotated with " + ITERATOR_ANNOTATION_NAME)

    iterators.foreach { iteratorElement =>
      debug("Processing iteratorElement", iteratorElement)

      val success =
        assertIteratorElementIsValid(iteratorElement) &&
          assertIteratorElementContainsOnlyOneMethodAnnotatedWith(classOf[Iterator.CurrentItem], iteratorElement) &&
          assertIteratorElementContainsOnlyOneMethodAnnotatedWith(classOf[Iterator.IsDone], iteratorElement) &&
          assertIteratorElementContainsOnlyOneMethodAnnotatedWith(classOf[Iterator.NextItem], iteratorElement)

      debug("Processing successful: " + success)
      if (!success) return false
    }

    note("Processing elements annotated with " + CURRENT_ITEM_ANNOTATION_NAME)

    currentItems.foreach { currentItemMethod =>
      debug("Processing currentItemMethod", currentItemMethod)

      val success =
        assertMethodIsEnclosedWithinIterator(currentItemMethod, classOf[Iterator.CurrentItem]) &&
          assertMethodReturnsTypeSpecifiedInIteratorAnnotationValue(currentItemMethod, classOf[Iterator.CurrentItem]) &&
          assertMethodTakesNoParameters(currentItemMethod, classOf[Iterator.CurrentItem])

      debug("Processing successful: " + success)
      if (!success) return false
    }

    note("Processing elements annotated with " + IS_DONE_ANNOTATION_NAME)

    isDones.foreach { isDoneMethod =>
      debug("Processing isDoneMethod", isDoneMethod)

      val success =
        assertMethodIsEnclosedWithinIterator(isDoneMethod, classOf[Iterator.IsDone]) &&
          assertIsDoneMethodReturnsBooleanType(isDoneMethod) &&
          assertMethodTakesNoParameters(isDoneMethod, classOf[Iterator.IsDone]) &&
          warnIfMethodIsPrivate(isDoneMethod, classOf[Iterator.IsDone])

      debug("Processing successful: " + success)
      if (!success) return false
    }

    note("Processing elements annotated with " + NEXT_ITEM_ANNOTATION_NAME)

    nextItems.foreach { nextItemMethod =>
      debug("Processing nextItemMethod", nextItemMethod)

      val success =
        assertMethodIsEnclosedWithinIterator(nextItemMethod, classOf[Iterator.NextItem]) &&
          assertMethodReturnsTypeSpecifiedInIteratorAnnotationValue(nextItemMethod, classOf[Iterator.NextItem]) &&
          assertMethodTakesNoParameters(nextItemMethod, classOf[Iterator.NextItem]) &&
          warnIfMethodIsPrivate(nextItemMethod, classOf[Iterator.NextItem])

      debug("Processing successful: " + success)
      if (!success) return false
    }
    true
  }

  private def assertIteratorElementIsValid(element: Element): Boolean = {
    debug("assertIteratorElementIsValid...")

    if (element.getKind != ElementKind.CLASS) {
      error("Only classes can be annotated with " + ITERATOR_ANNOTATION_NAME, element)
      false
    }
    else {
      debug("assertIteratorElementIsValid: true")
      true
    }
  }

  private def assertIteratorElementContainsOnlyOneMethodAnnotatedWith(annotation: Class[_ <: Annotation], enclosingElement: Element): Boolean = {
    debug("assertIteratorElementContainsOnlyOneMethodAnnotatedWith...")

    val count =
      enclosingElement
        .getEnclosedElements.asScala
        .count(_.getAnnotation(annotation) != null)

    debug(s"Found $count element(s) annotated with @${annotation.getName} within iterator")

    if (count == 1) {
      debug("assertIteratorElementContainsOnlyOneMethodAnnotatedWith: true")
      true
    }
    else {
      val quantity = if (count < 1) "a" else "only one"
      error(s"An iterator must have $quantity  method annotated with @${annotation.getName}", enclosingElement)
      false
    }
  }

  private def assertMethodIsEnclosedWithinIterator(element: Element, methodAnnotation: Class[_ <: Annotation]): Boolean = {
    debug("assertMethodIsEnclosedWithinIterator...")

    val enclosingElement = element.getEnclosingElement
    // Since @Iterator.X's target is METHOD, it must always be enclosed in some element
    assert(enclosingElement != null)

    if (iterators.contains(enclosingElement)) {
      debug(methodAnnotation.getSimpleName + " is enclosed within an iterator")
      true
    }
    else {
      error(s"A method annotated with @${methodAnnotation.getName} must be part of a class annotated with $ITERATOR_ANNOTATION_NAME", element)
      false
    }
  }

  private def assertMethodReturnsTypeSpecifiedInIteratorAnnotationValue(element: Element, methodAnnotation: Class[_ <: Annotation]): Boolean = {
    debug("assertMethodReturnsTypeSpecifiedInIteratorAnnotationValue...")

    val enclosingElement = element.getEnclosingElement
    assert(enclosingElement != null)

    val iteratorAnnotationValue = getAnnotationValueAsType(enclosingElement, classOf[Iterator])
    debug("iteratorAnnotationValue: " + iteratorAnnotationValue)

    val methodReturnType = element.asInstanceOf[ExecutableElement].getReturnType
    if (typeUtils.isAssignable(iteratorAnnotationValue, methodReturnType)) {
      debug(s"The return type of the method annotated with @${methodAnnotation.getName} is assignable to the type specified as value on its enclosing $ITERATOR_ANNOTATION_NAME")
      true
    }
    else {
      error(s"The return type of the method annotated with @${methodAnnotation.getName} is `$methodReturnType` but the iterator expects it to be `$iteratorAnnotationValue`.", element)
      false
    }
  }

  private def assertMethodTakesNoParameters(element: Element, methodAnnotation: Class[_ <: Annotation]): Boolean = {
    debug("assertMethodTakesNoParameters...")
    if (element.asInstanceOf[ExecutableElement].getParameters.isEmpty) {
      debug("assertMethodTakesNoParameters: true")
      true
    }
    else {
      error("A method annotated with @" + methodAnnotation.getName + " must not take any parameters", element)
      false
    }
  }

  private def assertIsDoneMethodReturnsBooleanType(element: Element): Boolean = {
    debug("assertIsDoneMethodReturnsBooleanType...")

    val methodReturnType = element.asInstanceOf[ExecutableElement].getReturnType
    val expectedReturnType = typeUtils.getPrimitiveType(TypeKind.BOOLEAN)

    if (typeUtils.isAssignable(methodReturnType, expectedReturnType)) {
      debug("assertIsDoneMethodReturnsBooleanType: true")
      true
    }
    else {
      error(s"The return type of the method annotated with $IS_DONE_ANNOTATION_NAME is `$methodReturnType` but the iterator expects it to be `$expectedReturnType`.", element)
      false
    }
  }

  private def warnIfMethodIsPrivate(element: Element, methodAnnotation: Class[_ <: Annotation]): Boolean = {
    debug("warnIfMethodIsPrivate...")

    val treatWarningsAsErrors = shouldTreatWarningsAsErrors(element)

    val operationSuccessful = withTreatMandatoryWarningsAsErrors(treatWarningsAsErrors) {
      if (element.getModifiers.contains(Modifier.PRIVATE)) {
        warning("A method annotated with @" + methodAnnotation.getName + " was found to be private", element)
        false
      }
      else {
        debug("isPrivate: false")
        true
      }
    }
    debug("operationSuccessful: " + operationSuccessful)
    operationSuccessful || !treatWarningsAsErrors
  }

  private def shouldTreatWarningsAsErrors(element: Element): Boolean = {
    debug("shouldTreatWarningsAsErrors...")

    val enclosingElement = element.getEnclosingElement
    assert(enclosingElement != null)

    val iteratorAnnotation = enclosingElement.getAnnotation(classOf[Iterator])
    // Enclosing element will always be annotated with @Iterator since we test this before invoking this method
    assert(iteratorAnnotation != null)

    val result = iteratorAnnotation.treatWarningsAsErrors
    debug("shouldTreatWarningsAsErrors: " + result)
    result
  }
}
