package no.fintlabs.operator.configuration.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceConfiguration extends AbstractNameTemplateConfiguration {
    private Integer port;
}
