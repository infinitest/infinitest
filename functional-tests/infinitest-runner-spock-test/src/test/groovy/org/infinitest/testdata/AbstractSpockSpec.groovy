package org.infinitest.testdata

import spock.lang.Specification

abstract class AbstractSpockSpec extends Specification {

    void "should pass from abstract class"() {
        expect:
            1
    }
}