package no.fintlabs.operator.repository


import spock.lang.Specification

class RepositoryHelperSpec extends Specification {

    def "Converting memory limit to Xms should result in max 90% of limit"() {
        expect:
        RepositoryHelper.getXmx("32Gi") == "28G"

    }

    def 'Converting megabyte memory limit should also result in 90% of limit'() {
        expect:
        RepositoryHelper.getXmx('256Mi') == '230M'
    }
}
