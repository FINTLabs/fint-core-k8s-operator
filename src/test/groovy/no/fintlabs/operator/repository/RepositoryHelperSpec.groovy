package no.fintlabs.operator.repository

import io.fabric8.kubernetes.api.model.Quantity
import spock.lang.Specification

class RepositoryHelperSpec extends Specification {

    def "Converting memory limit to Xms should result in max 90% of limit"() {
        when:
        def xmx = RepositoryHelper.getXmx(new Quantity("32Gi"))
        then:
        xmx == "28"
    }
}
