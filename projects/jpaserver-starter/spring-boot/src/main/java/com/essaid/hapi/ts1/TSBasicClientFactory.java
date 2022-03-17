package com.essaid.hapi.ts1;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import ca.uhn.fhir.rest.server.util.ITestingUiClientFactory;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class TSBasicClientFactory implements ITestingUiClientFactory {

  @Autowired
  TSProperties tsProperties;

  @Override
  public IGenericClient newClient(FhirContext theFhirContext, HttpServletRequest theRequest,
      String theServerBaseUrl) {

    IGenericClient client = theFhirContext.newRestfulGenericClient(theServerBaseUrl);
    if (tsProperties.isEnable_auth()) {
      SecurityContext context = SecurityContextHolder.getContext();
      Object userName = context.getAuthentication().getName();
      Object credentials = context.getAuthentication().getCredentials();

      client.registerInterceptor(
          new BasicAuthInterceptor(userName+":"+credentials));

//      String basicAuth = theRequest.getHeader("Authorization");
//      if (isNotBlank(basicAuth)) {
//        basicAuth = basicAuth.substring("Basic ".length());
//        client.registerInterceptor(
//            new BasicAuthInterceptor(new String(Base64.decodeBase64(basicAuth))));
//      }
    }
    return client;
  }
}
