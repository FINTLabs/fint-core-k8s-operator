package no.fintlabs.operator.configuration.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AbstractNameTemplateConfiguration {
    protected String nameTemplate;

    public String getName(String stack, String orgId) {
        return String.format(nameTemplate, stack, orgId.replace('.', '-'));
    }
}
