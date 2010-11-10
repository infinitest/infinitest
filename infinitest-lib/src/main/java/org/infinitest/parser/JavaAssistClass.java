package org.infinitest.parser;

import static com.google.common.collect.Sets.*;
import static javassist.Modifier.*;
import static javassist.bytecode.AnnotationsAttribute.*;
import static org.infinitest.parser.DescriptorParser.*;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;

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

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.base.Predicate;

public class JavaAssistClass extends AbstractJavaClass
{
    private final String className;
    private File classFile;
    private final Boolean isATest;
    private Collection<String> imports;

    public JavaAssistClass(CtClass classReference)
    {
        imports = findImports(classReference);
        isATest = !isAbstract(classReference) && isAJUnitTest(classReference) && canInstantiate(classReference);
        this.className = classReference.getName();
    }

    public Collection<String> getImports()
    {
        if (imports == null)
        {
            throw new DisposedClassException(getName());
        }
        return imports;
    }

    @Override
    public void dispose()
    {
        imports = null;
    }

    private Collection<String> findImports(CtClass ctClass)
    {
        Set<String> imports = newHashSet();
        addDependenciesFromConstantPool(ctClass, imports);
        addFieldDependencies(ctClass, imports);
        addClassAnnotationDependencies(ctClass, imports);
        addFieldAnnotationDependencies(ctClass, imports);
        addMethodAnnotationDependencies(ctClass, imports);
        return imports;
    }

    private void addFieldAnnotationDependencies(CtClass ctClass, Set<String> imports)
    {
        for (CtField field : ctClass.getDeclaredFields())
        {
            List<?> attributes = field.getFieldInfo2().getAttributes();
            addAnnotationsForAttributes(imports, attributes);
        }
    }

    private void addFieldDependencies(CtClass ctClass, Set<String> imports)
    {
        for (CtField field : ctClass.getDeclaredFields())
        {
            imports.add(parseClassNameFromConstantPoolDescriptor(field.getFieldInfo2().getDescriptor()));
        }
    }

    private void addMethodAnnotationDependencies(CtClass ctClass, Set<String> imports)
    {
        for (CtMethod ctMethod : ctClass.getDeclaredMethods())
        {
            MethodInfo methodInfo = ctMethod.getMethodInfo2();
            List<?> attributes = methodInfo.getAttributes();
            addAnnotationsForAttributes(imports, attributes);
            addParameterAnnotationsFor(imports, methodInfo, ParameterAnnotationsAttribute.visibleTag);
            addParameterAnnotationsFor(imports, methodInfo, ParameterAnnotationsAttribute.invisibleTag);
        }
    }

    private void addAnnotationsForAttributes(Set<String> imports, List<?> attributes)
    {
        for (Object each : attributes)
        {
            if (each instanceof AnnotationsAttribute)
            {
                addAnnotations(imports, (AnnotationsAttribute) each);
            }
        }
    }

    private void addParameterAnnotationsFor(Set<String> imports, MethodInfo methodInfo, String tag)
    {
        AttributeInfo attribute = methodInfo.getAttribute(tag);
        ParameterAnnotationsAttribute annotationAttribute = (ParameterAnnotationsAttribute) attribute;
        if (annotationAttribute != null)
        {
            Annotation[][] parameters = annotationAttribute.getAnnotations();
            for (Annotation[] annotations : parameters)
            {
                for (Annotation annotation : annotations)
                {
                    imports.add(annotation.getTypeName());
                }
            }
        }
    }

    private void addClassAnnotationDependencies(CtClass classReference, Set<String> imports)
    {
        addClassAnnotationsOfTagType(classReference, imports, visibleTag);
        addClassAnnotationsOfTagType(classReference, imports, invisibleTag);
    }

    private void addClassAnnotationsOfTagType(CtClass classRef, Set<String> imports, String tag)
    {
        addAnnotations(imports, getAnnotationsOfType(tag, classRef));
    }

    private AnnotationsAttribute getAnnotationsOfType(String tag, CtClass classRef)
    {
        return (AnnotationsAttribute) classRef.getClassFile2().getAttribute(tag);
    }

    private void addAnnotations(Set<String> imports, AnnotationsAttribute annotations)
    {
        if (annotations != null)
        {
            for (Annotation each : annotations.getAnnotations())
            {
                imports.add(each.getTypeName());
            }
        }
    }

    private void addDependenciesFromConstantPool(CtClass ctClass, Set<String> imports)
    {
        ConstPool constPool = ctClass.getClassFile2().getConstPool();
        Set<?> classNames = constPool.getClassNames();
        for (Object each : classNames)
        {
            imports.add(pathToClassName(each.toString()));
        }
    }

    private String pathToClassName(String classPath)
    {
        return classPath.replace('/', '.');
    }

    public String getName()
    {
        return className;
    }

    private boolean isAbstract(CtClass classReference)
    {
        return classReference.isInterface() || Modifier.isAbstract(classReference.getModifiers());
    }

    public boolean isATest()
    {
        return isATest;
    }

    boolean canInstantiate(CtClass classReference)
    {
        for (CtConstructor ctConstructor : classReference.getConstructors())
        {
            if (isValidConstructor(classReference, ctConstructor))
            {
                return true;
            }
        }
        return false;
    }

    private boolean isValidConstructor(CtClass classReference, CtConstructor ctConstructor)
    {
        if (usesCustomRunner(classReference))
        {
            return true;
        }
        return hasJUnitCompatibleConstructor(ctConstructor);
    }

    private boolean hasJUnitCompatibleConstructor(CtConstructor ctConstructor)
    {
        if (ctConstructor.isConstructor() && isPublic(ctConstructor.getModifiers()))
        {
            try
            {
                CtClass[] parameterTypes = ctConstructor.getParameterTypes();
                if (hasDefaultConstructor(parameterTypes))
                {
                    return true;
                }
                if (hasTestNameConstructor(parameterTypes))
                {
                    return true;
                }
            }
            catch (NotFoundException e)
            {
                return false;
            }
        }
        return false;
    }

    private boolean hasDefaultConstructor(CtClass[] parameterTypes)
    {
        return parameterTypes.length == 0;
    }

    private boolean hasTestNameConstructor(CtClass[] parameterTypes)
    {
        return parameterTypes.length == 1 && parameterTypes[0].getName().equals(String.class.getName());
    }

    @Override
    public String toString()
    {
        return getName();
    }

    private boolean isAJUnitTest(CtClass classReference)
    {
        return hasJUnitTestMethods(classReference) || usesCustomRunner(classReference);
    }

    private boolean usesCustomRunner(CtClass classReference)
    {
        return isAnnotatedWithCustomRunner(classReference) || anySuperclassOf(classReference, hasACustomRunner());
    }

    private boolean isAnnotatedWithCustomRunner(CtClass classReference)
    {
        AnnotationsAttribute annotations = getAnnotationsOfType(visibleTag, classReference);
        if (annotations != null)
        {
            for (Annotation annotation : annotations.getAnnotations())
            {
                if (annotation.getTypeName().equals(RunWith.class.getName()))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private Predicate<CtClass> hasACustomRunner()
    {
        return new Predicate<CtClass>()
        {
            public boolean apply(CtClass input)
            {
                return isAnnotatedWithCustomRunner(input);
            }
        };
    }

    private boolean hasJUnitTestMethods(CtClass classReference)
    {
        for (CtMethod ctMethod : classReference.getMethods())
        {
            if (isJUnit4TestMethod(ctMethod) || isJUnit3TestMethod(ctMethod))
            {
                return true;
            }
        }
        return false;
    }

    private boolean isJUnit3TestMethod(CtMethod ctMethod)
    {
        return ctMethod.getName().startsWith("test") && anySuperclassOf(ctMethod.getDeclaringClass(), isTestCase());
    }

    private boolean anySuperclassOf(CtClass classReference, Predicate<CtClass> predicate)
    {
        CtClass superclass = findSuperclass(classReference);
        while (superclass != null)
        {
            if (predicate.apply(superclass))
            {
                return true;
            }
            superclass = findSuperclass(superclass);
        }
        return false;
    }

    private Predicate<CtClass> isTestCase()
    {
        return new Predicate<CtClass>()
        {
            public boolean apply(CtClass input)
            {
                return input.getName().equals(TestCase.class.getName());
            }
        };
    }

    private CtClass findSuperclass(CtClass aClassReference)
    {
        try
        {
            return aClassReference.getSuperclass();
        }
        catch (NotFoundException e)
        {
            // If we can't access the superclass, it's not in the project classpath (probably in the
            // JDK), and we don't care.
            // The one exception to this might be if you've added a testing library to your JDK
            // ext/lib directory, but I'm not going to handle that case until we know we need to.
            return null;
        }
    }

    private boolean isJUnit4TestMethod(CtMethod ctMethod)
    {
        MethodInfo methodInfo = ctMethod.getMethodInfo2();
        List<?> attributes = methodInfo.getAttributes();
        for (Object attribute : attributes)
        {
            if (attribute instanceof AnnotationsAttribute)
            {
                AnnotationsAttribute annotations = (AnnotationsAttribute) attribute;
                for (Annotation each : annotations.getAnnotations())
                {
                    if (Test.class.getName().equals(each.getTypeName()))
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public void setClassFile(File classFile)
    {
        this.classFile = classFile;
    }

    public boolean locatedInClassFile()
    {
        return classFile != null;
    }

    public File getClassFile()
    {
        return classFile;
    }
}
