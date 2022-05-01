package com.essaid.hapi.webui;

import ca.uhn.fhir.context.FhirVersionEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@ConfigurationProperties(prefix = "hapi.webui")
@Configuration
@EnableConfigurationProperties
public class AppProperties {
    private Map<String, Server> servers = null;


    public Map<String, Server> getServers() {
        return servers;
    }

    public void setServers(Map<String, Server> servers) {
        this.servers = servers;
    }

    public static class Server {

        private String name;
        private String server_address;
        private Boolean refuse_to_fetch_third_party_urls = true;
        private FhirVersionEnum fhir_version = FhirVersionEnum.R4;

        public FhirVersionEnum getFhir_version() {
            return fhir_version;
        }

        public void setFhir_version(FhirVersionEnum fhir_version) {
            this.fhir_version = fhir_version;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getServer_address() {
            return server_address;
        }

        public void setServer_address(String server_address) {
            this.server_address = server_address;
        }

        public Boolean getRefuse_to_fetch_third_party_urls() {
            return refuse_to_fetch_third_party_urls;
        }

        public void setRefuse_to_fetch_third_party_urls(Boolean refuse_to_fetch_third_party_urls) {
            this.refuse_to_fetch_third_party_urls = refuse_to_fetch_third_party_urls;
        }
    }
}
