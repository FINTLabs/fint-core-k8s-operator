package no.fintlabs.operator.configuration.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AbstractNameTemplateConfiguration {
    protected String nameTemplate;

    public String getName(String stack) {
        return String.format(nameTemplate, stack);
    }
}
