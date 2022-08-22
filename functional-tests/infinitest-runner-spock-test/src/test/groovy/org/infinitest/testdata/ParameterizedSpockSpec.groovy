package org.infinitest.testdata

import spock.lang.Specification

class ParameterizedSpockSpec extends Specification {

    void "should verify parametrized tests execution"() {
        expect:
            number > 0
        where:
            number << [1, 2]
    }

}
