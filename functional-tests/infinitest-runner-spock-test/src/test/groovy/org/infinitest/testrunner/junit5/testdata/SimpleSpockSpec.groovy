package org.infinitest.testrunner.junit5.testdata

import spock.lang.Specification

class SimpleSpockSpec extends Specification {

    void "should be executed and pass"() {
        expect:
            GroovySystem.getShortVersion() == "4.0"
    }

}
