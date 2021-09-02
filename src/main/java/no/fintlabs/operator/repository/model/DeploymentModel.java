package no.fintlabs.operator.repository.model;

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
public class DeploymentModel {
    private String orgId;
    @Builder.Default
    private List<ComponentModel> components = new ArrayList<>();
}
