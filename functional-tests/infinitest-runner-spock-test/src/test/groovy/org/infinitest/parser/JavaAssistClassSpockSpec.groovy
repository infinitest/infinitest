package org.infinitest.parser

import javassist.ClassPool
import javassist.NotFoundException
import org.infinitest.testdata.HierarchicSpockSpec
import org.infinitest.testdata.ParameterizedSpockSpec
import org.infinitest.testdata.SimpleSpockSpec
import spock.lang.Specification

class JavaAssistClassSpockSpec extends Specification {

    private ClassPool classPool

    void setup() {
        classPool = new ClassPool()
        classPool.appendSystemPath()
    }

    void "should detect Spock 2 test (#clazz.simpleName)"() {
        expect:
            classFor(clazz).isATest()
        where:
            clazz << [SimpleSpockSpec, HierarchicSpockSpec, ParameterizedSpockSpec]
    }

    private JavaAssistClass classFor(Class<?> testClass) {
        try {
            return new JavaAssistClass(classPool.get(testClass.getName()));
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
