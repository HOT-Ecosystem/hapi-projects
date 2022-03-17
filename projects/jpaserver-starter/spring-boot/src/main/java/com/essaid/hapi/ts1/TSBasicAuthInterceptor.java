package com.essaid.hapi.ts1;

import ca.uhn.fhir.i18n.Msg;
import ca.uhn.fhir.interceptor.api.Hook;
import ca.uhn.fhir.interceptor.api.Pointcut;
import ca.uhn.fhir.rest.api.RequestTypeEnum;
import ca.uhn.fhir.rest.api.RestOperationTypeEnum;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.exceptions.AuthenticationException;
import ca.uhn.fhir.rest.server.exceptions.ForbiddenOperationException;
import ca.uhn.fhir.rest.server.servlet.ServletRequestDetails;
import com.essaid.hapi.ts1.TSProperties;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;

public class TSBasicAuthInterceptor {

  private static String WRITER = "WRITER";
  private static String READER = "READER";

  private static Set<String> READ_PERATIONS = new HashSet<>(
      Arrays.asList("$validate-code", "$expand", "$lookup", "$validate-code", "$subsumes",
          "$find-matches", "$meta"));


  @Autowired
  TSProperties tsProperties;

  @Hook(Pointcut.SERVER_INCOMING_REQUEST_POST_PROCESSED)
  public boolean authenticate(ServletRequestDetails theRequestDetails) {

    if (tsProperties.isEnable_auth()) {
      if (!isRead(theRequestDetails) && !theRequestDetails.getServletRequest()
          .isUserInRole(WRITER)) {
        ForbiddenOperationException e2 = new ForbiddenOperationException(
            "You do not have the necessary permissions to perform this operation.");
        throw e2;
      }
    }
    return true;
  }

  private boolean isRead(ServletRequestDetails theRequestDetails) {
    RestOperationTypeEnum restOperationType = theRequestDetails.getRestOperationType();
    switch (restOperationType) {
      case READ:
      case VREAD:
      case SEARCH_SYSTEM:
      case SEARCH_TYPE:
      case GET_TAGS:
      case GET_PAGE:
      case HISTORY_INSTANCE:
      case HISTORY_SYSTEM:
      case HISTORY_TYPE:
      case METADATA:
        return true;
      case EXTENDED_OPERATION_INSTANCE:
      case EXTENDED_OPERATION_SERVER:
      case EXTENDED_OPERATION_TYPE:
        String operation = theRequestDetails.getOperation();
        return READ_PERATIONS.contains(operation);
    }
    return false;
  }
}
