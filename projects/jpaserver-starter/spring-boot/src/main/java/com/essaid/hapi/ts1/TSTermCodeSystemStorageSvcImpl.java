package com.essaid.hapi.ts1;

import ca.uhn.fhir.jpa.entity.TermCodeSystemVersion;
import ca.uhn.fhir.jpa.entity.TermConcept;
import ca.uhn.fhir.jpa.entity.TermConceptParentChildLink;
import ca.uhn.fhir.jpa.model.entity.ResourceTable;
import ca.uhn.fhir.jpa.term.TermCodeSystemStorageSvcImpl;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.api.server.storage.ResourcePersistentId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.Transactional;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TSTermCodeSystemStorageSvcImpl extends TermCodeSystemStorageSvcImpl {

	private static Logger logger = LoggerFactory.getLogger(TSTermCodeSystemStorageSvcImpl.class);

	@Override
	@Transactional
	public void storeNewCodeSystemVersion(ResourcePersistentId theCodeSystemResourcePid, String theSystemUri,
													  String theSystemName, String theCodeSystemVersionId,
													  TermCodeSystemVersion theCodeSystemVersion,
													  ResourceTable theCodeSystemResourceTable, RequestDetails theRequestDetails) {

		if(false) {
			//CodeSystem codeSystem = (CodeSystem) theRequestDetails.getResource();
			Context context = new Context(theCodeSystemVersion);
			theCodeSystemVersion.getConcepts().forEach(concept ->
				indexCoceptComponents(concept, null, context));

			context.codesInfo.values().forEach(codeInfo ->
				indexParentProperties(codeInfo, context));

			assertParents(context);
		}
		super.storeNewCodeSystemVersion(theCodeSystemResourcePid, theSystemUri, theSystemName, theCodeSystemVersionId,
			theCodeSystemVersion, theCodeSystemResourceTable, theRequestDetails);
	}

	/**
	 * Indexes the concepts, and captures any hierarchy asserted parenthood relationships. A concept can only have one
	 * parent from the asserted hierarchy.
	 *  @param parentConcept
	 * @param parentInfo
	 * @param context
	 */
	private void indexCoceptComponents(TermConcept parentConcept,
												  CInfo parentInfo, Context context) {
		String code = parentConcept.getCode();
		CInfo codeInfo = context.getCodesInfo().computeIfAbsent(code, s -> new CInfo(parentConcept));
		if (parentInfo != null) {
			codeInfo.setParentByHierarchy(parentInfo);
		}

		parentConcept.getChildren().stream().map(link -> link.getChild()).forEach(child -> indexCoceptComponents(child,
			codeInfo, context));
	}

	private void indexParentProperties(CInfo codeInfo, Context context) {
		String code = codeInfo.getConcept().getCode();

		codeInfo.getConcept().getProperties().stream()
			.filter(property -> property.getKey().equals("parent"))
			.forEach(property -> {
				String parentCode = property.getValue();
				CInfo parentInfo = context.getCodesInfo().get(parentCode);
				if (parentInfo == null) {
					logger.warn("Code {} declares parent code {} but parent is not found in CodeSystem.", code, parentCode);
				} else {
					codeInfo.getParentsByProperty().add(parentInfo);
				}
			});
	}

	private void assertParents(Context context) {
		Map<String, TermConcept> codeToTermConcpet =
			context.getMyCodeSystemVersion().getConcepts().stream().collect(Collectors.toMap(TermConcept::getCode,
				Function.identity()));

		context.getCodesInfo().values().stream().forEach(
			childInfo -> {
				childInfo.getParentsByProperty().forEach(
					parentInfo -> {
						TermConcept childTermConcept = codeToTermConcpet.get(childInfo.getConcept().getCode());
						final String parentCode = parentInfo.getConcept().getCode();

						if (!childTermConcept.getParents().stream().anyMatch(termConceptParentChildLink -> termConceptParentChildLink.getParent().getCode().equals(parentCode))) {
							TermConcept parentTermConcept = codeToTermConcpet.get(parentCode);
							TermConceptParentChildLink link = new TermConceptParentChildLink();
							link.setCodeSystem(context.getMyCodeSystemVersion());
							link.setParent(parentTermConcept);
							link.setChild(childTermConcept);
							link.setRelationshipType(TermConceptParentChildLink.RelationshipTypeEnum.ISA);
							childTermConcept.getParents().add(link);
							childTermConcept.setParentPids(null);
							parentTermConcept.getChildren().add(link);
						}
					});
			});
	}


	private static class Context {

		private final TermCodeSystemVersion myCodeSystemVersion;
		private Map<String, CInfo> codesInfo = new HashMap<>();

		public Context(TermCodeSystemVersion theCodeSystemVersion) {
			this.myCodeSystemVersion = theCodeSystemVersion;
		}

		public Map<String, CInfo> getCodesInfo() {
			return codesInfo;
		}

		public TermCodeSystemVersion getMyCodeSystemVersion() {
			return myCodeSystemVersion;
		}
	}

	private static class CInfo {

		private final TermConcept concept;
		private CInfo parentByHierarchy;
		private List<CInfo> parentsByProperty = new ArrayList<>();

		public CInfo(TermConcept concept) {
			this.concept = concept;
		}

		public TermConcept getConcept() {
			return concept;
		}

		public CInfo getParentByHierarchy() {
			return parentByHierarchy;
		}

		public void setParentByHierarchy(CInfo parentByHierarchy) {
			this.parentByHierarchy = parentByHierarchy;
		}

		public List<CInfo> getParentsByProperty() {
			return parentsByProperty;
		}

		public void setParentsByProperty(List<CInfo> parentsByProperty) {
			this.parentsByProperty = parentsByProperty;
		}
	}


}
