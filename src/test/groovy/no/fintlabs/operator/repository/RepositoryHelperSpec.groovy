package no.fintlabs.operator.repository


import spock.lang.Specification

class RepositoryHelperSpec extends Specification {

    def 'Can parse proper sizes as defined by k8s doc'() {
        expect:
        RepositoryHelper.parseSize('256Mi').toPlainString() == '268435456'
        RepositoryHelper.parseSize('2Gi').toPlainString() == '2147483648'
        RepositoryHelper.parseSize('2G').toPlainString() == '2000000000'
        RepositoryHelper.parseSize('1e6').toPlainString() == '1000000'
        RepositoryHelper.parseSize('2.88').toPlainString() == '2.88'
        RepositoryHelper.parseSize('2.88Gi').toPlainString() == '3092376453.12'
    }

    def 'Proper heap size calculations'() {
        expect:
        RepositoryHelper.getXmx("512Mi") == '204M'
        RepositoryHelper.getXmx("2Gi") == '1587M'
        RepositoryHelper.getXmx("18Gi") == '16332M'
    }
}
