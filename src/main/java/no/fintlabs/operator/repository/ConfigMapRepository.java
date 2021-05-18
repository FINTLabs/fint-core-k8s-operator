package no.fintlabs.operator.repository;

import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import no.fintlabs.operator.configuration.AppConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Repository
public class ConfigMapRepository {

    private final KubernetesClient client;
    private final ApplicationContext context;
    private final AppConfiguration configuration;


    public ConfigMapRepository(KubernetesClient client, ApplicationContext context, AppConfiguration configuration) {
        this.client = client;
        this.context = context;
        this.configuration = configuration;
    }

    public void applyCoreEnvironmentConfig(String namespace) {
        Resource resource = context.getResource(configuration.getEnvironmentUrl());
        try {
            InputStream is = resource.getInputStream();

            client
                    .load(is)
                    .inNamespace(namespace)
                    .createOrReplace();

        } catch (IOException e) {
            log.error("Unable to download core environment yaml file from GitHub: {}", e.getMessage());
        }
    }
}
