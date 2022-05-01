This project is based on the [HAPI FHIR Tester Overlay app](https://github.com/hapifhir/hapi-fhir/tree/master/hapi-fhir-testpage-overlay) and additional content or modifications from the [JAP Starter project](https://github.com/hapifhir/hapi-fhir-jpaserver-starter)

The **Tester Overlay** is a web application for browsing a FHIR API. The **JAP Starter** project launches both the Tester and a FHIR API.

This project is meant to separate out the Tester project into an independently launchable  application, add or change some configuration, and possibly evolve it as needed.

Brief list of changes:
 * Copied source from Tester and JPA Starter.
   * Avoiding making changes to original content to help with future sync with upstream sources.
 * Setup as a Spring Boot application under the `com.essaid.hapi.webui` package.
 * For content under `webapp`, additional suffixed directories 