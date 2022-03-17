package com.essaid.hapi.ts1;

import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.context.support.IValidationSupport;
import ca.uhn.fhir.jpa.api.config.DaoConfig;
import ca.uhn.fhir.jpa.api.dao.DaoRegistry;
import ca.uhn.fhir.jpa.api.dao.IFhirResourceDao;
import ca.uhn.fhir.jpa.api.dao.IFhirSystemDao;
import ca.uhn.fhir.jpa.provider.JpaCapabilityStatementProvider;
import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.rest.annotation.Metadata;
import ca.uhn.fhir.rest.api.CacheControlDirective;
import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.exceptions.NotImplementedOperationException;
import ca.uhn.fhir.rest.server.util.ISearchParamRegistry;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.hl7.fhir.instance.model.api.IBaseConformance;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.CapabilityStatement;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.Enumerations.PublicationStatus;
import org.hl7.fhir.r4.model.TerminologyCapabilities.CapabilityStatementKind;
import org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesCodeSystemComponent;
import org.hl7.fhir.r4.model.UriType;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class TSConformanceProvider extends JpaCapabilityStatementProvider {

  private final RestfulServer restfulServer;
  private static Logger logger = LoggerFactory.getLogger(TSConformanceProvider.class);

  @Autowired
  TSProperties tsProperties;

  @Autowired
  DaoRegistry daoRegistry;

  private final AtomicReference<IBaseConformance> serverCache = new AtomicReference<>();
  private final AtomicLong serverCacheExpires = new AtomicLong(0L);

  private final AtomicReference<IBaseConformance> terminologyCache = new AtomicReference<>();
  private final AtomicLong terminologyCacheExpires = new AtomicLong(0L);

  /**
   * Constructor
   *
   * @param theRestfulServer
   * @param theSystemDao
   * @param theDaoConfig
   * @param theSearchParamRegistry
   * @param theValidationSupport
   */
  public TSConformanceProvider(
      @NotNull RestfulServer theRestfulServer,
      @NotNull IFhirSystemDao<?, ?> theSystemDao,
      @NotNull DaoConfig theDaoConfig,
      @NotNull ISearchParamRegistry theSearchParamRegistry,
      IValidationSupport theValidationSupport) {
    super(theRestfulServer, theSystemDao, theDaoConfig, theSearchParamRegistry,
        theValidationSupport);
    this.restfulServer = theRestfulServer;
  }


  @Override
  @Metadata(cacheMillis = 0)
  public IBaseConformance getServerConformance(
      HttpServletRequest theRequest,
      RequestDetails theRequestDetails) {

    if (tsProperties.isEnable_terminology()) {
      String mode = "full";

      String[] modes = theRequestDetails.getParameters().get("mode");
      if (modes != null && modes.length > 0) {
        mode = modes[0];
      }

      if (mode.equals("terminology")) {
        return getTerminology(theRequest, theRequestDetails);
      } else {
        return getCapabilityStatement(theRequest, theRequestDetails);
      }
    } else {
      // not added terminology capabilities
      return getCapabilityStatement(theRequest, theRequestDetails);
    }

  }

  private IBaseConformance getCapabilityStatement(HttpServletRequest theRequest,
      RequestDetails theRequestDetails) {
    long expire = serverCacheExpires.get();
    long now = System.currentTimeMillis();
    CacheControlDirective cacheControlDirective = new CacheControlDirective().parse(
        theRequestDetails.getHeaders(
            Constants.HEADER_CACHE_CONTROL));

    if (expire < now || cacheControlDirective.isNoCache()) {
      serverCache.set(null);
    }

    if (serverCache.get() == null) {
      serverCache.set(createCapabilityStatement(theRequest, theRequestDetails));
      serverCacheExpires.set(now + tsProperties.getServer_conformance_cache_milliseconds());
    }

    return serverCache.get();
  }

  private IBaseConformance createCapabilityStatement(HttpServletRequest theRequest,
      RequestDetails theRequestDetails) {
    IBaseConformance serverConformance = super.getServerConformance(theRequest, theRequestDetails);

    if(!tsProperties.isEnable_terminology()){
      return  serverConformance;
    }

    FhirVersionEnum version = restfulServer.getFhirContext().getVersion().getVersion();

    if (version.equals(FhirVersionEnum.R4)) {
      CapabilityStatement cs = (CapabilityStatement) serverConformance;
      cs.addInstantiates("http://hl7.org/fhir/CapabilityStatement/terminology-server");
      List<String> supportedCodeSystemUrls = getSupportedCodeSystemUrls();
      supportedCodeSystemUrls.forEach(s -> cs.addExtension(
          "http://hl7.org/fhir/StructureDefinition/capabilitystatement-supported-system",
          new UriType(s)));
      return cs;
    } else if (version.equals(FhirVersionEnum.R5)) {
      org.hl7.fhir.r5.model.CapabilityStatement cs = (org.hl7.fhir.r5.model.CapabilityStatement) serverConformance;
      cs.addInstantiates("http://hl7.org/fhir/CapabilityStatement/terminology-server");
      List<String> supportedCodeSystemUrls = getSupportedCodeSystemUrls();
      supportedCodeSystemUrls.forEach(s -> cs.addExtension(
          "http://hl7.org/fhir/StructureDefinition/capabilitystatement-supported-system",
          new org.hl7.fhir.r5.model.UriType(s)));
      return cs;
    } else {
      throw new NotImplementedOperationException("Only supporting R4 and R5 for now.");
    }
  }

  private IBaseConformance getTerminology(HttpServletRequest theRequest,
      RequestDetails theRequestDetails) {

    long expire = terminologyCacheExpires.get();
    long now = System.currentTimeMillis();
    CacheControlDirective cacheControlDirective = new CacheControlDirective().parse(
        theRequestDetails.getHeaders(
            Constants.HEADER_CACHE_CONTROL));

    if (expire < now || cacheControlDirective.isNoCache()) {
      terminologyCache.set(null);
    }

    if (terminologyCache.get() == null) {
      terminologyCache.set(createTerminologyCapabilities(theRequest, theRequestDetails));
      terminologyCacheExpires.set(now + tsProperties.getServer_conformance_cache_milliseconds());
    }

    return terminologyCache.get();
  }

  private IBaseConformance createTerminologyCapabilities(HttpServletRequest theRequest,
      RequestDetails theRequestDetails) {
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

    FhirVersionEnum version = restfulServer.getFhirContext().getVersion().getVersion();

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
