package com.essaid.hapi.webui;

import ca.uhn.fhir.to.FhirTesterMvcConfig;
import ca.uhn.fhir.to.TesterConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(FhirTesterMvcConfig.class)
public class WebUiConfig {

    /**
     * This bean tells the testing webpage which servers it should configure itself
     * to communicate with. In this example we configure it to talk to the local
     * server, as well as one public server. If you are creating a project to
     * deploy somewhere else, you might choose to only put your own server's
     * address here.
     *
     * Note the use of the ${serverBase} variable below. This will be replaced with
     * the base URL as reported by the server itself. Often for a simple Tomcat
     * (or other container) installation, this will end up being something
     * like "http://localhost:8080/hapi-fhir-jpaserver-starter". If you are
     * deploying your server to a place with a fully qualified domain name,
     * you might want to use that instead of using the variable.
     */
    @Bean
    public TesterConfig testerConfig(AppProperties appProperties) {
        TesterConfig retVal = new TesterConfig();
        appProperties.getServers().entrySet().stream().forEach(t -> {
            retVal
                    .addServer()
                    .withId(t.getKey())
                    .withFhirVersion(t.getValue().getFhir_version())
                    .withBaseUrl(t.getValue().getServer_address())
                    .withName(t.getValue().getName());
            retVal.setRefuseToFetchThirdPartyUrls(
                    t.getValue().getRefuse_to_fetch_third_party_urls());

        });
        return retVal;
    }
}
