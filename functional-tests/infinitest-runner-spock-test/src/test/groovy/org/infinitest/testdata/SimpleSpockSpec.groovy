package org.infinitest.testdata

import spock.lang.Specification

class SimpleSpockSpec extends Specification {

    void "should be executed and pass"() {
        expect:
            1
    }

}
