package no.fintlabs.operator.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComponentModel {
    private String componentName;
    private String componentPath;
    private String componentImage;
    private ComponentSizes.Size size;
    private List<String> cacheDisabledFor;

    public String getComponentName() {
        return componentName.replace("_", "-");
    }
}
