package org.infinitest.testrunner.junit5

import org.infinitest.testdata.HierarchicSpockSpec
import org.infinitest.testrunner.DefaultRunner
import org.infinitest.testrunner.TestResults
import org.infinitest.testdata.ParameterizedSpockSpec
import org.infinitest.testdata.SimpleSpockSpec
import spock.lang.Specification;

class RunnerJunit5SpockSpec extends Specification {

    void "should detect Spock 2 tests (#clazz.simpleName)"() {
        expect:
            Junit5Runner.isJUnit5Test(clazz)
        where:
            clazz << [SimpleSpockSpec, HierarchicSpockSpec, ParameterizedSpockSpec]
    }

    void "should execute Spock 2 test (#clazz.simpleName)"() {
        given:
            def runner = new DefaultRunner()
        when:
            TestResults results = runner.runTest(clazz.name)
        then:
            results.methodStats.size() == expectedMethodStatsSize
        where:
            clazz                  || expectedMethodStatsSize
            SimpleSpockSpec        || 1
            HierarchicSpockSpec    || 2
            ParameterizedSpockSpec || 3
    }

}
