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

import static java.util.Arrays.stream;
import static javassist.Modifier.*;
import static javassist.bytecode.AnnotationsAttribute.*;
import static org.infinitest.parser.DescriptorParser.*;

import java.io.*;
import java.util.*;

import javassist.*;
import javassist.bytecode.*;
import javassist.bytecode.annotation.*;

import org.junit.runner.*;

import com.google.common.base.*;
import com.google.common.collect.*;

/**
 * Be careful: instances of this class are kept in a cache
 * so we should keep its footprint minimal.
 */
public class JavaAssistClass extends AbstractJavaClass {
	private final String[] imports;
	private final boolean isATest;
	private final String className;
	private File classFile;

	public JavaAssistClass(CtClass classReference) {
		imports = findImports(classReference);
		isATest = !isAbstract(classReference) &&
				hasTests(classReference) &&
				(isJUnit5TestClass(imports) || canInstantiate(classReference));
		className = classReference.getName();
	}

	private boolean isJUnit5TestClass(final String[] imports) {
		return stream(imports)
				.anyMatch(org.junit.jupiter.api.Test.class.getName()::equals);
	}

	@Override
	public String[] getImports() {
		return imports;
	}

	private String[] findImports(CtClass ctClass) {
		Set<String> imports = Sets.newHashSet();
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
		return new Predicate<CtClass>() {
			@Override
			public boolean apply(CtClass input) {
				return isAnnotatedWithCustomRunner(input);
			}
		};
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
			if (isJUnit5TestMethod(ctMethod) || isJUnit4TestMethod(ctMethod)) {
				return true;
			}
		}
		return false;
	}

    private boolean isJUnit5TestMethod(CtMethod ctMethod) {
        final List<?> attributes = ctMethod.getMethodInfo2().getAttributes();
        return attributes.stream()
                .filter(clazz -> clazz instanceof AnnotationsAttribute)
                .map(attribute -> (AnnotationsAttribute) attribute)
                .map(AnnotationsAttribute::getAnnotations)
                .flatMap(Arrays::stream)
                .map(Annotation::getTypeName)
                .anyMatch(org.junit.jupiter.api.Test.class.getName()::equals);
    }

	private boolean anySuperclassOf(CtClass classReference, Predicate<CtClass> predicate) {
		CtClass superclass = findSuperclass(classReference);
		while (superclass != null) {
			if (predicate.apply(superclass)) {
				return true;
			}
			superclass = findSuperclass(superclass);
		}
		return false;
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
