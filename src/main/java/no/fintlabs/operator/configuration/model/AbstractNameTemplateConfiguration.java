package no.fintlabs.operator.configuration.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AbstractNameTemplateConfiguration {
    protected String nameTemplate;

    public String getName(String orgId, String component) {
        return String.format(nameTemplate, component, orgId.replace(".", "-"));
    }
}
