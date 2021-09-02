
package no.fintlabs.operator.repository.model;

import java.util.List;

import lombok.Data;

@Data
public class Component {
    private List<String> adapters;
    private String basePath;
    private List<String> clients;
    private Boolean common;
    private Boolean core;
    private String description;
    private String dn;
    private Boolean inBeta;
    private Boolean inPlayWithFint;
    private Boolean inProduction;
    private String name;
    private Boolean openData;
    private List<String> organisations;
    private Object port;
}
