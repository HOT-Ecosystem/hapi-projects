package ca.uhn.fhir.jpa.starter;

import ca.uhn.fhir.context.support.IValidationSupport;
import ca.uhn.fhir.jpa.provider.JpaCapabilityStatementProvider;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.api.server.storage.IReindexJobSubmitter;
import ca.uhn.fhir.rest.server.method.ConformanceMethodBinding;
import ca.uhn.fhir.rest.server.provider.ReindexProvider;
import com.essaid.hapi.ts1.TSConformanceProvider;
import com.essaid.hapi.ts1.TSBasicAuthInterceptor;
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import javax.servlet.ServletException;

@Import({AppProperties.class})
public class JpaRestfulServer extends BaseJpaRestfulServer {

	@Autowired
	AppProperties appProperties;

	@Autowired
	private ValidationSupportChain validationChain;

	@Autowired
	private IValidationSupport myValidationSupport;

	@Autowired
	private IReindexJobSubmitter reindexJobSubmitter;

	private static final long serialVersionUID = 1L;

	public JpaRestfulServer() {
		super();
	}

	@Override
	protected void initialize() throws ServletException {
		super.initialize();

		registerProvider(new ReindexProvider(getFhirContext(), reindexJobSubmitter));;

		setDefaultResponseEncoding(EncodingEnum.JSON);

		// replace the base capability provider
		JpaCapabilityStatementProvider confProvider = new TSConformanceProvider(this, fhirSystemDao,
				daoConfig, searchParamRegistry, myValidationSupport);
		confProvider.setImplementationDescription("HAPI FHIR R4 Server");
		myApplicationContext.getAutowireCapableBeanFactory().autowireBean(confProvider);
		setServerConformanceProvider(confProvider);


//		CSInterceptor csInterceptor = new CSInterceptor();
//		myApplicationContext.getAutowireCapableBeanFactory().autowireBean(csInterceptor);
//		this.getInterceptorService().registerInterceptor(csInterceptor);

		TSBasicAuthInterceptor authInterceptor = new TSBasicAuthInterceptor();
		myApplicationContext.getAutowireCapableBeanFactory().autowireBean(authInterceptor);
		getInterceptorService().registerInterceptor(authInterceptor);

		//validationChain.addValidationSupport(new RemoteTerminologyServiceValidationSupport(getFhirContext()));
	}

	@Override
	public ConformanceMethodBinding getServerConformanceMethod() {
		return super.getServerConformanceMethod();
	}


}
