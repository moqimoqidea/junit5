/*
 * Copyright 2015-2025 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * https://www.eclipse.org/legal/epl-v20.html
 */

package org.junit.platform.commons.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.platform.commons.support.PreconditionAssertions.assertPreconditionViolationException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.NullUnmarked;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.PreconditionViolationException;
import org.junit.platform.commons.util.AnnotationUtils;
import org.junit.platform.commons.util.ReflectionUtils;

/**
 * @since 1.0
 */
class AnnotationSupportTests {

	@SuppressWarnings({ "DataFlowIssue", "NullAway" })
	@Test
	void isAnnotatedPreconditions() {
		var optional = Optional.of(Probe.class);
		assertPreconditionViolationException("annotationType", () -> AnnotationSupport.isAnnotated(optional, null));
		assertPreconditionViolationException("annotationType", () -> AnnotationSupport.isAnnotated(Probe.class, null));
	}

	@Test
	void isAnnotatedDelegates() {
		var element = Probe.class;
		var optional = Optional.of(element);

		assertEquals(AnnotationUtils.isAnnotated(optional, Tag.class),
			AnnotationSupport.isAnnotated(optional, Tag.class));
		assertEquals(AnnotationUtils.isAnnotated(optional, Override.class),
			AnnotationSupport.isAnnotated(optional, Override.class));

		assertEquals(AnnotationUtils.isAnnotated(element, Tag.class),
			AnnotationSupport.isAnnotated(element, Tag.class));
		assertEquals(AnnotationUtils.isAnnotated(element, Override.class),
			AnnotationSupport.isAnnotated(element, Override.class));
	}

	@SuppressWarnings({ "DataFlowIssue", "NullAway" })
	@Test
	void findAnnotationOnElementPreconditions() {
		var optional = Optional.of(Probe.class);
		assertPreconditionViolationException("annotationType", () -> AnnotationSupport.findAnnotation(optional, null));
		assertPreconditionViolationException("annotationType",
			() -> AnnotationSupport.findAnnotation(Probe.class, null));
	}

	@Test
	void findAnnotationOnElementDelegates() {
		var element = Probe.class;
		var optional = Optional.of(element);

		assertEquals(AnnotationUtils.findAnnotation(optional, Tag.class),
			AnnotationSupport.findAnnotation(optional, Tag.class));
		assertEquals(AnnotationUtils.findAnnotation(optional, Override.class),
			AnnotationSupport.findAnnotation(optional, Override.class));

		assertEquals(AnnotationUtils.findAnnotation(element, Tag.class),
			AnnotationSupport.findAnnotation(element, Tag.class));
		assertEquals(AnnotationUtils.findAnnotation(element, Override.class),
			AnnotationSupport.findAnnotation(element, Override.class));
	}

	@SuppressWarnings({ "deprecation", "DataFlowIssue", "NullAway" })
	@Test
	void findAnnotationOnClassWithSearchModePreconditions() {
		assertPreconditionViolationException("annotationType",
			() -> AnnotationSupport.findAnnotation(Probe.class, null, SearchOption.INCLUDE_ENCLOSING_CLASSES));
		assertPreconditionViolationException("SearchOption",
			() -> AnnotationSupport.findAnnotation(Probe.class, Override.class, (SearchOption) null));
	}

	@SuppressWarnings({ "DataFlowIssue", "NullAway" })
	@Test
	void findAnnotationOnClassWithEnclosingInstanceTypesPreconditions() {
		assertPreconditionViolationException("enclosingInstanceTypes",
			() -> AnnotationSupport.findAnnotation(Probe.class, Override.class, (List<Class<?>>) null));
	}

	@SuppressWarnings("deprecation")
	@Test
	void findAnnotationOnClassWithSearchModeDelegates() {
		Class<?> clazz = Probe.class;
		assertEquals(AnnotationUtils.findAnnotation(clazz, Tag.class, false),
			AnnotationSupport.findAnnotation(clazz, Tag.class, SearchOption.DEFAULT));
		assertEquals(AnnotationUtils.findAnnotation(clazz, Tag.class, true),
			AnnotationSupport.findAnnotation(clazz, Tag.class, SearchOption.INCLUDE_ENCLOSING_CLASSES));
		assertEquals(AnnotationUtils.findAnnotation(clazz, Override.class, false),
			AnnotationSupport.findAnnotation(clazz, Override.class, SearchOption.DEFAULT));
		assertEquals(AnnotationUtils.findAnnotation(clazz, Override.class, true),
			AnnotationSupport.findAnnotation(clazz, Override.class, SearchOption.INCLUDE_ENCLOSING_CLASSES));

		clazz = Probe.InnerClass.class;
		assertEquals(AnnotationUtils.findAnnotation(clazz, Tag.class, false),
			AnnotationSupport.findAnnotation(clazz, Tag.class, SearchOption.DEFAULT));
		assertEquals(AnnotationUtils.findAnnotation(clazz, Tag.class, true),
			AnnotationSupport.findAnnotation(clazz, Tag.class, SearchOption.INCLUDE_ENCLOSING_CLASSES));
		assertEquals(AnnotationUtils.findAnnotation(clazz, Override.class, false),
			AnnotationSupport.findAnnotation(clazz, Override.class, SearchOption.DEFAULT));
		assertEquals(AnnotationUtils.findAnnotation(clazz, Override.class, true),
			AnnotationSupport.findAnnotation(clazz, Override.class, SearchOption.INCLUDE_ENCLOSING_CLASSES));
	}

	@NullUnmarked
	@Test
	void findAnnotationOnClassWithEnclosingInstanceTypes() {
		assertThat(AnnotationSupport.findAnnotation(Probe.class, Tag.class, List.of())) //
				.contains(Probe.class.getDeclaredAnnotation(Tag.class));
		assertThat(AnnotationSupport.findAnnotation(Probe.InnerClass.class, Tag.class, List.of())) //
				.isEmpty();
		assertThat(AnnotationSupport.findAnnotation(Probe.InnerClass.class, Tag.class, List.of(Probe.class))) //
				.contains(Probe.class.getDeclaredAnnotation(Tag.class));
	}

	@SuppressWarnings({ "DataFlowIssue", "NullAway" })
	@Test
	void findPublicAnnotatedFieldsPreconditions() {
		assertPreconditionViolationException("Class",
			() -> AnnotationSupport.findPublicAnnotatedFields(null, String.class, FieldMarker.class));
		assertPreconditionViolationException("fieldType",
			() -> AnnotationSupport.findPublicAnnotatedFields(Probe.class, null, FieldMarker.class));
		assertPreconditionViolationException("annotationType",
			() -> AnnotationSupport.findPublicAnnotatedFields(Probe.class, String.class, null));
	}

	@Test
	void findPublicAnnotatedFieldsDelegates() {
		assertEquals(AnnotationUtils.findPublicAnnotatedFields(Probe.class, String.class, FieldMarker.class),
			AnnotationSupport.findPublicAnnotatedFields(Probe.class, String.class, FieldMarker.class));
		assertEquals(AnnotationUtils.findPublicAnnotatedFields(Probe.class, Throwable.class, Override.class),
			AnnotationSupport.findPublicAnnotatedFields(Probe.class, Throwable.class, Override.class));
	}

	@SuppressWarnings({ "DataFlowIssue", "NullAway" })
	@Test
	void findAnnotatedMethodsPreconditions() {
		assertPreconditionViolationException("Class",
			() -> AnnotationSupport.findAnnotatedMethods(null, Tag.class, HierarchyTraversalMode.TOP_DOWN));
		assertPreconditionViolationException("annotationType",
			() -> AnnotationSupport.findAnnotatedMethods(Probe.class, null, HierarchyTraversalMode.TOP_DOWN));
		assertPreconditionViolationException("HierarchyTraversalMode",
			() -> AnnotationSupport.findAnnotatedMethods(Probe.class, Tag.class, null));
	}

	@Test
	void findAnnotatedMethodsDelegates() {
		assertEquals(
			AnnotationUtils.findAnnotatedMethods(Probe.class, Tag.class,
				ReflectionUtils.HierarchyTraversalMode.TOP_DOWN),
			AnnotationSupport.findAnnotatedMethods(Probe.class, Tag.class, HierarchyTraversalMode.TOP_DOWN));
		assertEquals(
			AnnotationUtils.findAnnotatedMethods(Probe.class, Tag.class,
				ReflectionUtils.HierarchyTraversalMode.BOTTOM_UP),
			AnnotationSupport.findAnnotatedMethods(Probe.class, Tag.class, HierarchyTraversalMode.BOTTOM_UP));

		assertEquals(
			AnnotationUtils.findAnnotatedMethods(Probe.class, Override.class,
				ReflectionUtils.HierarchyTraversalMode.TOP_DOWN),
			AnnotationSupport.findAnnotatedMethods(Probe.class, Override.class, HierarchyTraversalMode.TOP_DOWN));
		assertEquals(
			AnnotationUtils.findAnnotatedMethods(Probe.class, Override.class,
				ReflectionUtils.HierarchyTraversalMode.BOTTOM_UP),
			AnnotationSupport.findAnnotatedMethods(Probe.class, Override.class, HierarchyTraversalMode.BOTTOM_UP));
	}

	@Test
	void findRepeatableAnnotationsDelegates() throws Throwable {
		var bMethod = Probe.class.getDeclaredMethod("bMethod");
		assertEquals(AnnotationUtils.findRepeatableAnnotations(bMethod, Tag.class),
			AnnotationSupport.findRepeatableAnnotations(bMethod, Tag.class));
		Object expected = assertThrows(PreconditionViolationException.class,
			() -> AnnotationUtils.findRepeatableAnnotations(bMethod, Override.class));
		Object actual = assertThrows(PreconditionViolationException.class,
			() -> AnnotationSupport.findRepeatableAnnotations(bMethod, Override.class));
		assertSame(expected.getClass(), actual.getClass(), "expected same exception class");
		assertEquals(expected.toString(), actual.toString(), "expected equal exception toString representation");
	}

	@SuppressWarnings({ "DataFlowIssue", "NullAway" })
	@Test
	void findRepeatableAnnotationsPreconditions() {
		assertPreconditionViolationException("annotationType",
			() -> AnnotationSupport.findRepeatableAnnotations(Probe.class, null));
	}

	@Test
	void findAnnotatedFieldsDelegates() {
		assertEquals(AnnotationUtils.findAnnotatedFields(Probe.class, FieldMarker.class, f -> true),
			AnnotationSupport.findAnnotatedFields(Probe.class, FieldMarker.class));
		assertEquals(AnnotationUtils.findAnnotatedFields(Probe.class, Override.class, f -> true),
			AnnotationSupport.findAnnotatedFields(Probe.class, Override.class));

		assertEquals(
			AnnotationUtils.findAnnotatedFields(Probe.class, FieldMarker.class, f -> true,
				ReflectionUtils.HierarchyTraversalMode.TOP_DOWN),
			AnnotationSupport.findAnnotatedFields(Probe.class, FieldMarker.class, f -> true,
				HierarchyTraversalMode.TOP_DOWN));
		assertEquals(
			AnnotationUtils.findAnnotatedFields(Probe.class, Override.class, f -> true,
				ReflectionUtils.HierarchyTraversalMode.TOP_DOWN),
			AnnotationSupport.findAnnotatedFields(Probe.class, Override.class, f -> true,
				HierarchyTraversalMode.TOP_DOWN));
	}

	@SuppressWarnings({ "DataFlowIssue", "NullAway" })
	@Test
	void findAnnotatedFieldsPreconditions() {
		assertPreconditionViolationException("Class",
			() -> AnnotationSupport.findAnnotatedFields(null, FieldMarker.class));
		assertPreconditionViolationException("annotationType",
			() -> AnnotationSupport.findAnnotatedFields(Probe.class, null));

		assertPreconditionViolationException("Class", () -> AnnotationSupport.findAnnotatedFields(null, Override.class,
			f -> true, HierarchyTraversalMode.TOP_DOWN));
		assertPreconditionViolationException("annotationType",
			() -> AnnotationSupport.findAnnotatedFields(Probe.class, null, f -> true, HierarchyTraversalMode.TOP_DOWN));
		assertPreconditionViolationException("HierarchyTraversalMode",
			() -> AnnotationSupport.findAnnotatedFields(Probe.class, Override.class, f -> true, null));
	}

	@Test
	void findAnnotatedFieldValuesForNonStaticFields() {
		var instance = new Fields();

		assertThat(AnnotationSupport.findAnnotatedFieldValues(instance, Tag.class)).isEmpty();

		assertThat(AnnotationSupport.findAnnotatedFieldValues(instance, FieldMarker.class))//
				.containsExactlyInAnyOrder("i1", "i2");
	}

	@Test
	void findAnnotatedFieldValuesForStaticFields() {
		assertThat(AnnotationSupport.findAnnotatedFieldValues(Fields.class, Tag.class)).isEmpty();

		assertThat(AnnotationSupport.findAnnotatedFieldValues(Fields.class, FieldMarker.class))//
				.containsExactlyInAnyOrder("s1", "s2");
	}

	@Test
	void findAnnotatedFieldValuesForNonStaticFieldsByType() {
		var instance = new Fields();

		assertThat(AnnotationSupport.findAnnotatedFieldValues(instance, FieldMarker.class, Number.class)).isEmpty();

		assertThat(AnnotationSupport.findAnnotatedFieldValues(instance, FieldMarker.class, String.class))//
				.containsExactlyInAnyOrder("i1", "i2");
	}

	@Test
	void findAnnotatedFieldValuesForStaticFieldsByType() {
		assertThat(AnnotationSupport.findAnnotatedFieldValues(Fields.class, FieldMarker.class, Number.class)).isEmpty();

		assertThat(AnnotationSupport.findAnnotatedFieldValues(Fields.class, FieldMarker.class, String.class))//
				.containsExactlyInAnyOrder("s1", "s2");
	}

	@SuppressWarnings({ "DataFlowIssue", "NullAway" })
	@Test
	void findAnnotatedFieldValuesPreconditions() {
		assertPreconditionViolationException("instance",
			() -> AnnotationSupport.findAnnotatedFieldValues((Object) null, FieldMarker.class));
		assertPreconditionViolationException("annotationType",
			() -> AnnotationSupport.findAnnotatedFieldValues(this, null));

		assertPreconditionViolationException("Class",
			() -> AnnotationSupport.findAnnotatedFieldValues(null, FieldMarker.class));
		assertPreconditionViolationException("annotationType",
			() -> AnnotationSupport.findAnnotatedFieldValues(Probe.class, null));

		assertPreconditionViolationException("instance",
			() -> AnnotationSupport.findAnnotatedFieldValues((Object) null, FieldMarker.class, Number.class));
		assertPreconditionViolationException("annotationType",
			() -> AnnotationSupport.findAnnotatedFieldValues(this, null, Number.class));
		assertPreconditionViolationException("fieldType",
			() -> AnnotationSupport.findAnnotatedFieldValues(this, FieldMarker.class, null));

		assertPreconditionViolationException("Class",
			() -> AnnotationSupport.findAnnotatedFieldValues(null, FieldMarker.class, Number.class));
		assertPreconditionViolationException("annotationType",
			() -> AnnotationSupport.findAnnotatedFieldValues(Probe.class, null, Number.class));
		assertPreconditionViolationException("fieldType",
			() -> AnnotationSupport.findAnnotatedFieldValues(Probe.class, FieldMarker.class, null));
	}

	// -------------------------------------------------------------------------

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@interface FieldMarker {
	}

	@Tag("class-tag")
	static class Probe {

		@FieldMarker
		public static String publicAnnotatedStaticField = "static";

		@FieldMarker
		public String publicAnnotatedInstanceField = "instance";

		@Tag("method-tag")
		void aMethod() {
		}

		@Tag("method-tag-1")
		@Tag("method-tag-2")
		void bMethod() {
		}

		@SuppressWarnings("InnerClassMayBeStatic")
		class InnerClass {
		}

	}

	static class Fields {

		@FieldMarker
		static String staticField1 = "s1";

		@FieldMarker
		static String staticField2 = "s2";

		@FieldMarker
		String instanceField1 = "i1";

		@FieldMarker
		String instanceField2 = "i2";

	}

}
