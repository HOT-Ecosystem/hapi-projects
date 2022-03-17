package com.essaid.hapi.ts1;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.starter.JpaRestfulServer;
import ca.uhn.fhir.jpa.term.api.ITermCodeSystemStorageSvc;
import ca.uhn.fhir.rest.server.RestfulServer;
import javax.servlet.Servlet;
import org.apache.lucene.search.BooleanQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({TSProperties.class, TSSecurityConfig.class})
public class TSConfiguration {

	/**
	 * For parent hierarchy
	 * @return
	 */
	@Bean
	public ITermCodeSystemStorageSvc termCodeSystemStorageSvc() {
		return new TSTermCodeSystemStorageSvcImpl();
	}

}
