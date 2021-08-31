package no.fintlabs.operator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class K8sComponentModel {
    private String componentName;
    private String componentPath;
    private String componentImage;
    private ComponentSizes.Size size;

    public String getComponentName() {
        return componentName.replace("_", "-");
    }
}
