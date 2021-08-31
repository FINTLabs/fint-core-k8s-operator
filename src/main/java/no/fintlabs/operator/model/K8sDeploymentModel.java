package no.fintlabs.operator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class K8sDeploymentModel {
    private String orgId;
    @Builder.Default
    private List<K8sComponentModel> components = new ArrayList<>();
}
