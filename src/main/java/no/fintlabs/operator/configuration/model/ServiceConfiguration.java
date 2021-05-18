package no.fintlabs.operator.configuration.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@Component
//@ConfigurationProperties(prefix = "fint.kubernetes.service")
public class ServiceConfiguration extends AbstractNameTemplateConfiguration {
    private Integer port;
}
