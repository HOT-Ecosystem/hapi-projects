package com.essaid.hapi.ts1.attic;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.interceptor.api.Hook;
import ca.uhn.fhir.interceptor.api.Interceptor;
import ca.uhn.fhir.interceptor.api.Pointcut;
import ca.uhn.fhir.jpa.api.dao.DaoRegistry;
import ca.uhn.fhir.jpa.api.dao.IFhirResourceDao;
import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.servlet.ServletRequestDetails;
import com.essaid.hapi.ts1.TSProperties;
import com.essaid.hapi.ts1.TSTerminologyCapbilities;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.hl7.fhir.instance.model.api.IBaseConformance;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.CapabilityStatement;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.Enumerations.PublicationStatus;
import org.hl7.fhir.r4.model.TerminologyCapabilities.CapabilityStatementKind;
import org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesCodeSystemComponent;
import org.hl7.fhir.r4.model.UriType;
import org.springframework.beans.factory.annotation.Autowired;

@Interceptor
public class CSInterceptor {

  @Autowired
  DaoRegistry daoRegistry;

  @Autowired
  TSProperties tsProperties;

  @Autowired
  FhirContext fhirContext;

  @Hook(Pointcut.SERVER_CAPABILITY_STATEMENT_GENERATED)
  public IBaseConformance tsCS(IBaseConformance iBaseConformance, RequestDetails requestDetails,
      ServletRequestDetails servletRequestDetails) {

    if (!tsProperties.isEnable_terminology()) {
      return null;
    }

    if ("metadata".equals(requestDetails.getRequestPath())) {

      return doMetadata(iBaseConformance, requestDetails);
    }
    return null;
  }

  private IBaseConformance doMetadata(IBaseConformance iBaseConformance,
      RequestDetails requestDetails) {
    // "terminology" and "normative"
    //https://hl7.org/fhir/R4/http.html#capabilities

    String mode = "full";
    String[] modes = requestDetails.getParameters().get("mode");
    if (modes != null && modes.length > 0) {
      mode = modes[0];
    }
    if (mode.equals("terminology")) {
      return doTerminology();
    } else {
      return doCapability(iBaseConformance);
    }
  }

  private IBaseConformance doCapability(IBaseConformance iBaseConformance) {

    CapabilityStatement cs = (CapabilityStatement) iBaseConformance;
    cs.addInstantiates("http://hl7.org/fhir/CapabilityStatement/terminology-server");
    List<String> supportedCodeSystemUrls = getSupportedCodeSystemUrls();
    supportedCodeSystemUrls.forEach(s -> cs.addExtension(
        "http://hl7.org/fhir/StructureDefinition/capabilitystatement-supported-system",
        new UriType(s)));
    return null;
  }

  private IBaseConformance doTerminology() {

    TSTerminologyCapbilities tsTerminologyCapbilities = new TSTerminologyCapbilities();
    tsTerminologyCapbilities.setDate(new Date());
    tsTerminologyCapbilities.setStatus(PublicationStatus.ACTIVE);
    tsTerminologyCapbilities.setKind(CapabilityStatementKind.INSTANCE);
    List<String> supportedCodeSystemUrls = getSupportedCodeSystemUrls();
    supportedCodeSystemUrls.forEach(s -> {
      TerminologyCapabilitiesCodeSystemComponent tccs = new TerminologyCapabilitiesCodeSystemComponent();
      tccs.setUri(s);
      tsTerminologyCapbilities.addCodeSystem(tccs);
    });
    return tsTerminologyCapbilities;
  }

  /**
   * Supported CSs are tagged so but keep the following in mind:
   * <ul>
   *   <li>
   *     http://hl7.org/implement/standards/fhir/resource.html
   *   </li>
   *   <li>
   *     https://www.hl7.org/fhir/resource-operation-meta-delete.html
   *   </li>
   *   <li>
   *     https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#tags
   *   </li>
   * </ul>
   * <p>
   *
   * @return
   */

  private List<String> getSupportedCodeSystemUrls() {
    IFhirResourceDao codeSystemDao = daoRegistry.getResourceDao("CodeSystem");

    TokenParam tagParam = new TokenParam();
    tagParam.setSystem(
        "http://hl7.org/fhir/StructureDefinition/capabilitystatement-supported-system");
    tagParam.setValue("true");

    TokenAndListParam tokenAndListParam = new TokenAndListParam();
    tokenAndListParam.addAnd(tagParam);

    SearchParameterMap searchParameterMap = new SearchParameterMap();
    searchParameterMap.add(ca.uhn.fhir.rest.api.Constants.PARAM_TAG, tokenAndListParam);
    IBundleProvider enabledCodeSystemsBundle = codeSystemDao.search(searchParameterMap);
    List<IBaseResource> allResources = enabledCodeSystemsBundle.getAllResources();

    FhirVersionEnum version = fhirContext.getVersion().getVersion();

    if (version.equals(FhirVersionEnum.R4)) {
      return allResources.stream()
          .map(iBaseResource -> CodeSystem.class.cast(iBaseResource).getUrl())
          .collect(Collectors.toList());
    } else if (version.equals(FhirVersionEnum.R5)) {
      return allResources.stream()
          .map(iBaseResource -> org.hl7.fhir.r5.model.CodeSystem.class.cast(iBaseResource).getUrl())
          .collect(Collectors.toList());
    }

    return Collections.emptyList();
  }

}
