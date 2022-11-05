package org.infinitest.parser

import javassist.ClassPool
import javassist.NotFoundException
import org.infinitest.testdata.HierarchicSpockSpec
import org.infinitest.testdata.ParameterizedSpockSpec
import org.infinitest.testdata.SimpleSpockSpec
import spock.lang.Specification

class JavaAssistClassSpockSpec extends Specification {

    private ClassPoolForFakeClassesTestUtil classPoolUtil

    void setup() {
        classPoolUtil = new ClassPoolForFakeClassesTestUtil()
    }

    void "should detect Spock 2 test (#clazz.simpleName)"() {
        expect:
            classFor(clazz).isATest()
        where:
            clazz << [SimpleSpockSpec, HierarchicSpockSpec, ParameterizedSpockSpec]
    }

    private JavaAssistClass classFor(Class<?> testClass) {
        try {
            ClassPool classPool = classPoolUtil.getClassPool()
            return new JavaAssistClass(classPool.get(testClass.getName()));
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
