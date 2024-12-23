/*
 * Infinitest, a Continuous Test Runner.
 *
 * Copyright (C) 2010-2013
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>
 * "David Gageot" <david@gageot.net>, et al.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.infinitest.parser;

import static javassist.Modifier.isPublic;
import static javassist.bytecode.AnnotationsAttribute.invisibleTag;
import static javassist.bytecode.AnnotationsAttribute.visibleTag;
import static org.infinitest.parser.DescriptorParser.parseClassNameFromConstantPoolDescriptor;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;

import org.infinitest.util.InfinitestUtils;
import org.junit.platform.commons.annotation.Testable;
import org.junit.runner.RunWith;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;
import junit.framework.TestCase;

/**
 * Be careful: instances of this class are kept in a cache
 * so we should keep its footprint minimal.
 */
public class JavaAssistClass extends AbstractJavaClass {
	private static final String ARCHUNIT_ARCHTEST = "com.tngtech.archunit.junit.ArchTest";
	private static final String ARCHUNIT_ANALYSE_CLASSES = "com.tngtech.archunit.junit.AnalyzeClasses";
	
	private final String[] imports;
	private final boolean isATest;
	private final boolean canComputeTestDependencies;
	private final String className;
	private File classFile;

	public JavaAssistClass(CtClass classReference) {
		imports = findImports(classReference);
		isATest = !isAbstract(classReference) &&
				((hasTests(classReference) && canInstantiate(classReference))
				|| isJUnit5Testable(classReference, new HashSet<CtClass>()));
		
		// ArchUnit loads classes dynamically so we cannot compute the dependencies
		canComputeTestDependencies = !hasArchUnitTests(classReference);
		className = classReference.getName();
	}
	
	/**
	 * @return <code>true</code> if the class or one of its parents is annotated with {@link Testable}
	 */
	private boolean isJUnit5Testable(final CtClass classReference, Set<CtClass> visitedClasses) {
		// Break the recursion when we encounter a  class we have already visited
		if (!visitedClasses.add(classReference)) {
			return false;
		}
		
		CtClass clazz = classReference;
		while (clazz != null) {
			if (clazz.hasAnnotation(Testable.class)) {
				return true;
			}
			
			// Look for class-level annotations and check if they have the Testable meta annotation
			AttributeInfo attributeInfo = clazz.getClassFile2().getAttribute(AnnotationsAttribute.visibleTag);
			if (isJUnit5Testable(attributeInfo, classReference.getClassPool(), visitedClasses)) {
				return true;
			}
			
			for (CtMethod method : classReference.getDeclaredMethods()) {
				attributeInfo = method.getMethodInfo2().getAttribute(AnnotationsAttribute.visibleTag);
				if (isJUnit5Testable(attributeInfo, classReference.getClassPool(), visitedClasses)) {
					return true;
				}
			}
			
			for (CtField field : classReference.getDeclaredFields()) {
				attributeInfo = field.getFieldInfo2().getAttribute(AnnotationsAttribute.visibleTag);
				if (isJUnit5Testable(attributeInfo, classReference.getClassPool(), visitedClasses)) {
					return true;
				}
			}
			
			try {
				clazz = clazz.getSuperclass();
			} catch (NotFoundException e) {
				return false;
			}
		}
		
		return false;
	}

	/**
	 * @return <code>true</code> if the attribute (from a class, method or field) has the {@link Testable} meta
	 * annotation or an annotation that is itself {@link Testable}
	 */
	private boolean isJUnit5Testable(AttributeInfo attributeInfo, ClassPool classPool,
			Set<CtClass> visitedClasses) {
		if (attributeInfo instanceof AnnotationsAttribute) {
			AnnotationsAttribute attribute = (AnnotationsAttribute) attributeInfo;
			Annotation[] annotations = attribute.getAnnotations();
			if (annotations != null) {
				for (Annotation annotation : annotations) {
					try {
						CtClass annotationClass = classPool.get(annotation.getTypeName());
						if (isJUnit5Testable(annotationClass, visitedClasses)) {
							return true;
						}

					} catch (NotFoundException e) {
						return false;
					}
				}
			}
		}
		
		return false;
	}

	@Override
	public String[] getImports() {
		return imports;
	}

	private String[] findImports(CtClass ctClass) {
		Set<String> imports = new HashSet<>();
		addDependenciesFromConstantPool(ctClass, imports);
		addFieldDependencies(ctClass, imports);
		addClassAnnotationDependencies(ctClass, imports);
		addFieldAnnotationDependencies(ctClass, imports);
		addMethodAnnotationDependencies(ctClass, imports);

		String[] array = new String[imports.size()];

		int index = 0;
		for (String anImport : imports) {
			array[index++] = anImport.intern(); // Use less memory
		}
		return array;
	}

	private void addFieldAnnotationDependencies(CtClass ctClass, Collection<String> imports) {
		for (CtField field : ctClass.getDeclaredFields()) {
			List<?> attributes = field.getFieldInfo2().getAttributes();
			addAnnotationsForAttributes(imports, attributes);
		}
	}

	private void addFieldDependencies(CtClass ctClass, Collection<String> imports) {
		for (CtField field : ctClass.getDeclaredFields()) {
			imports.add(parseClassNameFromConstantPoolDescriptor(field.getFieldInfo2().getDescriptor()));
		}
	}

	private void addMethodAnnotationDependencies(CtClass ctClass, Collection<String> imports) {
		for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
			MethodInfo methodInfo = ctMethod.getMethodInfo2();
			List<?> attributes = methodInfo.getAttributes();
			addAnnotationsForAttributes(imports, attributes);
			addParameterAnnotationsFor(imports, methodInfo, ParameterAnnotationsAttribute.visibleTag);
			addParameterAnnotationsFor(imports, methodInfo, ParameterAnnotationsAttribute.invisibleTag);
		}
	}

	private void addAnnotationsForAttributes(Collection<String> imports, List<?> attributes) {
		for (Object each : attributes) {
			if (each instanceof AnnotationsAttribute) {
				addAnnotations(imports, (AnnotationsAttribute) each);
			}
		}
	}

	private void addParameterAnnotationsFor(Collection<String> imports, MethodInfo methodInfo, String tag) {
		AttributeInfo attribute = methodInfo.getAttribute(tag);
		ParameterAnnotationsAttribute annotationAttribute = (ParameterAnnotationsAttribute) attribute;
		if (annotationAttribute != null) {
			Annotation[][] parameters = annotationAttribute.getAnnotations();
			for (Annotation[] annotations : parameters) {
				for (Annotation annotation : annotations) {
					imports.add(annotation.getTypeName());
				}
			}
		}
	}

	private void addClassAnnotationDependencies(CtClass classReference, Collection<String> imports) {
		addClassAnnotationsOfTagType(classReference, imports, visibleTag);
		addClassAnnotationsOfTagType(classReference, imports, invisibleTag);
	}

	private void addClassAnnotationsOfTagType(CtClass classRef, Collection<String> imports, String tag) {
		addAnnotations(imports, getAnnotationsOfType(tag, classRef));
	}

	private AnnotationsAttribute getAnnotationsOfType(String tag, CtClass classRef) {
		return (AnnotationsAttribute) classRef.getClassFile2().getAttribute(tag);
	}

	private void addAnnotations(Collection<String> imports, AnnotationsAttribute annotations) {
		if (annotations != null) {
			for (Annotation each : annotations.getAnnotations()) {
				imports.add(each.getTypeName());
			}
		}
	}

	private void addDependenciesFromConstantPool(CtClass ctClass, Collection<String> imports) {
		ConstPool constPool = ctClass.getClassFile2().getConstPool();
		Set<?> classNames = constPool.getClassNames();
		for (Object each : classNames) {
			imports.add(pathToClassName(each.toString()));
		}
	}

	private String pathToClassName(String classPath) {
		return classPath.replace('/', '.');
	}

	@Override
	public String getName() {
		return className;
	}

	private boolean isAbstract(CtClass classReference) {
		return classReference.isInterface() || Modifier.isAbstract(classReference.getModifiers());
	}

	@Override
	public boolean isATest() {
		return isATest;
	}
	
	@Override
	public boolean canComputeTestDependencies() {
		return canComputeTestDependencies;
	}

	boolean canInstantiate(CtClass classReference) {
		for (CtConstructor ctConstructor : classReference.getConstructors()) {
			if (isValidConstructor(classReference, ctConstructor)) {
				return true;
			}
		}
		return false;
	}

	private boolean isValidConstructor(CtClass classReference, CtConstructor ctConstructor) {
		return usesCustomRunner(classReference) || hasJUnitCompatibleConstructor(ctConstructor);
	}

	private boolean hasJUnitCompatibleConstructor(CtConstructor ctConstructor) {
		if (ctConstructor.isConstructor() && isPublic(ctConstructor.getModifiers())) {
			try {
				CtClass[] parameterTypes = ctConstructor.getParameterTypes();
				if (hasDefaultConstructor(parameterTypes)) {
					return true;
				}
				if (hasTestNameConstructor(parameterTypes)) {
					return true;
				}
			} catch (NotFoundException e) {
				return false;
			}
		}
		return false;
	}

	private boolean hasDefaultConstructor(CtClass[] parameterTypes) {
		return parameterTypes.length == 0;
	}

	private boolean hasTestNameConstructor(CtClass[] parameterTypes) {
		return (parameterTypes.length == 1) && parameterTypes[0].getName().equals(String.class.getName());
	}

	@Override
	public String toString() {
		return getName();
	}

	private boolean hasTests(CtClass classReference) {
		return hasJUnitTestMethods(classReference) //
				|| usesCustomRunner(classReference) //
				|| hasTestNGTests(classReference);
	}

	private boolean usesCustomRunner(CtClass classReference) {
		return isAnnotatedWithCustomRunner(classReference) || anySuperclassOf(classReference, hasACustomRunner());
	}

	private boolean isAnnotatedWithCustomRunner(CtClass classReference) {
		return isAnnotatedWithGivenAnnotation(classReference, RunWith.class);
	}

	private boolean isAnnotatedWithGivenAnnotation(CtClass classReference, Class<?> givenAnnotation) {
		AnnotationsAttribute annotations = getAnnotationsOfType(visibleTag, classReference);
		if (annotations != null) {
			for (Annotation annotation : annotations.getAnnotations()) {
				if (annotation.getTypeName().equals(givenAnnotation.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	private Predicate<CtClass> hasACustomRunner() {
		return this::isAnnotatedWithCustomRunner;
	}

	private boolean hasArchUnitTests(CtClass classReference) {
		for (CtField field : classReference.getDeclaredFields()) {
			if (field.hasAnnotation(ARCHUNIT_ARCHTEST) || field.hasAnnotation(ARCHUNIT_ANALYSE_CLASSES)) {
				return true;
			}
		}
		
		// getMethods() returns the non-private methods
		for (CtMethod ctMethod : classReference.getDeclaredMethods()) {
			if (ctMethod.hasAnnotation(ARCHUNIT_ARCHTEST) || ctMethod.hasAnnotation(ARCHUNIT_ANALYSE_CLASSES)) {
				return true;
			}
		}
		
		return false;
	}

	private boolean hasTestNGTests(CtClass classReference) {
		if (isTestNGTestClass(classReference)) {
			return true;
		}
		for (CtMethod ctMethod : classReference.getMethods()) {
			if (isTestNGTestMethod(ctMethod)) {
				return true;
			}
		}
		return false;
	}

	private boolean isTestNGTestClass(CtClass classReference) {
		return isAnnotatedWithGivenAnnotation(classReference, org.testng.annotations.Test.class);
	}

	private boolean hasJUnitTestMethods(CtClass classReference) {
		for (CtMethod ctMethod : classReference.getMethods()) {
			if (isJUnit5TestMethod(ctMethod, classReference.getClassPool())
					|| isJUnit4TestMethod(ctMethod) 
					|| isJUnit3TestMethod(ctMethod)) {
				return true;
			}
		}
		return false;
	}

    private boolean isJUnit5TestMethod(CtMethod ctMethod, ClassPool classPool) {
        final List<?> attributes = ctMethod.getMethodInfo2().getAttributes();
        return attributes.stream()
                .filter(AnnotationsAttribute.class::isInstance)
                .map(AnnotationsAttribute.class::cast)
                .map(AnnotationsAttribute::getAnnotations)
                .flatMap(Arrays::stream)
                .anyMatch(annotation -> isJUnit5TestAnnotation(annotation, classPool));
    }
	
	/**
	 * @return <code>true</code> if the annotation is JUnit's <code>Test</code> or if the annotation type itself is annotated
	 * with <code>Test</code> or <code>TestTemplate</code>. Annotations such as <code>ParameterizedTest<code> are annotated with
	 * <code>TestTemplate</code> and should be detected as tests
	 */
	private boolean isJUnit5TestAnnotation(Annotation annotation, ClassPool classPool) {
		String annotationTypeName = annotation.getTypeName();
		boolean isJUnitTestAnnotation = org.junit.jupiter.api.Test.class.getName().equals(annotationTypeName);
		
		if (isJUnitTestAnnotation) {
			return true;
		}
		
		try {
			CtClass annotationType = classPool.get(annotationTypeName);
			
			return isAnnotatedWithGivenAnnotation(annotationType, org.junit.jupiter.api.Test.class)
					|| isAnnotatedWithGivenAnnotation(annotationType, org.junit.jupiter.api.TestTemplate.class);
		} catch (NotFoundException  e) {
			InfinitestUtils.log(Level.FINE, "Could not load class " + annotationTypeName + " : " + e.getMessage());
			
			return false;
		}
    }

	private boolean isJUnit3TestMethod(CtMethod ctMethod) {
		return ctMethod.getName().startsWith("test") && anySuperclassOf(ctMethod.getDeclaringClass(), isTestCase());
	}

	private boolean anySuperclassOf(CtClass classReference, Predicate<CtClass> predicate) {
		CtClass superclass = findSuperclass(classReference);
		while (superclass != null) {
			if (predicate.test(superclass)) {
				return true;
			}
			superclass = findSuperclass(superclass);
		}
		return false;
	}

	private Predicate<CtClass> isTestCase() {
		return input -> input.getName().equals(TestCase.class.getName());
	}

	private CtClass findSuperclass(CtClass aClassReference) {
		try {
			return aClassReference.getSuperclass();
		} catch (NotFoundException e) {
			// If we can't access the superclass, it's not in the project
			// classpath (probably in the
			// JDK), and we don't care.
			// The one exception to this might be if you've added a testing
			// library to your JDK
			// ext/lib directory, but I'm not going to handle that case until we
			// know we need to.
			return null;
		}
	}

	private boolean isTestNGTestMethod(CtMethod ctMethod) {
		MethodInfo methodInfo = ctMethod.getMethodInfo2();
		List<?> attributes = methodInfo.getAttributes();
		for (Object attribute : attributes) {
			if (attribute instanceof AnnotationsAttribute) {
				AnnotationsAttribute annotations = (AnnotationsAttribute) attribute;
				for (Annotation each : annotations.getAnnotations()) {
					if (org.testng.annotations.Test.class.getName().equals(each.getTypeName())) {
						return true;
					}
				}
			}
		}

		return false;
	}

	private boolean isJUnit4TestMethod(CtMethod ctMethod) {
		MethodInfo methodInfo = ctMethod.getMethodInfo2();
		List<?> attributes = methodInfo.getAttributes();
		for (Object attribute : attributes) {
			if (attribute instanceof AnnotationsAttribute) {
				AnnotationsAttribute annotations = (AnnotationsAttribute) attribute;
				for (Annotation each : annotations.getAnnotations()) {
					if (org.junit.Test.class.getName().equals(each.getTypeName())) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public void setClassFile(File classFile) {
		this.classFile = classFile;
	}

	@Override
	public boolean locatedInClassFile() {
		return classFile != null;
	}

	@Override
	public File getClassFile() {
		return classFile;
	}
}
